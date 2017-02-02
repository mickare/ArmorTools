package de.mickare.armortools.command.armorstand.item;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction.Type;
import de.mickare.armortools.permission.PermissionAnd;

public abstract class AbstractHandIDCommand extends AbstractItemIDCommand {


  public AbstractHandIDCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractHandIDCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  public static class HandIDCommand extends AbstractHandIDCommand {

    public HandIDCommand(ArmorToolsPlugin plugin) {
      super(plugin, "handid", "handid [id] [area]", Out.CMD_HAND_ID);
      this.addPermission(new PermissionAnd(Permissions.HAND, Permissions.HAND_ID));
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

    @Override
    protected Type getModifyActionType() {
      return Type.HAND;
    }
  }

  public static class OffHandIDCommand extends AbstractHandIDCommand {

    public OffHandIDCommand(ArmorToolsPlugin plugin) {
      super(plugin, "offhandid", "offhandid [id] [area]", Out.CMD_OFFHAND_ID);
      this.addPermission(new PermissionAnd(Permissions.OFFHAND, Permissions.HAND_ID));
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

    @Override
    protected Type getModifyActionType() {
      return Type.OFFHAND;
    }
  }

}
