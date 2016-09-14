package de.mickare.armortools;

@FunctionalInterface
public interface TriConsumer<T, U, O> {

  public void accept(T t, U u, O o);

}
