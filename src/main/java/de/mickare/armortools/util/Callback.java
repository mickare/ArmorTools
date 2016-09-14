package de.mickare.armortools.util;

import javax.annotation.Nullable;

public interface Callback<V> {

  void call(@Nullable V value);

}
