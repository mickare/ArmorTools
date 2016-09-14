package de.mickare.armortools.command.armorstand;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class HandCommand extends AbstractModifyCommand1 {

  public HandCommand(ArmorToolsPlugin plugin) {
    super(plugin, "hand", "hand [area]", Out.CMD_HAND);
    this.addPermission(Permissions.HAND);
  }

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {

      final ItemStack itemInHand = player.getItemInHand();

      if (player.getGameMode() == GameMode.CREATIVE) {
        return ModifyAction.area(area, a -> {
          a.setItemInHand(itemInHand);
          return true;
        });
      }

      if (itemInHand != null && itemInHand.getAmount() > 0) {

        final AtomicInteger amount = new AtomicInteger(itemInHand.getAmount());
        return ModifyAction.area(area, a -> {

          if (amount.decrementAndGet() >= 0) {
            ItemStack i = new ItemStack(itemInHand);
            i.setAmount(1);
            if (a.getItemInHand() != null) {
              player.getInventory().addItem(a.getItemInHand());
            }
            a.setItemInHand(i);
            return true;
          }

          return false;
        }).setFinish(() -> {

          if (amount.get() > 0) {
            itemInHand.setAmount(amount.get());
            player.setItemInHand(itemInHand);
          } else {
            player.setItemInHand(null);
          }

        });

      } else {
        return ModifyAction.area(area, a -> {
          if (a.getItemInHand() != null) {
            player.getInventory().addItem(a.getItemInHand());
          }
          a.setItemInHand(null);
          return true;
        });
      }



    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      if (player.getGameMode() == GameMode.CREATIVE) {
        return ModifyAction.click(a -> {
          a.setItemInHand(player.getItemInHand());
          Out.CMD_HAND_SWITCHED.send(player);
          return true;
        });
      }

      return ModifyAction.click(a -> {
        ItemStack a_item = a.getItemInHand();
        ItemStack p_item = player.getItemInHand();

        if (p_item == null || p_item.getType() == Material.AIR || p_item.getAmount() <= 1) {
          player.setItemInHand(a_item);
          a.setItemInHand(p_item);

        } else {

          ItemStack item = p_item.clone();
          item.setAmount(1);
          a.setItemInHand(item);

          p_item.setAmount(p_item.getAmount() - 1);
          player.setItemInHand(p_item);
          if (a_item != null) {
            player.getInventory().addItem(a_item);
          }
        }

        Out.CMD_HAND_SWITCHED.send(player);

        return true;
      });

    }

  }
}
