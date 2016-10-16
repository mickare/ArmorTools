package de.mickare.armortools.command.armorstand.item;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public abstract class AbstractHandCommand extends AbstractItemCommand {

  public AbstractHandCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractHandCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  public static class HandCommand extends AbstractHandCommand {

    public HandCommand(ArmorToolsPlugin plugin) {
      super(plugin, "hand", "hand [area]", Out.CMD_HAND);
      this.addPermission(Permissions.HAND);
    }

    @Override
    protected ItemStack getItem(ArmorStand armor) {
      return armor.getEquipment().getItemInMainHand();
    }

    @Override
    protected void setItem(ArmorStand armor, ItemStack item) {
      armor.getEquipment().setItemInMainHand(item);
    }

    @Override
    protected void sendItemSwitchedMessage(Player player) {
      Out.CMD_HAND_SWITCHED.send(player);
    }

  }

  public static class OffHandCommand extends AbstractHandCommand {

    public OffHandCommand(ArmorToolsPlugin plugin) {
      super(plugin, "offhand", "offhand [area]", Out.CMD_HAND);
      this.addPermission(Permissions.OFFHAND);
    }

    @Override
    protected ItemStack getItem(ArmorStand armor) {
      return armor.getEquipment().getItemInOffHand();
    }

    @Override
    protected void setItem(ArmorStand armor, ItemStack item) {
      armor.getEquipment().setItemInOffHand(item);
    }

    @Override
    protected void sendItemSwitchedMessage(Player player) {
      Out.CMD_OFFHAND_SWITCHED.send(player);
    }

  }


}
