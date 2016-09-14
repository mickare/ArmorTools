package de.mickare.armortools.util.function;

@FunctionalInterface
public interface CheckedBiFunction<A, B, R> {

  R apply(A a, B b) throws Exception;

}
