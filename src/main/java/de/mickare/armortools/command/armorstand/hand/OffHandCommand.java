package de.mickare.armortools.command.armorstand.hand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class OffHandCommand extends AbstractHandCommand {

  public OffHandCommand(ArmorToolsPlugin plugin) {
    super(plugin, "offhand", "offhand [area]", Out.CMD_HAND);
    this.addPermission(Permissions.OFFHAND);
  }

  @Override
  protected ItemStack getArmorHandItem(ArmorStand armor) {
    return armor.getEquipment().getItemInOffHand();
  }

  @Override
  protected void setArmorHandItem(ArmorStand armor, ItemStack item) {
    armor.getEquipment().setItemInOffHand(item);
  }

}
