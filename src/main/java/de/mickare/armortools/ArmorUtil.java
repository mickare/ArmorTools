package de.mickare.armortools;



import java.lang.reflect.Method;

import org.bukkit.entity.ArmorStand;

import de.mickare.armortools.reflect.BukkitReflect;
import de.mickare.armortools.reflect.nbt.NBTUtil;
import de.mickare.armortools.reflect.nbt.WrapNBTCompound;

public class ArmorUtil {

  public static final String NBT_TAG_SITTABLE = "Sittable";

  private static Class<?> entityArmorStand = null;
  private static Method setMarker = null;
  private static Method isMarker = null;

  public static Byte byteOf(boolean value) {
    return value ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
  }

  public static Class<?> getEntityArmorStandClass() throws ClassNotFoundException {
    if (entityArmorStand == null) {
      entityArmorStand = BukkitReflect.getNMSClass("EntityArmorStand");
    }
    return entityArmorStand;
  }

  private static int getDisabledSlots(ArmorStand armor) throws Exception {
    WrapNBTCompound compound = NBTUtil.readCompound(armor);
    return compound.getInt("DisabledSlots");
  }

  private static void setDisabledSlots(ArmorStand armor, int value) throws Exception {
    WrapNBTCompound compound = NBTUtil.readCompound(armor);
    compound.setInt("DisabledSlots", value);
    NBTUtil.writeCompound(armor, compound);
  }

  public static boolean isSittable(ArmorStand armor) {
    if (!armor.isCustomNameVisible() && armor.getCustomName() != null) {
      return armor.getCustomName().equals(NBT_TAG_SITTABLE);
    }
    return false;
  }

  public static void setSittable(ArmorStand armor, boolean sittable) {
    if (sittable) {
      armor.setCustomNameVisible(false);
      armor.setCustomName(NBT_TAG_SITTABLE);
    } else {
      armor.setCustomName(null);
    }
  }

  public static void setProtected(ArmorStand armor, boolean protect, DisabledPart part) {
    try {
      int disabledSlots = getDisabledSlots(armor);
      disabledSlots = part.apply(disabledSlots, protect);
      setDisabledSlots(armor, disabledSlots);
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
