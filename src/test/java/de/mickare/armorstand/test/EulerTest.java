package de.mickare.armorstand.test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.mickare.armortools.euler.RotationMode;
import de.mickare.armortools.util.MathUtil;

public class EulerTest {

  static RotationOrder[] orders = new RotationOrder[12];

  static {
    int i = 0;
    orders[i++] = RotationOrder.XYX;
    orders[i++] = RotationOrder.XYZ;
    orders[i++] = RotationOrder.XZX;
    orders[i++] = RotationOrder.XZY;

    orders[i++] = RotationOrder.YXY;
    orders[i++] = RotationOrder.YXZ;
    orders[i++] = RotationOrder.YZX;
    orders[i++] = RotationOrder.YZY;

    orders[i++] = RotationOrder.ZXY;
    orders[i++] = RotationOrder.ZXZ;
    orders[i++] = RotationOrder.ZYX;
    orders[i++] = RotationOrder.ZYZ;
  }


  private static DecimalFormat format;

  static {
    DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
    dfs.setDecimalSeparator('.');
    format = new DecimalFormat("0.00", dfs);
  }

  //@Test
  public void testEuler() throws Exception {

    ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    final AtomicInteger progress = new AtomicInteger(0);

    try {
      for (RotationMode mode : RotationMode.values()) {
        System.out.print("testing rotation (" + mode.name() + "): ");
        progress.set(0);

        final Consumer<Integer> yawTestMedod = (yaw) -> {
          for (int pitch = 0; pitch <= 360; ++pitch) {
            for (int roll = 0; roll <= 360; ++roll) {
              try {
                mode.rotate(yaw, pitch, roll);
              } catch (Exception e) {
                System.out.println(e.getMessage() + ": " + yaw + ", " + pitch + ", " + roll);
                throw e;
              }
            }
          }
          int prog = progress.addAndGet(1);
          if (prog % 36 == 0) {
            System.out.print((prog / 36) + "0%, ");
          }
        };


        List<Future<?>> futures = Lists.newArrayListWithCapacity(360);
        for (int yaw = 0; yaw <= 360; ++yaw) {
          final int fyaw = yaw;
          futures.add(pool.submit(() -> yawTestMedod.accept(fyaw)));
          // yawTestMedod.accept(fyaw);
        }

        for (Future<?> f : futures) {
          f.get(1, TimeUnit.SECONDS);
        }

        System.out.print("\n");
      }
    } finally {
      pool.shutdown();
    }
    pool.awaitTermination(1, TimeUnit.MINUTES);


  }

  public static String str(double[] angles) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < angles.length; ++i) {
      if (i > 0) {
        sb.append("| ");
      }
      sb.append(Strings.padStart(format.format(angles[i] / MathUtil.degreesToRadians), 7, ' '));
    }
    return sb.toString();
  }

}
