package de.mickare.armortools.util;

import org.apache.commons.math3.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.bukkit.util.EulerAngle;

public class MathUtil {


  public static final double degreesToRadians = Math.PI / 180;

  /**
   * 
   * @param yaw Drehung
   * @param pitch Neigung
   * @param roll Rolle
   * @return
   */
  public static EulerAngle transformDegrees(int yaw, int pitch, int roll) {
    try {
      return transformRadians(yaw * degreesToRadians, pitch * degreesToRadians,
          roll * degreesToRadians);
    } catch (CardanEulerSingularityException exception) {

    }
    return transformRadians(yaw * degreesToRadians, pitch * degreesToRadians,
        roll * degreesToRadians + 0.0001);
  }

  public static EulerAngle transformRadians(double yaw, double pitch, double roll) {

    // Gier = yaw
    // Nick = pitch
    // Roll = roll

    Rotation rot;
    rot = new Rotation(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR, yaw, pitch, roll);

    double[] angles;
    angles = rot.getAngles(RotationOrder.XZY, RotationConvention.FRAME_TRANSFORM);

    return new EulerAngle(angles[0], angles[1], angles[2]);
  }


}
