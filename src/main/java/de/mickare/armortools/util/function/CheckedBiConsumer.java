package de.mickare.armortools.util.function;

@FunctionalInterface
public interface CheckedBiConsumer<A, B> {

  void accept(A a, B b) throws Exception;

}
