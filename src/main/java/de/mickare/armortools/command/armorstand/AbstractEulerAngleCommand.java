package de.mickare.armortools.command.armorstand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import com.google.common.collect.Lists;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction.Type;
import de.mickare.armortools.euler.RotationMode;
import de.mickare.armortools.util.MathUtil;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractEulerAngleCommand extends AbstractModifyCommand
    implements TabCompleter {

  public static final double DEGREE_TO_RADIANS = Math.PI / 180d;

  protected AbstractEulerAngleCommand(ArmorToolsPlugin plugin, String command, String usage,
      String desc) {
    super(plugin, command, usage, desc);
    this.setDescription(this.getDescription() + " " + Out.CMD_ANGLE_MODEHELP.get());
    this.addPermission(Permissions.ANGLE);
  }

  private static String[] getRotationModesNames() {
    String[] result = new String[RotationMode.values().length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = RotationMode.values()[i].name();
    }
    return result;
  }

  protected AbstractEulerAngleCommand(ArmorToolsPlugin plugin, String command, String usage,
      Out desc) {
    this(plugin, command, usage, desc.toString());
  }

  private List<String> parseRotationMode(String arg) {
    if (arg == null || arg.isEmpty()) {
      return Lists.newArrayList(getRotationModesNames());
    }
    return Arrays.stream(getRotationModesNames()).filter(n -> n.startsWith(arg.toUpperCase()))
        .collect(Collectors.toList());
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    boolean info = false;
    if (args.length > 0) {
      info = args[0].equalsIgnoreCase("i") || args[0].equalsIgnoreCase("info");
    }

    if (args.length <= 1) {
      return Lists.newArrayList("~", "info");
    }
    if (info) {
      if (args.length == 2) {
        return parseRotationMode(args[1]);
      }
    } else {
      if (args.length <= 3) {
        return Lists.newArrayList("~ ");
      } else if (args.length == 4) {
        return parseRotationMode(args[3]);
      }
    }
    return null;
  }

  private Optional<Double> parseAngle(Player player, String arg, String fieldname) {
    if (arg.equalsIgnoreCase("~")) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Double.parseDouble(arg) * DEGREE_TO_RADIANS);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, fieldname, arg);
        player.sendMessage(ChatColor.RED + this.getUsage());
      }
    }
    return null;
  }

  @Override
  protected ModifyAction parseAction(Player player, String[] args) {
    int index = 0;

    if (args.length > index) {
      final String arg0 = args[index];
      if (arg0.equalsIgnoreCase("i") || arg0.equalsIgnoreCase("info")) {
        index++;

        RotationMode mode = RotationMode.DEFAULT;

        for (RotationMode m : RotationMode.values()) {
          if (index >= args.length) {
            break;
          }
          if (args[index].equalsIgnoreCase(m.name())) {
            // if (!Permissions.ANGLE.checkPermission(player, m.name())) {
            // Out.PERMISSION_MISSING_EXTENSION.send(player, m.name());
            // return null;
            // }
            mode = m;
            index++;
            break;
          }
        }

        Out.CMD_ANGLE_MODE.send(player, mode);

        return parseInfo(player, mode);

      }
    }


    Optional<Double> x = Optional.of(0d);
    Optional<Double> y = Optional.of(0d);
    Optional<Double> z = Optional.of(0d);

    if (args.length > index) {
      x = parseAngle(player, args[index], "x");
      if (x == null) {
        return null;
      }
      index++;
    }
    if (args.length > index) {
      y = parseAngle(player, args[index], "y");
      if (x == null) {
        return null;
      }
      index++;
    }
    if (args.length > index) {
      z = parseAngle(player, args[index], "z");
      if (x == null) {
        return null;
      }
      index++;
    }

    RotationMode mode = RotationMode.DEFAULT;

    for (RotationMode m : RotationMode.values()) {
      if (index >= args.length) {
        break;
      }
      if (args[index].equalsIgnoreCase(m.name())) {
        // if (!Permissions.ANGLE.checkPermission(player, m.name())) {
        // Out.PERMISSION_MISSING_EXTENSION.send(player, m.name());
        // return null;
        // }
        mode = m;
        index++;
        break;
      }
    }

    Out.CMD_ANGLE_MODE.send(player, mode);

    int area = -1;
    if (args.length > index) {
      try {
        area = Integer.parseInt(args[index]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "area", args[index]);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return null;
      }
      if (area <= 0) {
        Out.ARG_INVALID_INT_MIN.send(player, "area", area, 1);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return null;
      }
      if (area > ArmorToolsPlugin.MAX_AREA) {
        Out.ARG_INVALID_INT_MAX.send(player, "area", area, ArmorToolsPlugin.MAX_AREA);
        return null;
      }
      if (!this.checkPermission(player, "area")) {
        Out.PERMISSION_MISSING_AREA.send(player);
        return null;
      }
    }

    return parseAction(player, x, y, z, mode, area);
  }

  private String toStringEulerAngle(double[] angles) {
    return MathUtil.round(angles[0] / DEGREE_TO_RADIANS, 2) //
        + ", " + MathUtil.round(angles[1] / DEGREE_TO_RADIANS, 2) //
        + ", " + MathUtil.round(angles[2] / DEGREE_TO_RADIANS, 2);
  }

  protected abstract ModifyAction.Type getModifyActionType();

  protected ModifyAction parseInfo(final Player player, final RotationMode mode) {

    Out.CMD_INFO_HIT.send(player, this.getCommand());

    return ModifyAction.click(ModifyAction.Type.NONE, (action, armorstands) -> {
      armorstands.stream().findAny().ifPresent(a -> {
        double[] angles = mode.getAngles(AbstractEulerAngleCommand.this.getAngle(a));
        Out.CMD_ANGLE_INFO.send(player, toStringEulerAngle(angles));
      });
      return 1;
    });

  }

  public abstract void execute(Player player, ArmorStand armorstand,
      Function<EulerAngle, EulerAngle> rotate);

  public abstract EulerAngle getAngle(ArmorStand armorstand);

  protected ModifyAction parseAction(final Player player, final Optional<Double> x,
      final Optional<Double> y, final Optional<Double> z, final RotationMode mode, int area) {

    /*
     * Function<ArmorStand, EulerAngle> rotation;
     * 
     * 
     * // https://en.wikipedia.org/wiki/Euler_angles#Extrinsic_rotations // Z-Achse ist
     * Blickrichtung des Armorstands.
     * 
     * final EulerAngle angle = mode.rotate(x * DEGREE_TO_RADIANS, y * DEGREE_TO_RADIANS, z *
     * DEGREE_TO_RADIANS + 0.0001); rotation = new Function<ArmorStand, EulerAngle>() {
     * 
     * @Override public EulerAngle apply(ArmorStand arg0) { return angle; } };
     */

    final Function<EulerAngle, EulerAngle> rotate = (angle) -> {
      if (x.isPresent() && y.isPresent() && z.isPresent()) {
        return mode.rotate(x.get(), y.get(), z.get());
      } else {
        double old[] = mode.getAngles(angle);
        double ox = old[0];
        double oy = old[1];
        double oz = old[2];
        if (x.isPresent()) {
          ox = x.get();
        }
        if (y.isPresent()) {
          oy = y.get();
        }
        if (z.isPresent()) {
          oz = z.get();
        }
        return mode.rotate(ox, oy, oz);
      }
    };

    if (area > 0) {

      return ModifyAction.area(getModifyActionType(), area, (action, armorstands) -> {
        return executeAction(player, armorstands, rotate);
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(getModifyActionType(), (action, armorstands) -> {
        return executeAction(player, armorstands, rotate);
      });

    }

  }

  private int executeAction(Player player, Set<ArmorStand> armorstands,
      Function<EulerAngle, EulerAngle> rotate) {
    armorstands.forEach(armor -> execute(player, armor, rotate));
    return armorstands.size();
  }

  public static class LeftArmCommand extends AbstractEulerAngleCommand {
    public LeftArmCommand(ArmorToolsPlugin plugin) {
      super(plugin, "leftarm", "leftarm ([x] [y] [z] [mode] [area]) / (info [mode])",
          Out.CMD_ANGLE_LEFTARM);
      this.addPermission(Permissions.ANGLE_LEFTARM);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setLeftArmPose(rotate.apply(armorstand.getLeftArmPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getLeftArmPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.LEFTARM;
    }

  }

  public static class LeftLegCommand extends AbstractEulerAngleCommand {
    public LeftLegCommand(ArmorToolsPlugin plugin) {
      super(plugin, "leftleg", "leftleg ([x] [y] [z] [mode] [area]) / (info [mode])",
          Out.CMD_ANGLE_LEFTLEG);
      this.addPermission(Permissions.ANGLE_LEFTLEG);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setLeftLegPose(rotate.apply(armorstand.getLeftLegPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getLeftLegPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.LEFTLEG;
    }
  }

  public static class RightArmCommand extends AbstractEulerAngleCommand {
    public RightArmCommand(ArmorToolsPlugin plugin) {
      super(plugin, "rightarm", "rightarm ([x] [y] [z] [mode] [area]) / (info [mode])",
          Out.CMD_ANGLE_RIGHTARM);
      this.addPermission(Permissions.ANGLE_RIGHTARM);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setRightArmPose(rotate.apply(armorstand.getRightArmPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getRightArmPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.RIGHTARM;
    }
  }

  public static class RightLegCommand extends AbstractEulerAngleCommand {
    public RightLegCommand(ArmorToolsPlugin plugin) {
      super(plugin, "rightleg", "rightleg ([x] [y] [z] [mode] [area]) / (info [mode])",
          Out.CMD_ANGLE_RIGHTLEG);
      this.addPermission(Permissions.ANGLE_RIGHTLEG);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setRightLegPose(rotate.apply(armorstand.getRightLegPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getRightLegPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.RIGHTLEG;
    }
  }

  public static class HeadCommand extends AbstractEulerAngleCommand {
    public HeadCommand(ArmorToolsPlugin plugin) {
      super(plugin, "head", "head ([x] [y] [z] [mode] [area]) / (info [mode])", Out.CMD_ANGLE_HEAD);
      this.addPermission(Permissions.ANGLE_HEAD);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setHeadPose(rotate.apply(armorstand.getHeadPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getHeadPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.HEAD;
    }
  }

  public static class BodyCommand extends AbstractEulerAngleCommand {
    public BodyCommand(ArmorToolsPlugin plugin) {
      super(plugin, "body", "body ([x] [y] [z] [mode] [area]) / (info [mode])", Out.CMD_ANGLE_BODY);
      this.addPermission(Permissions.ANGLE_BODY);
    }

    @Override
    public void execute(Player player, ArmorStand armorstand,
        Function<EulerAngle, EulerAngle> rotate) {
      armorstand.setBodyPose(rotate.apply(armorstand.getBodyPose()));
    }

    @Override
    public EulerAngle getAngle(ArmorStand armorstand) {
      return armorstand.getBodyPose();
    }

    @Override
    protected Type getModifyActionType() {
      return Type.BODY;
    }
  }

}
