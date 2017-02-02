package de.mickare.armortools.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {


  public static int removeItemFromPlayer(Player player, ItemStack item, int amount) {
    if (item != null && item.getType() != Material.AIR) {

      int removed = 0;

      // Try to remove first from hand
      ItemStack newHandItem = null;
      if (amount < item.getAmount()) {
        newHandItem = new ItemStack(item);
        newHandItem.setAmount(item.getAmount() - amount);
        removed += amount;
      } else {
        removed += item.getAmount();
      }
      player.getEquipment().setItemInMainHand(newHandItem);

      // Then from inventory
      if (removed < amount) {
        ItemStack removeItems = new ItemStack(item);
        final int missing = amount - removed;
        removeItems.setAmount(missing);
        removed += missing - player.getInventory().removeItem(removeItems).values().stream()
            .mapToInt(ItemStack::getAmount).findAny().orElse(0);
      }

      return removed;
    }

    return amount;
  }


  public static ItemStack singletonItem(ItemStack old) {
    if (old == null) {
      return null;
    }
    ItemStack item = new ItemStack(old);
    item.setAmount(1);
    return item;
  }

  public static boolean isSimilar(ItemStack a, ItemStack b) {
    return (a == null && b == null) || (a != null && a.isSimilar(b));
  }

}
