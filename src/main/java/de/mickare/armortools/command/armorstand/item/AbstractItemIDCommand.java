package de.mickare.armortools.command.armorstand.item;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand2;
import de.mickare.armortools.util.InventoryUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractItemIDCommand extends AbstractModifyCommand2 {

  public AbstractItemIDCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractItemIDCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  protected abstract ItemStack getItem(ArmorStand armor);

  protected abstract void setItem(ArmorStand armor, ItemStack item);

  protected abstract void sendItemSwitchedMessage(Player player);

  private int executeAction(Player player, ModifyAction action, Set<ArmorStand> armorstands,
      ItemStack item) {
    if (armorstands.size() == 0) {
      return 0;
    }

    if (player.getGameMode() == GameMode.CREATIVE) {
      armorstands.forEach(armor -> setItem(armor, item));
      sendItemSwitchedMessage(player);
      return armorstands.size();
    }

    int removedItems = InventoryUtils.removeItemFromPlayer(player, item, armorstands.size());
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
      setItem(armor, item);
      armorstandsChanged++;
    }

    sendItemSwitchedMessage(player);
    return armorstandsChanged;
  }

  @Override
  protected ModifyAction createAction(Player player, String arg0, int area) {

    if (arg0 == null) {
      Out.ARG_MISSING.send(player);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }

    int id = 0;
    short sub_id = 0;
    String[] ids = arg0.split(":");
    if (ids.length > 2) {
      Out.ARG_INVALID.send(player, arg0);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }
    try {
      id = Integer.parseInt(ids[0]);
    } catch (NumberFormatException nfe) {
      Out.ARG_INVALID_INT_ONLY.send(player, "id", arg0);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }

    if (id < 0) {
      Out.ARG_INVALID_INT_MIN.send(player, "id", area, 0);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }

    if (ids.length == 2) {
      try {
        sub_id = Short.parseShort(ids[1]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "sub-id", arg0);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return null;
      }

    }

    final ItemStack item;
    if (id > 0) {
      @SuppressWarnings("deprecation")
      Material mat = Material.getMaterial(id);
      if (mat == null) {
        Out.ARG_INVALID_NO_MATERIAL.send(player, id);
        return null;
      }
      item = new ItemStack(mat, 1, sub_id);
    } else {
      item = null;
    }



    if (area > 0) {

      return ModifyAction.area(area, (action, armorstands) -> {
        return executeAction(player, action, armorstands, item);
      });

    } else {

      return ModifyAction.click((action, armorstands) -> {
        return executeAction(player, action, armorstands, item);
      });

    }

  }

}
