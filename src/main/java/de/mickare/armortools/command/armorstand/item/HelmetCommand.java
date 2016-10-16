package de.mickare.armortools.command.armorstand.item;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction.Type;

public class HelmetCommand extends AbstractItemCommand {

  public HelmetCommand(ArmorToolsPlugin plugin) {
    super(plugin, "helmet", "helmet [area]", Out.CMD_HELMET);
    this.addPermission(Permissions.HELMET);
  }

  @Override
  protected ItemStack getItem(ArmorStand armor) {
    return armor.getHelmet();
  }



  @Override
  protected void setItem(ArmorStand armor, ItemStack item) {
    armor.setHelmet(item);
  }



  @Override
  protected void sendItemSwitchedMessage(Player player) {
    Out.CMD_HELMET_SWITCHED.send(player);
  }

  @Override
  protected Type getModifyActionType() {
    return Type.HELMET;
  }

}
