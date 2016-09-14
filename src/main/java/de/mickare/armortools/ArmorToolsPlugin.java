package de.mickare.armortools;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.mickare.armortools.command.MainArmorCommand;
import de.mickare.armortools.permission.PermissionCheck;
import de.mickare.armortools.permission.SimplePermissionCheck;
import de.mickare.armortools.permission.WorldGuardPermissionCheck;
import lombok.Getter;

public class ArmorToolsPlugin extends JavaPlugin implements Listener {

  public static int MAX_AREA = 30;

  private PermissionCheck permissionCheck;

  private @Getter WorldEditPlugin worldEdit;

  @Override
  public void onLoad() {
    new ClickManager(this);
    new StepManager(this);
  }

  @Override
  public void onDisable() {

    getLogger().info("ArmorStand Plugin disabled!");
  }

  @Override
  public void onEnable() {

    Locale locale = Locale.getDefault();
    ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);
    Out.setResource(bundle);

    Plugin worldguard = Bukkit.getPluginManager().getPlugin("WorldGuard");
    if (worldguard != null) {
      this.permissionCheck = new WorldGuardPermissionCheck();
    } else {
      this.permissionCheck = new SimplePermissionCheck();
    }

    worldEdit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");

    new MainArmorCommand(this).register();

    Bukkit.getPluginManager().registerEvents(ClickManager.getInstance(), this);
    Bukkit.getPluginManager().registerEvents(StepManager.getInstance(), this);
    Bukkit.getPluginManager().registerEvents(this, this);

    getLogger().info("ArmorStand Plugin enabled!");
  }

  public boolean canModify(Player player, ArmorStand armorstand) {
    return permissionCheck.canModify(player, armorstand);
  }

  public boolean canModify(Player player, Entity entity) {
    return permissionCheck.canModify(player, entity);
  }

  public boolean canBuild(Player player, Location location) {
    return permissionCheck.canBuild(player, location);
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractAtEntityEvent event) {
    if (event.getRightClicked() != null
        && event.getRightClicked().getType() == EntityType.ARMOR_STAND
        && !event.getPlayer().isSneaking()) {
      final ArmorStand armorstand = (ArmorStand) event.getRightClicked();
      final Player player = event.getPlayer();
      if (ArmorUtil.isSittable(armorstand)) {
        armorstand.eject();
        new BukkitRunnable() {
          @Override
          public void run() {
            armorstand.eject();
            armorstand.setPassenger(player);
          }
        }.runTask(this);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void ejectPlayerFromArmorstand(PlayerQuitEvent event) {
    // If not ejected the armorstand will vanish like the player!

    final Player player = event.getPlayer();

    if (player.isInsideVehicle()) {
      Entity e = player.getVehicle();

      while (e != null && e.getPassenger() != player) {
        if (!(e instanceof ArmorStand)) {
          return;
        }
        e = e.getPassenger();
      }

      if (e != null) {
        player.eject();
        e.eject();
      }
    }

  }

  @EventHandler
  public void onArrowHit(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Arrow && event.getEntity() instanceof ArmorStand) {
      event.setCancelled(true);
      event.getDamager().remove();
    }
  }

}
