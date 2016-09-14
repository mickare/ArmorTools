package de.mickare.armortools.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import com.google.common.base.Preconditions;

import de.mickare.armortools.util.function.CheckedBiConsumer;
import de.mickare.armortools.util.function.CheckedBiFunction;
import de.mickare.armortools.util.function.CheckedTriConsumer;
import lombok.Getter;

public class NBTTagWrapper {

  private static CheckedBiConsumer<Object, ? super Object> ENTITY_LOAD_TAG = null;
  private static CheckedBiConsumer<Object, ? super Object> ENTITY_WRITE_TAG = null;

  private static Constructor<?> NBTTAG_CONSTR = null;
  private static CheckedBiConsumer<Object, String> TAG_REMOVE = null;
  private static CheckedBiFunction<Object, String, Boolean> TAG_HAS_KEY = null;

  private static CheckedTriConsumer<Object, String, Byte> TAG_SET_BYTE = null;
  private static CheckedTriConsumer<Object, String, Short> TAG_SET_SHORT = null;
  private static CheckedTriConsumer<Object, String, Integer> TAG_SET_INT = null;
  private static CheckedTriConsumer<Object, String, Long> TAG_SET_LONG = null;
  private static CheckedTriConsumer<Object, String, Float> TAG_SET_FLOAT = null;
  private static CheckedTriConsumer<Object, String, Double> TAG_SET_DOUBLE = null;
  private static CheckedTriConsumer<Object, String, String> TAG_SET_STRING = null;
  private static CheckedTriConsumer<Object, String, Boolean> TAG_SET_BOOL = null;

  private static CheckedBiFunction<Object, String, Byte> TAG_GET_BYTE = null;
  private static CheckedBiFunction<Object, String, Short> TAG_GET_SHORT = null;
  private static CheckedBiFunction<Object, String, Integer> TAG_GET_INT = null;
  private static CheckedBiFunction<Object, String, Long> TAG_GET_LONG = null;
  private static CheckedBiFunction<Object, String, Float> TAG_GET_FLOAT = null;
  private static CheckedBiFunction<Object, String, Double> TAG_GET_DOUBLE = null;
  private static CheckedBiFunction<Object, String, String> TAG_GET_STRING = null;
  private static CheckedBiFunction<Object, String, Boolean> TAG_GET_BOOL = null;



  private static <A> CheckedBiConsumer<Object, A> setter(final Class<?> c, final String methodName,
      final Class<A> arg0) throws NoSuchMethodException, SecurityException {
    return c.getMethod(methodName, arg0)::invoke;
  }

  private static <A, B> CheckedTriConsumer<Object, A, B> setter(final Class<?> c,
      final String methodName, final Class<A> arg0, final Class<B> arg1)
          throws NoSuchMethodException, SecurityException {
    return c.getMethod(methodName, arg0, arg1)::invoke;
  }

  @SuppressWarnings("unchecked")
  private static <A, R> CheckedBiFunction<Object, A, R> getter(final Class<?> c,
      final String methodName, final Class<A> arg0)
          throws NoSuchMethodException, SecurityException {
    final Method m = c.getMethod(methodName, arg0);
    return (o, a) -> (R) m.invoke(o, a);
  }

  static {

    try {
      Class<?> tag = BukkitReflect.getNMSClass("NBTTagCompound");
      Class<?> entityLiving = BukkitReflect.getNMSClass("EntityLiving");

      ENTITY_LOAD_TAG = entityLiving.getMethod("c", tag)::invoke;
      ENTITY_WRITE_TAG = entityLiving.getMethod("a", tag)::invoke;

      NBTTAG_CONSTR = tag.getConstructor();
      TAG_REMOVE = setter(tag, "remove", String.class);
      TAG_HAS_KEY = getter(tag, "hasKey", String.class);

      TAG_SET_BYTE = setter(tag, "setByte", String.class, byte.class);
      TAG_SET_SHORT = setter(tag, "setShort", String.class, short.class);
      TAG_SET_INT = setter(tag, "setInt", String.class, int.class);
      TAG_SET_LONG = setter(tag, "setLong", String.class, long.class);
      TAG_SET_FLOAT = setter(tag, "setFloat", String.class, float.class);
      TAG_SET_DOUBLE = setter(tag, "setDouble", String.class, double.class);
      TAG_SET_STRING = setter(tag, "setString", String.class, String.class);
      TAG_SET_BOOL = setter(tag, "setBoolean", String.class, boolean.class);

      TAG_GET_BYTE = getter(tag, "getByte", String.class);
      TAG_GET_SHORT = getter(tag, "getShort", String.class);
      TAG_GET_INT = getter(tag, "getInt", String.class);
      TAG_GET_LONG = getter(tag, "getLong", String.class);
      TAG_GET_FLOAT = getter(tag, "getFloat", String.class);
      TAG_GET_DOUBLE = getter(tag, "getDouble", String.class);
      TAG_GET_STRING = getter(tag, "getString", String.class);
      TAG_GET_BOOL = getter(tag, "getBoolean", String.class);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private static Object newNBTTagCompound() throws Exception {
    return NBTTAG_CONSTR.newInstance();
  }

  private final @Getter LivingEntity entity;
  private final @Getter Object handle;

  public NBTTagWrapper(ArmorStand entity) {
    Preconditions.checkNotNull(entity);
    this.entity = entity;
    try {
      this.handle = BukkitReflect.getHandle(entity);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public NBTTagWrapper(LivingEntity entity) {
    Preconditions.checkNotNull(entity);
    this.entity = entity;
    try {
      this.handle = BukkitReflect.getHandle(entity);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void setByte(String key, byte value) {
    set(key, value, TAG_SET_BYTE);
  }

  public void setShort(String key, short value) {
    set(key, value, TAG_SET_SHORT);
  }

  public void setInt(String key, int value) {
    set(key, value, TAG_SET_INT);
  }

  public void setLong(String key, long value) {
    set(key, value, TAG_SET_LONG);
  }

  public void setFloat(String key, float value) {
    set(key, value, TAG_SET_FLOAT);
  }

  public void setDouble(String key, double value) {
    set(key, value, TAG_SET_DOUBLE);
  }

  public void setString(String key, String value) {
    set(key, value, TAG_SET_STRING);
  }

  public void setBoolean(String key, boolean value) {
    set(key, value, TAG_SET_BOOL);
  }

  private <T> void set(String key, T value, CheckedTriConsumer<Object, String, T> modifier) {
    try {
      Object tag = newNBTTagCompound();
      ENTITY_LOAD_TAG.accept(handle, tag);
      modifier.accept(tag, key, value);
      ENTITY_WRITE_TAG.accept(handle, tag);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(String key) {
    try {
      Object tag = newNBTTagCompound();
      ENTITY_LOAD_TAG.accept(handle, tag);
      TAG_REMOVE.accept(tag, key);
      ENTITY_WRITE_TAG.accept(handle, tag);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean has(String key) {
    try {
      Object tag = newNBTTagCompound();
      ENTITY_LOAD_TAG.accept(handle, tag);
      return TAG_HAS_KEY.apply(tag, key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



  public byte getByte(String key) {
    return get(key, TAG_GET_BYTE);
  }

  public short getShort(String key) {
    return get(key, TAG_GET_SHORT);
  }

  public int getInt(String key) {
    return get(key, TAG_GET_INT);
  }

  public long getLong(String key) {
    return get(key, TAG_GET_LONG);
  }

  public float getFloat(String key) {
    return get(key, TAG_GET_FLOAT);
  }

  public double getDouble(String key) {
    return get(key, TAG_GET_DOUBLE);
  }

  public String getString(String key) {
    return get(key, TAG_GET_STRING);
  }

  public boolean getBoolean(String key) {
    return get(key, TAG_GET_BOOL);
  }


  private <T> T get(String key, CheckedBiFunction<Object, String, T> modifier) {
    try {
      Object tag = newNBTTagCompound();
      ENTITY_LOAD_TAG.accept(handle, tag);
      return (T) modifier.apply(tag, key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<Byte> getByteIfPresent(String key) {
    return getIfPresent(key, TAG_GET_BYTE);
  }

  public Optional<Short> getShortIfPresent(String key) {
    return getIfPresent(key, TAG_GET_SHORT);
  }

  public Optional<Integer> getIntIfPresent(String key) {
    return getIfPresent(key, TAG_GET_INT);
  }

  public Optional<Long> getLongIfPresent(String key) {
    return getIfPresent(key, TAG_GET_LONG);
  }

  public Optional<Float> getFloatIfPresent(String key) {
    return getIfPresent(key, TAG_GET_FLOAT);
  }

  public Optional<Double> getDoubleIfPresent(String key) {
    return getIfPresent(key, TAG_GET_DOUBLE);
  }

  public Optional<String> getStringIfPresent(String key) {
    return getIfPresent(key, TAG_GET_STRING);
  }

  public Optional<Boolean> getBooleanIfPresent(String key) {
    return getIfPresent(key, TAG_GET_BOOL);
  }



  public <T> Optional<T> getIfPresent(String key, CheckedBiFunction<Object, String, T> modifier) {
    try {
      Object tag = newNBTTagCompound();
      ENTITY_LOAD_TAG.accept(handle, tag);
      if (TAG_HAS_KEY.apply(tag, key)) {
        return Optional.ofNullable((T) modifier.apply(tag, key));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


}
