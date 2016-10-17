package de.mickare.armortools.command.armorstand.item;

import java.util.Collection;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;
import de.mickare.armortools.util.InventoryUtils;

public abstract class AbstractItemCommand extends AbstractModifyCommand1 {

  public AbstractItemCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractItemCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  protected abstract ItemStack getItem(ArmorStand armor);

  protected abstract void setItem(ArmorStand armor, ItemStack item);

  protected abstract void sendItemSwitchedMessage(Player player);

  private int executeAction(Player player, ModifyAction action, Collection<ArmorStand> armorstands) {
    if (armorstands.size() == 0) {
      return 0;
    }

    ItemStack handItem = player.getEquipment().getItemInMainHand();

    if (player.getGameMode() == GameMode.CREATIVE) {
      armorstands.forEach(armor -> setItem(armor, handItem));
      sendItemSwitchedMessage(player);
      return armorstands.size();
    }

    int removedItems = InventoryUtils.removeItemFromPlayer(player, handItem, armorstands.size());
    int armorstandsChanged = 0;

    for (ArmorStand armor : armorstands) {
      if (armorstandsChanged >= removedItems) {
        break;
      }
      ItemStack oldItem = getItem(armor);
      if (oldItem != null) {
        ItemStack currentInHand = player.getEquipment().getItemInMainHand();
        if (currentInHand == null || currentInHand.getType() == Material.AIR) {
          player.getEquipment().setItemInMainHand(oldItem);
        } else {
          player.getInventory().addItem(oldItem);
        }
      }
      setItem(armor, handItem);
      armorstandsChanged++;
    }

    sendItemSwitchedMessage(player);
    return armorstandsChanged;
  }

  protected abstract ModifyAction.Type getModifyActionType();

  @Override
  protected ModifyAction createAction(Player player, int area) {

    if (area > 0) {

      return ModifyAction.area(getModifyActionType(), area, (action, armorstands) -> {
        return executeAction(player, action, armorstands);
      });

    } else {

      return ModifyAction.click(getModifyActionType(), (action, armorstands) -> {
        return executeAction(player, action, armorstands);
      });

    }

  }

}
