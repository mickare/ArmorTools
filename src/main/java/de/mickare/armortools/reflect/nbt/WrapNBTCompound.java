package de.mickare.armortools.reflect.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.base.Preconditions;

import de.mickare.armortools.reflect.BukkitReflect;
import de.mickare.armortools.util.function.CheckedBiConsumer;
import de.mickare.armortools.util.function.CheckedBiFunction;
import de.mickare.armortools.util.function.CheckedTriConsumer;
import lombok.Getter;

public class WrapNBTCompound {

  public static final Constructor<?> NBTTAG_CONSTR;
  public static final CheckedBiConsumer<Object, String> TAG_REMOVE;
  public static final CheckedBiFunction<Object, String, Boolean> TAG_HAS_KEY;

  public static final CheckedTriConsumer<Object, String, Byte> TAG_SET_BYTE;
  public static final CheckedTriConsumer<Object, String, Short> TAG_SET_SHORT;
  public static final CheckedTriConsumer<Object, String, Integer> TAG_SET_INT;
  public static final CheckedTriConsumer<Object, String, Long> TAG_SET_LONG;
  public static final CheckedTriConsumer<Object, String, Float> TAG_SET_FLOAT;
  public static final CheckedTriConsumer<Object, String, Double> TAG_SET_DOUBLE;
  public static final CheckedTriConsumer<Object, String, String> TAG_SET_STRING;
  public static final CheckedTriConsumer<Object, String, Boolean> TAG_SET_BOOL;

  public static final CheckedTriConsumer<Object, String, Object> TAG_SET_COMPOUND;


  public static final CheckedBiFunction<Object, String, Byte> TAG_GET_BYTE;
  public static final CheckedBiFunction<Object, String, Short> TAG_GET_SHORT;
  public static final CheckedBiFunction<Object, String, Integer> TAG_GET_INT;
  public static final CheckedBiFunction<Object, String, Long> TAG_GET_LONG;
  public static final CheckedBiFunction<Object, String, Float> TAG_GET_FLOAT;
  public static final CheckedBiFunction<Object, String, Double> TAG_GET_DOUBLE;
  public static final CheckedBiFunction<Object, String, String> TAG_GET_STRING;
  public static final CheckedBiFunction<Object, String, Boolean> TAG_GET_BOOL;

  public static final CheckedBiFunction<Object, String, Object> TAG_GET_COMPOUND;



  private static <A> CheckedBiConsumer<Object, A> setter(final Class<?> c, final String methodName,
      final Class<A> arg0) throws NoSuchMethodException, SecurityException {
    return c.getMethod(methodName, arg0)::invoke;
  }

  private static <A, B> CheckedTriConsumer<Object, A, B> setter(final Class<?> c,
      final String methodName, final Class<A> arg0, final Class<B> arg1)
      throws NoSuchMethodException, SecurityException {
    return c.getMethod(methodName, arg0, arg1)::invoke;
  }

  private static <A, B> CheckedTriConsumer<Object, A, Object> setterUnsafe(final Class<?> c,
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
      Class<?> compoundClass = BukkitReflect.getNMSClass("NBTTagCompound");

      NBTTAG_CONSTR = compoundClass.getConstructor();
      TAG_REMOVE = setter(compoundClass, "remove", String.class);
      TAG_HAS_KEY = getter(compoundClass, "hasKey", String.class);

      TAG_SET_BYTE = setter(compoundClass, "setByte", String.class, byte.class);
      TAG_SET_SHORT = setter(compoundClass, "setShort", String.class, short.class);
      TAG_SET_INT = setter(compoundClass, "setInt", String.class, int.class);
      TAG_SET_LONG = setter(compoundClass, "setLong", String.class, long.class);
      TAG_SET_FLOAT = setter(compoundClass, "setFloat", String.class, float.class);
      TAG_SET_DOUBLE = setter(compoundClass, "setDouble", String.class, double.class);
      TAG_SET_STRING = setter(compoundClass, "setString", String.class, String.class);
      TAG_SET_BOOL = setter(compoundClass, "setBoolean", String.class, boolean.class);

      TAG_SET_COMPOUND =
          setterUnsafe(compoundClass, "set", String.class, BukkitReflect.getNMSClass("NBTBase"));

      TAG_GET_BYTE = getter(compoundClass, "getByte", String.class);
      TAG_GET_SHORT = getter(compoundClass, "getShort", String.class);
      TAG_GET_INT = getter(compoundClass, "getInt", String.class);
      TAG_GET_LONG = getter(compoundClass, "getLong", String.class);
      TAG_GET_FLOAT = getter(compoundClass, "getFloat", String.class);
      TAG_GET_DOUBLE = getter(compoundClass, "getDouble", String.class);
      TAG_GET_STRING = getter(compoundClass, "getString", String.class);
      TAG_GET_BOOL = getter(compoundClass, "getBoolean", String.class);

      TAG_GET_COMPOUND = getter(compoundClass, "getCompound", String.class);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private @Getter final Object handle;

  public WrapNBTCompound() {
    try {
      this.handle = NBTTAG_CONSTR.newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public WrapNBTCompound(Object compound) {
    Preconditions.checkNotNull(compound);
    this.handle = compound;
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

  public void setCompound(String key, WrapNBTCompound compound) {
    if (compound != null) {
      set(key, compound.getHandle(), TAG_SET_COMPOUND);
    } else {
      set(key, null, TAG_SET_COMPOUND);
    }
  }

  private <T> void set(String key, T value, CheckedTriConsumer<Object, String, T> modifier) {
    try {
      modifier.accept(this.handle, key, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(String key) {
    try {
      TAG_REMOVE.accept(this.handle, key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean has(String key) {
    try {
      return TAG_HAS_KEY.apply(this.handle, key);
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

  public WrapNBTCompound getCompound(String key) {
    Object obj = get(key, TAG_GET_COMPOUND);
    return obj != null ? new WrapNBTCompound(obj) : null;
  }

  private <T> T get(String key, CheckedBiFunction<Object, String, T> modifier) {
    try {
      return (T) modifier.apply(this.handle, key);
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

  public Optional<WrapNBTCompound> getCompoundIfPresent(String key) {
    return getIfPresent(key, TAG_GET_COMPOUND).map(WrapNBTCompound::new);
  }

  public <T> Optional<T> getIfPresent(String key, CheckedBiFunction<Object, String, T> modifier) {
    try {
      if (this.has(key)) {
        return Optional.ofNullable(this.get(key, modifier));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
