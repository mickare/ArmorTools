package de.mickare.armortools;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;

import lombok.Getter;

public class ArmorToolsMenu {

  @Getter
  public static enum Slot {

    HELMET(0, ArmorStand::setHelmet, ArmorStand::getHelmet), //
    CHESTPLATE(9, ArmorStand::setChestplate, ArmorStand::getChestplate), //
    LEGGINGS(18, ArmorStand::setLeggings, ArmorStand::getLeggings), //
    BOOTS(27, ArmorStand::setBoots, ArmorStand::getBoots), //
    ITEM_IN_HAND(36, ArmorStand::setItemInHand, ArmorStand::getItemInHand), //
    TOGGLE_ARMS(9), //
    TOGGLE_PLATE(10), //
    TOGGLE_VISIBILITY(11), //
    TOGGLE_GRAVITY(12), //
    SET_NAME(13), //
    ;

    private final int id;
    private final BiConsumer<ArmorStand, ItemStack> setter;
    private final Function<ArmorStand, ItemStack> getter;

    private Slot(int id) {
      this(id, null, null);
    }

    private Slot(int id, BiConsumer<ArmorStand, ItemStack> setter,
        Function<ArmorStand, ItemStack> getter) {
      if (setter != null || getter != null) {
        Preconditions.checkArgument(setter != null && getter != null);
      }
      this.id = id;
      this.setter = setter;
      this.getter = getter;
    }

    public boolean isArmorStandModifier() {
      return setter != null;
    }

    public Slot getSlot(Inventory inventory, int slot) {
      Preconditions.checkArgument(inventory.getType() == InventoryType.PLAYER);
      return null;
    }

    private final static ImmutableBiMap<Integer, Slot> SLOTS_BY_ID;

    static {
      ImmutableBiMap.Builder<Integer, Slot> b = ImmutableBiMap.builder();
      for (Slot slot : Slot.values()) {
        b.put(slot.id, slot);
      }
      SLOTS_BY_ID = b.build();
    }

    public static Slot getBySlotId(int id) {
      return SLOTS_BY_ID.get(id);
    }

  }

  @Getter
  private final ArmorStand armorstand;

  @Getter
  private final Inventory inventory;

  public ArmorToolsMenu(ArmorStand armorstand) {
    Preconditions.checkNotNull(armorstand);
    this.armorstand = armorstand;

    // Need to create a chest inventory as menu.
    // Minecraft does not handle other Player Inventories than the player's own.
    inventory = Bukkit.createInventory(null, 5 * 9, "ArmorStand Menu");
  }

  @SuppressWarnings("unused")
  private void renderInventory() {
    inventory.setItem(1, new ItemStack(Material.STAINED_GLASS_PANE, 8));
    inventory.setItem(10, new ItemStack(Material.STAINED_GLASS_PANE, 8));
    inventory.setItem(19, new ItemStack(Material.STAINED_GLASS_PANE, 8));
    inventory.setItem(28, new ItemStack(Material.STAINED_GLASS_PANE, 8));
    inventory.setItem(37, new ItemStack(Material.STAINED_GLASS_PANE, 8));

    inventory.setItem(Slot.HELMET.id, getItem(Slot.HELMET));
    inventory.setItem(Slot.CHESTPLATE.id, getItem(Slot.CHESTPLATE));
    inventory.setItem(Slot.LEGGINGS.id, getItem(Slot.LEGGINGS));
    inventory.setItem(Slot.BOOTS.id, getItem(Slot.BOOTS));
    inventory.setItem(Slot.ITEM_IN_HAND.id, getItem(Slot.ITEM_IN_HAND));

  }

  public ItemStack getItem(Slot slot) {
    if (slot.isArmorStandModifier()) {
      return slot.getter.apply(armorstand);
    }
    return null;
  }

  public ItemStack setItem(Slot slot, ItemStack item) {
    ItemStack old = getItem(slot);
    if (slot.isArmorStandModifier()) {
      slot.setter.accept(armorstand, item);
    }
    return old;
  }

  @SuppressWarnings("unused")
  public void handle(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    final Player player = (Player) event.getWhoClicked();

    boolean isPrimaryInv = event.getInventory().equals(this.inventory);
    if (isPrimaryInv) {
      Slot slot = Slot.getBySlotId(event.getSlot());
      if (slot == null) {
        event.setCancelled(true);
        return;
      }

      if (slot.isArmorStandModifier()) {


      }



    }
    if (!isPrimaryInv) {
      switch (event.getAction()) {
        case COLLECT_TO_CURSOR:
          event.setCancelled(true);
          break;
        default:
      }
    }
  }

  @SuppressWarnings("unused")
  public void handleItemEvent(Slot slot, final Player player, InventoryClickEvent event) {
    Preconditions.checkArgument(slot.isArmorStandModifier());
    ItemStack item = getItem(slot);
    event.setCurrentItem(item);
    Inventory inv = event.getInventory();

    // TODO

  }

  public void handleActionEvent(Slot slot, Player player, InventoryClickEvent event) {
    Preconditions.checkArgument(!slot.isArmorStandModifier());

    // TODO
  }

  public void handle(InventoryDragEvent event) {

  }

}
