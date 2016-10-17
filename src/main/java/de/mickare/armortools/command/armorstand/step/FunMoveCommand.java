package de.mickare.armortools.command.armorstand.step;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.ArmorUtil;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.StepAction;
import de.mickare.armortools.StepManager;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

public class FunMoveCommand extends AbstractModifyCommand1 {

  public static final int dis_modulo = 32;
  public static final int dis_normal_modulo = 16;
  public static final int dis_sneak_modulo = 1;

  public FunMoveCommand(ArmorToolsPlugin plugin) {
    super(plugin, "funmove", "funmove", Out.CMD_FUNMOVE);
    this.addPermission(Permissions.FUNMOVE);
  }

  @Override
  protected ModifyAction createAction(final Player player, final int area) {

    if (area > 0) {

      player.sendMessage(ChatColor.RED + "FunMove in Area not allowed!");
      return null;

    } else {

      player.sendMessage(ChatColor.RED + "WARNING: THIS MODIFIES ARMORSTANDS (Gravity + Marker)!");

      return ModifyAction.click(ModifyAction.Type.FUNMOVE, (action, armorstands) -> {
        if (armorstands.size() != 1) {
          return 0;
        }
        ArmorStand armorstand = armorstands.stream().findAny().orElse(null);
        if (armorstand == null) {
          return 0;
        }
        armorstand.eject();
        armorstand.setPassenger(player);
        ArmorUtil.setMarker(armorstand, false);
        armorstand.setGravity(true);
        StepManager.getInstance().putMove(player,
            new FunMoveStepAction(Sets.newHashSet(armorstand)));
        Out.CMD_MOVE_START.send(player, 1);
        return 1;
      });

    }

  }

  public @RequiredArgsConstructor class FunMoveStepAction implements StepAction {

    private final @NonNull Set<ArmorStand> armorstands;

    @Override
    public void callEndEvent() {}

    @Override
    public boolean move(StepManager moveManager, Player player, int step) {

      Iterator<ArmorStand> check = armorstands.iterator();
      while (check.hasNext()) {
        if (!check.next().isValid()) {
          check.remove();
        }
      }

      if (armorstands.isEmpty()) {
        Out.CMD_MOVE_EMPTY_VALID.send(player);
        return false;
      }

      if (step == 0) {
        return true; // nothing to do
      }

      Vector direction = player.getLocation().getDirection();
      direction.normalize();

      int step_dir = step < 0 ? -1 : 1;

      final Vector loc_vec_add;
      if (player.isSneaking()) {
        loc_vec_add = direction.multiply(-step_dir * dis_sneak_modulo);
      } else {
        loc_vec_add = direction.multiply(-step_dir * dis_normal_modulo);
      }

      final Vector vel9 = loc_vec_add.clone().multiply(1.d / dis_modulo).multiply(9);

      final Iterator<ArmorStand> iter = armorstands.iterator();
      while (iter.hasNext()) {
        final ArmorStand armorstand = iter.next();

        final Location loc = armorstand.getLocation();

        Vector velocity = armorstand.getVelocity().add(vel9).multiply(0.1);

        if (!getPlugin().canBuild(player, loc.clone().add(velocity.clone().multiply(2)))) {
          continue;
        }

        if (loc.getBlockY() <= 0) {
          Out.ARMOR_REMOVED_FELL_THROUGH_WORLD.send(player);
          iter.remove();
          armorstand.remove();
          continue;
        }

        armorstand.setVelocity(velocity);
      }

      if (armorstands.isEmpty()) {
        Out.CMD_MOVE_EMPTY_VALID.send(player);
        return false;
      }

      return true;
    }

  }

}
