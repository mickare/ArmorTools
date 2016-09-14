package de.mickare.armortools.util;

import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;

public class BlockUtil {


  public static boolean isStairs(BaseBlock block) {
    return isStairs(block.getId());
  }

  public static boolean isStairs(int blockId) {
    switch (blockId) {
      case BlockID.OAK_WOOD_STAIRS:
      case BlockID.COBBLESTONE_STAIRS:
      case BlockID.BRICK_STAIRS:
      case BlockID.STONE_BRICK_STAIRS:
      case BlockID.NETHER_BRICK_STAIRS:
      case BlockID.SANDSTONE_STAIRS:
      case BlockID.SPRUCE_WOOD_STAIRS:
      case BlockID.BIRCH_WOOD_STAIRS:
      case BlockID.JUNGLE_WOOD_STAIRS:
      case BlockID.QUARTZ_STAIRS:
      case BlockID.ACACIA_STAIRS:
      case BlockID.DARK_OAK_STAIRS:
        return true;
      default:
        return false;
    }
  }

  public static boolean isStep(BaseBlock block) {
    return isStep(block.getId());
  }

  public static boolean isStep(int blockId) {
    switch (blockId) {
      case BlockID.STEP:
      case BlockID.WOODEN_STEP:
        return true;
      default:
        return false;
    }
  }

  public static boolean isLog(BaseBlock block) {
    return isLog(block.getId());
  }

  public static boolean isLog(int blockId) {
    switch (blockId) {
      case BlockID.LOG:
      case BlockID.LOG2:
        return true;
      default:
        return false;
    }
  }

  @SuppressWarnings("deprecation")
  public static ItemStack toHandItem(BaseBlock block) {
    int data = block.getData();
    switch (block.getId()) {
      case BlockID.AIR:
        return null;
      case BlockID.STONE:
        data %= 7;
        break;
      case BlockID.DIRT:
        data %= 2;
        break;
      case BlockID.CLOTH:
      case BlockID.CARPET:
      case BlockID.STAINED_CLAY:
      case BlockID.STAINED_GLASS:
      case BlockID.STAINED_GLASS_PANE:
        data %= 16;
        break;
      case BlockID.LEAVES:
      case BlockID.LOG:
        data %= 4;
        break;
      case BlockID.LEAVES2:
      case BlockID.LOG2:
        data %= 2;
        break;
      case BlockID.STEP:
        data %= 8;
        break;
      case BlockID.WOODEN_STEP:
        data %= 8;
        data %= 5; // last 3 are same as 0
        break;
      default:
        data = 0;
    }

    return new ItemStack(block.getId(), 1, (short) data);
  }

  @SuppressWarnings("deprecation")
  public static ItemStack toHelmetItem(BaseBlock block) {
    if (block.getId() == BlockID.DOUBLE_STEP) {
      int id = 1;
      int data = 0;
      switch (block.getData() % 10) {
        case 0:
        case 8:
          id = BlockID.STONE;
          data = 6;
          break;
        case 1:
          id = BlockID.SANDSTONE;
          break;
        case 2:
          id = BlockID.WOOD;
          break;
        case 3:
          id = BlockID.COBBLESTONE;
          break;
        case 4:
          id = BlockID.BRICK;
          break;
        case 5:
          id = BlockID.STONE_BRICK;
          break;
        case 6:
          id = BlockID.NETHER_BRICK;
          break;
        case 7:
          id = BlockID.QUARTZ_BLOCK;
          break;
        case 9:
          id = BlockID.SANDSTONE;
          data = 2;
          break;
        default:
      }
      return new ItemStack(id, 1, (short) data);
    } else if (block.getId() == BlockID.DOUBLE_WOODEN_STEP) {
      int id = 5;
      int data = block.getData() % 8;
      return new ItemStack(id, 1, (short) data);
    }

    return toHandItem(block);
  }



}
