package de.mickare.armortools;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import lombok.Getter;

public class StepManager implements Listener {

  private static @Getter StepManager instance = null;

  /**
   * Calculates a step delta from the previous slot to the next slot.
   *
   * @param previousSlot
   * @param newSlot
   * @return step delta -4 to 4, 0 means no difference
   */
  public static int stepDelta(int previousSlot, int newSlot) {
    int pmod = (previousSlot + 5) % 9;
    int nmod = newSlot;
    return (((nmod - pmod) + 9) % 9) - 4;
  }

  private final Cache<Player, StepAction> actions = CacheBuilder.newBuilder().weakKeys()//
      .removalListener(new RemovalListener<Player, StepAction>() {
        @Override
        public void onRemoval(RemovalNotification<Player, StepAction> notify) {
          if (notify.getCause() != RemovalCause.REPLACED && notify.getKey().isOnline()) {
            Out.STEP_ACTION_CANCELLED.send(notify.getKey());
          }
        }
      })//
      .expireAfterAccess(1, TimeUnit.MINUTES).build();

  public void invalidateMove(Player player) {
    actions.invalidate(player);
  }

  public void putMove(Player player, StepAction action) {
    actions.put(player, action);
  }

  private final JavaPlugin plugin;

  public StepManager(JavaPlugin plugin) {
    Preconditions.checkNotNull(plugin);
    this.plugin = plugin;
    instance = this;
  }

  @EventHandler
  public void onItemHeld(final PlayerItemHeldEvent e) {
    final Player player = e.getPlayer();
    final StepAction action = actions.getIfPresent(player);
    if (action != null) {
      final int preslot = e.getPreviousSlot();
      final int newslot = e.getNewSlot();
      final int step = stepDelta(preslot, newslot);

      try {
        if (!action.move(this, player, step)) {
          this.invalidateMove(player);
        }
      } catch (Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to execute step action: " + ex.getMessage(),
            ex);
        Out.ERROR_INTERNAL.send(player);
      }

    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInteract(PlayerInteractEvent e) {
    actions.invalidate(e.getPlayer());
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onConsume(PlayerItemConsumeEvent e) {
    actions.invalidate(e.getPlayer());
  }

  @EventHandler
  public void on(PlayerQuitEvent e) {
    actions.invalidate(e.getPlayer());
  }

  @EventHandler
  public void on(PlayerTeleportEvent e) {
    actions.invalidate(e.getPlayer());
  }

  @EventHandler
  public void on(AsyncPlayerChatEvent e) {
    actions.invalidate(e.getPlayer());
  }

  @EventHandler
  public void on(PlayerCommandPreprocessEvent e) {
    actions.invalidate(e.getPlayer());
  }

}
