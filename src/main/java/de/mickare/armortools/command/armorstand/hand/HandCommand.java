package de.mickare.armortools.command.armorstand.hand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class HandCommand extends AbstractHandCommand {

  public HandCommand(ArmorToolsPlugin plugin) {
    super(plugin, "hand", "hand [area]", Out.CMD_HAND);
    this.addPermission(Permissions.HAND);
  }

  @Override
  protected ItemStack getArmorHandItem(ArmorStand armor) {
    return armor.getEquipment().getItemInMainHand();
  }

  @Override
  protected void setArmorHandItem(ArmorStand armor, ItemStack item) {
    armor.getEquipment().setItemInMainHand(item);
  }

}
