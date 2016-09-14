package de.mickare.armortools.util.function;

@FunctionalInterface
public interface CheckedTriConsumer<A, B, C> {

  void accept(A a, B b, C c) throws Exception;

}
