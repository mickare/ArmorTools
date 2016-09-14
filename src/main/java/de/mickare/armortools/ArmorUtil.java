package de.mickare.armortools;



import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.ArmorStand;

import com.google.common.base.Preconditions;

import de.mickare.armortools.reflect.BukkitReflect;

public class ArmorUtil {

  public static final String NBT_TAG_SITTABLE = "Sittable";
  // public static final String NBT_TAG_Owner = "Owner";

  private static Field ArmorStand_DisabledSlots = null;

  private static Class<?> entityArmorStand = null;
  private static Method setMarker = null;
  private static Method isMarker = null;

  public static Byte byteOf(boolean value) {
    return value ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
  }

  /*
   * public static Object getDataWatcher(ArmorStand armorstand) throws Exception { return
   * WatcherReflect.getDataWatcher(BukkitReflect.getHandle(armorstand)); }
   */

  public static Class<?> getEntityArmorStandClass() throws ClassNotFoundException {
    if (entityArmorStand == null) {
      entityArmorStand = BukkitReflect.getNMSClass("EntityArmorStand");
    }
    return entityArmorStand;
  }

  private static int getDisabledSlots(Object handle) throws Exception {
    Preconditions.checkNotNull(handle);
    if (ArmorStand_DisabledSlots == null) {
      ArmorStand_DisabledSlots =
          BukkitReflect.getNMSClass("EntityArmorStand").getDeclaredField("bB");
      ArmorStand_DisabledSlots.setAccessible(true);
    }
    return ArmorStand_DisabledSlots.getInt(handle);
  }

  private static void setDisabledSlots(Object handle, int value) throws Exception {
    Preconditions.checkNotNull(handle);
    if (ArmorStand_DisabledSlots == null) {
      ArmorStand_DisabledSlots =
          BukkitReflect.getNMSClass("EntityArmorStand").getDeclaredField("bA");
      ArmorStand_DisabledSlots.setAccessible(true);
    }
    ArmorStand_DisabledSlots.setInt(handle, value);
  }

  public static boolean isSittable(ArmorStand armor) {
    if (!armor.isCustomNameVisible() && armor.getCustomName() != null) {
      return armor.getCustomName().equals(NBT_TAG_SITTABLE);
    }
    return false;
    /*
     * try { Preconditions.checkNotNull(armor); NBTTagWrapper tag = new NBTTagWrapper(armor);
     * Optional<Boolean> sittable = tag.getBooleanIfPresent(NBT_TAG_SITTABLE); return
     * sittable.orElse(false); } catch (Exception e) { throw new RuntimeException(e); }
     */
  }

  public static void setSittable(ArmorStand armor, boolean sittable) {
    if (sittable) {
      armor.setCustomNameVisible(false);
      armor.setCustomName(NBT_TAG_SITTABLE);
    } else {
      armor.setCustomName(null);
    }
    /*
     * try { Preconditions.checkNotNull(armor); NBTTagWrapper tag = new NBTTagWrapper(armor);
     * tag.setBoolean(NBT_TAG_SITTABLE, sittable); } catch (Exception e) { throw new
     * RuntimeException(e); }
     */
  }

  /*
   * public static boolean hasOwner(ArmorStand armor) { try { Preconditions.checkNotNull(armor);
   * NBTTagWrapper tag = new NBTTagWrapper(armor); Optional<String> owner =
   * tag.getStringIfPresent(NBT_TAG_Owner); if (owner.isPresent()) { return true; } return false; }
   * catch (Exception e) { throw new RuntimeException(e); } }
   * 
   * public static UUID getOwner(ArmorStand armor) { try { Preconditions.checkNotNull(armor);
   * NBTTagWrapper tag = new NBTTagWrapper(armor); Optional<String> owner =
   * tag.getStringIfPresent(NBT_TAG_Owner); if (owner.isPresent()) { return
   * UUID.fromString(owner.get()); } return null; } catch (Exception e) { throw new
   * RuntimeException(e); } }
   * 
   * public static void setOwner(ArmorStand armor, UUID owner) { try {
   * Preconditions.checkNotNull(armor); Preconditions.checkNotNull(owner); NBTTagWrapper tag = new
   * NBTTagWrapper(armor); tag.setString(NBT_TAG_Owner, owner.toString()); } catch (Exception e) {
   * throw new RuntimeException(e); } }
   */


  public static void setProtected(ArmorStand armor, boolean protect, DisabledPart part) {
    try {
      Object handle = BukkitReflect.getHandle(armor);
      int disabledSlots = getDisabledSlots(handle);
      disabledSlots = part.apply(disabledSlots, protect);
      setDisabledSlots(handle, disabledSlots);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setMarker(ArmorStand armor, boolean marker) {
    try {
      if (setMarker == null) {
        setMarker = getEntityArmorStandClass().getMethod("setMarker", boolean.class);
      }
      setMarker.invoke(BukkitReflect.getHandle(armor), marker);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public static boolean isMarker(ArmorStand armor) {
    try {
      if (isMarker == null) {
        isMarker = getEntityArmorStandClass().getMethod("isMarker");
      }
      return (boolean) isMarker.invoke(BukkitReflect.getHandle(armor));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


}
