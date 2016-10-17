package de.mickare.armortools.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction;
import de.mickare.armortools.command.armorstand.step.MoveCommand.MoveStepAction;
import de.mickare.armortools.command.armorstand.step.RotateCommand.RotateStepAction;
import de.mickare.armortools.event.ArmorMoveEvent.EndArmorMoveEvent;
import de.mickare.armortools.event.ArmorRotateEvent.EndArmorRotateEvent;
import de.mickare.armortools.event.ArmorstandModifyEvent.PostArmorstandModifyEvent;

public class ArmorEventFactory {


  public static ArmorstandModifyEvent callPreModifyEvent(Player player, ModifyAction action,
      ArmorStand entity) {
    return callPreModifyEvent(player, action, Collections.singleton(entity));
  }

  public static ArmorstandModifyEvent callPreModifyEvent(Player player, ModifyAction action,
      Collection<ArmorStand> entities) {
    ArmorstandModifyEvent event = new ArmorstandModifyEvent(player, action, entities);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }

  public static PostArmorstandModifyEvent callPostModifyEvent(ArmorstandModifyEvent old,
      int count) {
    PostArmorstandModifyEvent event = new PostArmorstandModifyEvent(old, count);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }


  public static ArmorMoveEvent callMoveEvent(Player player, MoveStepAction action, Vector moved,
      Map<ArmorStand, Location> targetLocations) {
    ArmorMoveEvent event = new ArmorMoveEvent(player, action, moved, targetLocations);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }

  public static EndArmorMoveEvent callEndMoveEvent(ArmorMoveEvent old) {
    EndArmorMoveEvent event = new EndArmorMoveEvent(old);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }

  public static ArmorRotateEvent callRotateEvent(Player player, RotateStepAction action,
      Map<ArmorStand, Location> targetLocations) {
    ArmorRotateEvent event = new ArmorRotateEvent(player, action, targetLocations);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }

  public static EndArmorRotateEvent callEndRotateEvent(ArmorRotateEvent old) {
    EndArmorRotateEvent event = new EndArmorRotateEvent(old);
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }
}
