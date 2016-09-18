package de.mickare.armortools.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import com.google.common.base.Preconditions;

public class BukkitReflect {

  public static final String VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
  private static Method ENTITY_GET_HANDLE = null;
  private static Method CRAFTARMORSTAND_GET_HANDLE = null;
  private static Field ENTITY_INVULNERABLE = null;


  private static Field ENTITY_LASTYAW = null, ENTITY_LASTPITCH = null, ENTITY_YAW = null,
      ENTITY_PITCH = null;


  static {

    try {
      ENTITY_GET_HANDLE = getCraftBukkitClass("entity.CraftEntity").getMethod("getHandle");
      CRAFTARMORSTAND_GET_HANDLE =
          getCraftBukkitClass("entity.CraftArmorStand").getDeclaredMethod("getHandle");

      Class<?> entity = getNMSClass("Entity");
      ENTITY_INVULNERABLE = entity.getDeclaredField("invulnerable");
      ENTITY_INVULNERABLE.setAccessible(true);

      ENTITY_LASTYAW = entity.getDeclaredField("lastYaw");
      ENTITY_LASTPITCH = entity.getDeclaredField("lastYaw");
      ENTITY_YAW = entity.getDeclaredField("lastYaw");
      ENTITY_PITCH = entity.getDeclaredField("lastYaw");

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static void setRotation(ArmorStand entity, float yaw, float pitch) throws Exception {
    setRotation(getHandle(entity), yaw, pitch);
  }

  public static void setRotation(Object entity, float yaw, float pitch) throws Exception {
    ENTITY_LASTYAW.setFloat(entity, pitch);
    ENTITY_YAW.setFloat(entity, pitch);
    ENTITY_LASTPITCH.setFloat(entity, pitch);
    ENTITY_PITCH.setFloat(entity, pitch);
    double d3 = (double) (ENTITY_LASTYAW.getFloat(entity) - yaw);

    if (d3 < -180.0D) {
      ENTITY_LASTYAW.setFloat(entity, ENTITY_LASTYAW.getFloat(entity) + 360.0F);
    }

    if (d3 >= 180.0D) {
      ENTITY_LASTYAW.setFloat(entity, ENTITY_LASTYAW.getFloat(entity) - 360.0F);
    }

    ENTITY_YAW.setFloat(entity, yaw % 360.0F);
    ENTITY_PITCH.setFloat(entity, pitch % 360.0F);
  }


  public static String getNMSClassName(String name) {
    return "net.minecraft.server." + VERSION + "." + name;
  }

  public static Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
    return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
  }


  public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
    return Class.forName(getNMSClassName(name));
  }


  public static Object getHandle(Entity entity) throws Exception {
    Preconditions.checkNotNull(entity);
    return ENTITY_GET_HANDLE.invoke(entity);
  }

  public static Object getHandle(ArmorStand armorstand) throws Exception {
    Preconditions.checkNotNull(armorstand);
    return CRAFTARMORSTAND_GET_HANDLE.invoke(armorstand);
  }

  public static boolean isInvulnerable(Object entity) throws Exception {
    Preconditions.checkNotNull(entity);
    return ENTITY_INVULNERABLE.getBoolean(entity);
  }

  public static void setInvulnerable(Object entity, boolean invulnerable) throws Exception {
    Preconditions.checkNotNull(entity);
    ENTITY_INVULNERABLE.setBoolean(entity, invulnerable);
  }


}
