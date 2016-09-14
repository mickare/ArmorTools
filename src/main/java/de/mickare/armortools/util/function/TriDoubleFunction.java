package de.mickare.armortools.util.function;

@FunctionalInterface
public interface TriDoubleFunction<R> {

  R apply(double x, double y, double z);

}
