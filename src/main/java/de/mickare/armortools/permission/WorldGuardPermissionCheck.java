package de.mickare.armortools.permission;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;

import de.mickare.armortools.Permissions;

public class WorldGuardPermissionCheck implements PermissionCheck {

  @Override
  public boolean canModify(Player player, ArmorStand armorstand) {
    return Permissions.MODIFY.checkPermission(player) && canBuild(player, armorstand.getLocation());
  }

  @Override
  public boolean canBuild(Player player, Location location) {
    return WGBukkit.getPlugin().canBuild(player, location);
  }

}
