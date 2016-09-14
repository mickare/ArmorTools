package de.mickare.armortools.euler;

import org.apache.commons.math3.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.bukkit.util.EulerAngle;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public @RequiredArgsConstructor enum RotationMode {

  // Achsen wurden umbenannt um die leicht Konfuse Minecraft Änderung zu verbessern.

  // X = Achse die Quer von Schulter zu Schulter geht
  // umbenannt zu X

  // Y = Achse die von vorne anch hinten geht.
  // umbennant zu Z

  // Z = Achse die von unten nach oben geht.
  // umbenannt zu Y

  DEFAULT(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR), // DEFAULT
  XZY(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR), // DEFAULT - DUPLICATE
  XZY_FIXED(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM),

  YZY(RotationOrder.ZYZ, RotationConvention.VECTOR_OPERATOR),
  YZY_FIXED(RotationOrder.ZYZ, RotationConvention.FRAME_TRANSFORM),

  YXZ(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR),
  YXZ_FIXED(RotationOrder.ZXY, RotationConvention.FRAME_TRANSFORM),

  ZXZ(RotationOrder.YXY, RotationConvention.VECTOR_OPERATOR),
  ZXZ_FIXED(RotationOrder.YXY, RotationConvention.FRAME_TRANSFORM),

  ZXY(RotationOrder.YXZ, RotationConvention.VECTOR_OPERATOR),
  ZXY_FIXED(RotationOrder.YXZ, RotationConvention.FRAME_TRANSFORM),

  MC(RotationOrder.XZY, RotationConvention.FRAME_TRANSFORM), // MC DEFAULT
  XYZ(RotationOrder.XZY, RotationConvention.VECTOR_OPERATOR),
  XYZ_FIXED(RotationOrder.XZY, RotationConvention.FRAME_TRANSFORM), // MC DEFAULT - DUPLICATE

  ;


  private static final Rotation[] SINGULARITY_FIX = new Rotation[] {//
      new Rotation(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR, 0.0001d, 0d, 0d), //
      new Rotation(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR, 0d, 0.0001d, 0d), //
      new Rotation(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR, 0d, 0d, 0.0001d)};

  private final @Getter @NonNull RotationOrder order;
  private final @Getter @NonNull RotationConvention convention;

  public EulerAngle rotate(double x, double y, double z) {
    return forwardToMinecraft(getRotation(x, y, z));
  }

  public double[] getAngles(EulerAngle angle) {
    return revertRotation(revertFromMinecraft(angle));
  }

  public Rotation getRotation(double x, double y, double z) {
    return new Rotation(order, convention, x, y, z);
  }

  public double[] revertRotation(Rotation rotation) throws CardanEulerSingularityException {
    return fixSingularityGetAngles(rotation, order, convention);
  }

  private static int MAX_TRIES = 9;

  private static double[] fixSingularityGetAngles(final Rotation rotation,
      final RotationOrder order, final RotationConvention convention) {

    Rotation rot = rotation;
    for (int tries = 0; tries <= MAX_TRIES; ++tries) {
      try {
        return rot.getAngles(order, convention);
      } catch (CardanEulerSingularityException e) {
        if (tries == MAX_TRIES) {
          throw e;
        }
      }
      rot = SINGULARITY_FIX[tries % SINGULARITY_FIX.length].applyTo(rot);
    }
    throw new RuntimeException("SHOULD NOT HAPPEN: end of method reached");
  }

  public static final EulerAngle forwardToMinecraft(final Rotation rot)
      throws CardanEulerSingularityException {
    final double[] angles =
        fixSingularityGetAngles(rot, RotationOrder.XZY, RotationConvention.FRAME_TRANSFORM);
    return new EulerAngle(angles[0], angles[1], angles[2]);
  }

  public static final Rotation revertFromMinecraft(EulerAngle angle) {
    return new Rotation(RotationOrder.XZY, RotationConvention.FRAME_TRANSFORM, angle.getX(),
        angle.getY(), angle.getZ());
  }

}
