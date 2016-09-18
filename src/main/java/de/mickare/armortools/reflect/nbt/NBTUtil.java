package de.mickare.armortools.reflect.nbt;

import org.bukkit.entity.Entity;

import com.google.common.base.Preconditions;

import de.mickare.armortools.reflect.BukkitReflect;
import de.mickare.armortools.util.function.CheckedBiConsumer;

public class NBTUtil {

  private static CheckedBiConsumer<Object, ? super Object> getReadMethod(Object handle)
      throws NoSuchMethodException, SecurityException, ClassNotFoundException {
    return handle.getClass().getMethod("b", BukkitReflect.getNMSClass("NBTTagCompound"))::invoke;
  }

  private static CheckedBiConsumer<Object, ? super Object> getWriteMethod(Object handle)
      throws NoSuchMethodException, SecurityException, ClassNotFoundException {
    return handle.getClass().getMethod("a", BukkitReflect.getNMSClass("NBTTagCompound"))::invoke;
  }

  public static WrapNBTCompound readCompound(Entity entity) {
    WrapNBTCompound compound = new WrapNBTCompound();
    try {
      Object handle = BukkitReflect.getHandle(entity);
      getReadMethod(handle).accept(handle, compound.getHandle());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return compound;
  }

  public static void writeCompound(Entity entity, WrapNBTCompound compound) {
    Preconditions.checkNotNull(compound);
    try {
      Object handle = BukkitReflect.getHandle(entity);
      getWriteMethod(handle).accept(handle, compound.getHandle());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
