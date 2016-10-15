package de.mickare.armortools.command.armorstand.hand;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand2;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractHandIDCommand extends AbstractModifyCommand2 {


  public AbstractHandIDCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractHandIDCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  protected abstract ItemStack getArmorHandItem(ArmorStand armor);

  protected abstract void setArmorHandItem(ArmorStand armor, ItemStack item);

  protected abstract void sendItemSwitchedMessage(Player player);
  
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
        setArmorHandItem(a, item);
        return true;
      });


    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        setArmorHandItem(a, item);
        sendItemSwitchedMessage(player);
        return true;
      });

    }

  }



  public static class HandIDCommand extends AbstractHandIDCommand {

    public HandIDCommand(ArmorToolsPlugin plugin) {
      super(plugin, "handid", "handid [id] [area]", Out.CMD_HAND_ID);
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

    @Override
    protected void sendItemSwitchedMessage(Player player) {
      Out.CMD_HAND_SWITCHED.send(player);      
    }
  }

  public static class OffHandIDCommand extends AbstractHandIDCommand {

    public OffHandIDCommand(ArmorToolsPlugin plugin) {
      super(plugin, "offhandid", "offhandid [id] [area]", Out.CMD_OFFHAND_ID);
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

    @Override
    protected void sendItemSwitchedMessage(Player player) {
      Out.CMD_OFFHAND_SWITCHED.send(player);      
    }
  }

}
