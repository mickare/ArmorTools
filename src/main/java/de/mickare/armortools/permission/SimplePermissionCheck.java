package de.mickare.armortools.permission;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import de.mickare.armortools.Permissions;

public class SimplePermissionCheck implements PermissionCheck {

  @Override
  public boolean canModify(Player player, ArmorStand armorstand) {
    return Permissions.MODIFY.checkPermission(player);
  }

  @Override
  public boolean canBuild(Player player, Location location) {
    return true;
  }

}
