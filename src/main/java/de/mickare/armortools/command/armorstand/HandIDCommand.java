package de.mickare.armortools.command.armorstand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import net.md_5.bungee.api.ChatColor;

public class HandIDCommand extends AbstractModifyCommand2 {

  public HandIDCommand(ArmorToolsPlugin plugin) {
    super(plugin, "handid", "handid [id] [area]", Out.CMD_HAND_ID);
    this.addPermission(Permissions.HAND_ID);
  }

  @Override
  protected ModifyAction parseAction(Player player, String arg0, int area) {

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

      return ModifyAction.area(area, a -> {
        a.setItemInHand(item);
        return true;
      });


    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        a.setItemInHand(item);
        Out.CMD_HAND_SWITCHED.send(player);
        return true;
      });

    }

  }
}
