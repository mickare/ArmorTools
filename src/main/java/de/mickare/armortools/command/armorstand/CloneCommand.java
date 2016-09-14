package de.mickare.armortools.command.armorstand;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.AbstractCommandAndClick;
import de.mickare.armortools.util.Callback;

public class CloneCommand extends AbstractCommandAndClick<ArmorToolsPlugin> {

  public static final Map<Player, ArmorSetting> SETTINGS = new WeakHashMap<>();

  public CloneCommand(ArmorToolsPlugin plugin) {
    super(plugin, "clone", "clone", Out.CMD_CLONE.toString());
    this.addPermission(Permissions.CLONE);
  }

  @Override
  public Callback<ArmorStand> getClickCallback(final Player player, String[] args) {

    Out.CMD_MODIFY_HIT.send(player, this.getCommand());

    return new Callback<ArmorStand>() {
      @Override
      public void call(ArmorStand armorstand) {
        if (!getPlugin().canModify(player, armorstand)) {
          Out.CMD_MODIFY_YOU_CANT_BUILD_HERE.send(player);
          return;
        }
        SETTINGS.put(player, new ArmorSetting(armorstand));
        Out.CMD_CLONE_DONE.send(player);
      }
    };
  }

  public static class ArmorSetting {

    private final EulerAngle bodyPose;
    private final EulerAngle headPose;
    private final EulerAngle leftArmPose;
    private final EulerAngle leftLegPose;
    private final EulerAngle rightArmPose;
    private final EulerAngle rightLegPose;
    private final boolean small;
    private final boolean gravity;
    private final boolean arms;
    private final boolean basePlate;

    public ArmorSetting(ArmorStand armorstand) {
      this.bodyPose = armorstand.getBodyPose();
      this.headPose = armorstand.getHeadPose();
      this.leftArmPose = armorstand.getLeftArmPose();
      this.leftLegPose = armorstand.getLeftLegPose();
      this.rightArmPose = armorstand.getRightArmPose();
      this.rightLegPose = armorstand.getRightLegPose();
      this.small = armorstand.isSmall();
      this.gravity = armorstand.hasGravity();
      this.arms = armorstand.hasArms();
      this.basePlate = armorstand.hasBasePlate();
    }

    public void apply(ArmorStand armorstand) {
      armorstand.setBodyPose(bodyPose);
      armorstand.setHeadPose(headPose);
      armorstand.setLeftArmPose(leftArmPose);
      armorstand.setLeftLegPose(leftLegPose);
      armorstand.setRightArmPose(rightArmPose);
      armorstand.setRightLegPose(rightLegPose);
      armorstand.setSmall(small);
      armorstand.setGravity(gravity);
      armorstand.setArms(arms);;
      armorstand.setBasePlate(basePlate);
    }

  }

}
