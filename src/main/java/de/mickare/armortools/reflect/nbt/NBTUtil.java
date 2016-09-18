package de.mickare.armortools.reflect.nbt;

import org.bukkit.entity.Entity;

import com.google.common.base.Preconditions;

import de.mickare.armortools.reflect.BukkitReflect;
import de.mickare.armortools.util.function.CheckedBiConsumer;

public class NBTUtil {

  private static CheckedBiConsumer<Object, ? super Object> ENTITY_READ_TAG;
  private static CheckedBiConsumer<Object, ? super Object> ENTITY_WRITE_TAG;

  static {

    try {
      Class<?> tag = BukkitReflect.getNMSClass("NBTTagCompound");
      Class<?> entity = BukkitReflect.getNMSClass("Entity");

      try {
        ENTITY_READ_TAG = entity.getMethod("b", tag)::invoke;
      } catch (Exception e) {
        ENTITY_READ_TAG = entity.getMethod("c", tag)::invoke;
      }

      ENTITY_WRITE_TAG = entity.getMethod("a", tag)::invoke;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static WrapNBTCompound readCompound(Entity entity) {
    WrapNBTCompound compound = new WrapNBTCompound();
    try {
      ENTITY_READ_TAG.accept(BukkitReflect.getHandle(entity), compound.getHandle());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return compound;
  }

  public static void writeCompound(Entity entity, WrapNBTCompound compound) {
    Preconditions.checkNotNull(compound);
    try {
      ENTITY_WRITE_TAG.accept(BukkitReflect.getHandle(entity), compound.getHandle());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
