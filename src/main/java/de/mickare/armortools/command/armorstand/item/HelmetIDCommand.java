package de.mickare.armortools.command.armorstand.item;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction.Type;

public class HelmetIDCommand extends AbstractItemIDCommand {

  public HelmetIDCommand(ArmorToolsPlugin plugin) {
    super(plugin, "helmetid", "helmetid [id] [area]", Out.CMD_HELMET_ID);
    this.addPermission(Permissions.HELMET_ID);
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
