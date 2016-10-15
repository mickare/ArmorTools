package de.mickare.armortools.util;

import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class DataContainer {

  public static <T> DataKey<T> newKey() {
    return DataKey.create();
  }

  private final Map<DataKey<?>, Object> map = Maps.newIdentityHashMap();

  public static class DataKey<T> {
    public static <T> DataKey<T> create() {
      return new DataKey<T>();
    }

    @SuppressWarnings("unchecked")
    private T cast(Object d) {
      return (T) d;
    }
  }

  public <T> T get(DataKey<T> key) {
    Preconditions.checkNotNull(key);
    return key.cast(map.get(key));
  }

  public <T> Optional<T> getOptional(DataKey<T> key) {
    return Optional.ofNullable(get(key));
  }

  public <T> T set(DataKey<T> key, T value) {
    Preconditions.checkNotNull(key);
    return key.cast(map.put(key, value));
  }

  public boolean contains(DataKey<?> key) {
    return map.containsKey(key);
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

}
