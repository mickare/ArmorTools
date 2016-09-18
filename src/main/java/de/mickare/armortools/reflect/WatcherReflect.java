package de.mickare.armortools.reflect;

import static de.mickare.armortools.reflect.BukkitReflect.getNMSClass;

import java.lang.reflect.Method;

import com.google.common.base.Preconditions;

public class WatcherReflect {

  private static Method ENTITY_GET_DATA_WATCHER = null;
  private static Method DATA_WATCHER_GET_BYTE = null;
  private static Method DATA_WATCHER_WATCH = null;
  private static Method DATA_WATCHER_GET = null; // get
  private static Method DATA_WATCHER_PUT = null; // put


  public static Object getDataWatcher(Object handle) throws Exception {
    Preconditions.checkNotNull(handle);
    if (ENTITY_GET_DATA_WATCHER == null) {
      ENTITY_GET_DATA_WATCHER = getNMSClass("Entity").getDeclaredMethod("getDataWatcher");
      Preconditions.checkNotNull(ENTITY_GET_DATA_WATCHER);
    }
    return ENTITY_GET_DATA_WATCHER.invoke(handle);
  }

  public static byte getByte(Object datawatcher, int i) throws Exception {
    Preconditions.checkNotNull(datawatcher);
    if (DATA_WATCHER_GET_BYTE == null) {
      DATA_WATCHER_GET_BYTE = getNMSClass("DataWatcher").getDeclaredMethod("getByte", int.class);
      Preconditions.checkNotNull(DATA_WATCHER_GET_BYTE);
    }
    return (byte) DATA_WATCHER_GET_BYTE.invoke(datawatcher, i);
  }


  public static void watch(Object datawatcher, int i, Object value) throws Exception {
    Preconditions.checkNotNull(datawatcher);
    if (DATA_WATCHER_WATCH == null) {
      DATA_WATCHER_WATCH =
          getNMSClass("DataWatcher").getDeclaredMethod("watch", int.class, Object.class);
      Preconditions.checkNotNull(DATA_WATCHER_WATCH);
    }
    DATA_WATCHER_WATCH.invoke(datawatcher, i, value);
  }

  public static Object getWatched(Object datawatcher, int i) throws Exception {
    Preconditions.checkNotNull(datawatcher);
    if (DATA_WATCHER_GET == null) {
      DATA_WATCHER_GET = getNMSClass("DataWatcher").getDeclaredMethod("j", int.class);
    }
    return DATA_WATCHER_GET.invoke(datawatcher, i);
  }

  public static boolean isWatched(Object datawatcher, int i) throws Exception {
    return getWatched(datawatcher, i) != null;
  }


  public static void putWatched(Object datawatcher, int i, Object value) throws Exception {
    Preconditions.checkNotNull(datawatcher);
    Preconditions.checkNotNull(value);
    if (DATA_WATCHER_PUT == null) {
      DATA_WATCHER_PUT = getNMSClass("DataWatcher").getDeclaredMethod("a", int.class, Object.class);
    }
    DATA_WATCHER_PUT.invoke(datawatcher, i, value);
  }


}
