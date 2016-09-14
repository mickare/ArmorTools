package de.mickare.armortools;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class ClickManager implements Listener {

  private static ClickManager instance = null;

  public static ClickManager getInstance() {
    return instance;
  }

  public static final long EXPIRE_AFTER_WRITE_DURATION = 1;
  public static final TimeUnit EXPIRE_AFTER_WRITE_TIMEUNIT = TimeUnit.MINUTES;

  // <PlayerName, ClickAction>
  private final Cache<Player, ClickAction<?>> privateActions = CacheBuilder.newBuilder().weakKeys()//
      .expireAfterWrite(EXPIRE_AFTER_WRITE_DURATION, EXPIRE_AFTER_WRITE_TIMEUNIT)//
      .removalListener(new RemovalListener<Player, ClickAction<?>>() {
        @Override
        public void onRemoval(RemovalNotification<Player, ClickAction<?>> notify) {
          if (notify.getCause() != RemovalCause.REPLACED && !notify.getValue().isDone()
              && notify.getKey().isOnline()) {
            Out.CLICK_ACTION_CANCELLED.send(notify.getKey());
          }
        }
      }).build();

  private final JavaPlugin plugin;

  public ClickManager(JavaPlugin plugin) {
    Preconditions.checkNotNull(plugin);
    this.plugin = plugin;
    instance = this;
  }
  
  public void addAction(Player player, ClickAction<?> action) {
    ClickAction<?> old = this.privateActions.getIfPresent(player);
    if (old != null) {
      old.setDone(true);
    }
    privateActions.put(player, action);
  }

  public void removeAction(Player player) {
    privateActions.invalidate(player);
  }

  public boolean execute(Player player, Entity entity) {
    ClickAction<?> ca = this.privateActions.getIfPresent(player);
    if (ca != null) {
      ca.setDone(true);
      removeAction(player);
      try {
        if (!ca.castExecute(player, entity)) {
          Out.CLICK_ACTION_CANCELLED.send(player);
        }
      } catch (Exception e) {
        plugin.getLogger().log(Level.SEVERE, "Failed to execute click action: " + e.getMessage(),
            e);
        Out.ERROR_INTERNAL.send(player);
      }
      return true;
    }
    return false;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerInteract(PlayerInteractAtEntityEvent event) {

    if (event.getRightClicked() != null) {
      if (this.execute(event.getPlayer(), event.getRightClicked())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    removeAction(event.getPlayer());
  }

  @EventHandler
  public void on(PlayerTeleportEvent event) {
    removeAction(event.getPlayer());
  }

  @EventHandler
  public void on(PlayerQuitEvent e) {
    removeAction(e.getPlayer());
  }

  @EventHandler
  public void on(PlayerCommandPreprocessEvent event) {
    removeAction(event.getPlayer());
  }

  @EventHandler
  public void on(AsyncPlayerChatEvent e) {
    removeAction(e.getPlayer());
  }
}
