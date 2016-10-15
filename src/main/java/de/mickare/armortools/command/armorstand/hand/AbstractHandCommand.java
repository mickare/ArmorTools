package de.mickare.armortools.command.armorstand.hand;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;

public abstract class AbstractHandCommand extends AbstractModifyCommand1 {

  public AbstractHandCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractHandCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  protected abstract ItemStack getArmorHandItem(ArmorStand armor);

  protected abstract void setArmorHandItem(ArmorStand armor, ItemStack item);

  protected abstract void sendItemSwitchedMessage(Player player);

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {

      final ItemStack itemInHand = player.getEquipment().getItemInMainHand();

      if (player.getGameMode() == GameMode.CREATIVE) {
        return ModifyAction.area(area, armor -> {
          setArmorHandItem(armor, itemInHand);
          return true;
        });
      }

      if (itemInHand != null && itemInHand.getAmount() > 0) {

        final AtomicInteger amount = new AtomicInteger(itemInHand.getAmount());
        return ModifyAction.area(area, armor -> {

          if (amount.decrementAndGet() >= 0) {
            ItemStack i = new ItemStack(itemInHand);
            i.setAmount(1);
            if (getArmorHandItem(armor) != null) {
              player.getInventory().addItem(getArmorHandItem(armor));
            }
            setArmorHandItem(armor, i);
            return true;
          }

          return false;
        }).setFinish(() -> {

          if (amount.get() > 0) {
            itemInHand.setAmount(amount.get());
            player.getEquipment().setItemInMainHand(itemInHand);
          } else {
            player.getEquipment().setItemInMainHand(itemInHand);
          }

        });

      } else {
        return ModifyAction.area(area, armor -> {
          if (getArmorHandItem(armor) != null) {
            player.getInventory().addItem(getArmorHandItem(armor));
          }
          setArmorHandItem(armor, null);
          return true;
        });
      }



    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      if (player.getGameMode() == GameMode.CREATIVE) {
        return ModifyAction.click(armor -> {
          setArmorHandItem(armor, player.getEquipment().getItemInMainHand());
          sendItemSwitchedMessage(player);
          return true;
        });
      }

      return ModifyAction.click(armor -> {
        ItemStack a_item = getArmorHandItem(armor);
        ItemStack p_item = player.getEquipment().getItemInMainHand();

        if (p_item == null || p_item.getType() == Material.AIR || p_item.getAmount() <= 1) {
          player.getEquipment().setItemInMainHand(a_item);
          setArmorHandItem(armor, p_item);
        } else {

          ItemStack item = p_item.clone();
          item.setAmount(1);
          setArmorHandItem(armor, item);

          p_item.setAmount(p_item.getAmount() - 1);
          player.getEquipment().setItemInMainHand(p_item);
          if (a_item != null) {
            player.getInventory().addItem(a_item);
          }
        }
        sendItemSwitchedMessage(player);
        return true;
      });

    }

  }


  public static class HandCommand extends AbstractHandCommand {

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
