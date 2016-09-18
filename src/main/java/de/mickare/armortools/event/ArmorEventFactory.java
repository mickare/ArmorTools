package de.mickare.armortools.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class ArmorEventFactory {

  public static boolean callModifyEvent(Player player, ArmorStand armorstand) {
    ArmorstandModifyEvent event = new ArmorstandModifyEvent(player, armorstand);
    Bukkit.getPluginManager().callEvent(event);
    return !event.isCancelled();
  }

}
