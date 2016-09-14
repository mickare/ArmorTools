package de.mickare.armortools.permission;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface PermissionCheck {

  public boolean canModify(Player player, ArmorStand armorstand);

  default boolean canModify(Player player, Entity entity) {
    return canBuild(player, entity.getLocation());
  }

  boolean canBuild(Player player, Location location);

}
