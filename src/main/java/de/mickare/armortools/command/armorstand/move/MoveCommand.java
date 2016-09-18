package de.mickare.armortools.command.armorstand.move;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.StepAction;
import de.mickare.armortools.StepManager;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;
import de.mickare.armortools.event.ArmorEventFactory;
import de.mickare.armortools.util.Callback;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class MoveCommand extends AbstractModifyCommand1 {

  public static final int dis_modulo = 32;
  public static final int dis_normal_modulo = 16;
  public static final int dis_sneak_modulo = 1;

  public MoveCommand(ArmorToolsPlugin plugin) {
    super(plugin, "move", "move [area]", Out.CMD_MOVE);
    this.addPermission(Permissions.MOVE);
  }

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {

      return ModifyAction.area(area, a -> {
        return true;
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        StepManager.getInstance().putMove(player, new MoveStepAction(Sets.newHashSet(a)));
        Out.CMD_MOVE_START.send(player, 1);
        return true;
      });

    }

  }

  protected Callback<ArmorStand> doAreaAction(Player player, ModifyAction action) {
    if (action == null) {
      return null;
    }
    if (action.isArea()) {

      Set<ArmorStand> armorstands =
          player.getNearbyEntities(action.getAreaSize(), action.getAreaSize(), action.getAreaSize())
              .stream()//
              .filter(e -> e instanceof ArmorStand)//
              .map(e -> (ArmorStand) e)//
              .filter(a -> ArmorEventFactory.callModifyEvent(player, a))//
              .filter(a -> getPlugin().canModify(player, a))//
              .collect(Collectors.toSet());
      if (armorstands.size() == 0) {
        Out.CMD_MOVE_AREA_EMPTY.send(player);
        return null;
      }
      StepManager.getInstance().putMove(player, new MoveStepAction(Sets.newHashSet(armorstands)));

      Out.CMD_MOVE_START.send(player, armorstands.size());

      return null;

    } else {

      return (armorstand) -> {
        if (!getPlugin().canModify(player, armorstand)) {
          Out.CMD_MODIFY_YOU_CANT_BUILD_HERE.send(player);
          return;
        }
        if (!ArmorEventFactory.callModifyEvent(player, armorstand)) {
          return;
        }
        action.apply(armorstand);
      };

    }
  }

  public @RequiredArgsConstructor class MoveStepAction implements StepAction {

    private final @NonNull Set<ArmorStand> armorstands;

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

      Vector facing = player.getLocation().getDirection();
      double x = facing.getX();
      double y = facing.getY();
      double z = facing.getZ();

      double max = Math.max(x, Math.max(y, z));
      double min = Math.min(x, Math.min(y, z));

      double selected;
      if (Math.abs(max) > Math.abs(min)) {
        selected = max;
      } else { // if (Math.abs(max) <= Math.abs(min))
        selected = min;
      }

      Vector direction;
      int stepDirection = selected > 0 ? -1 : 1;
      if (x == selected) {
        direction = new Vector(stepDirection, 0, 0);
      } else if (y == selected) {
        direction = new Vector(0, stepDirection, 0);
      } else { // if (z == selected)
        direction = new Vector(0, 0, stepDirection);
      }

      final Vector loc_vec_add;
      if (player.isSneaking()) {
        loc_vec_add = direction.multiply(step * dis_sneak_modulo);
      } else {
        loc_vec_add = direction.multiply(step * dis_normal_modulo);
      }


      final Iterator<ArmorStand> iter = armorstands.iterator();
      while (iter.hasNext()) {
        final ArmorStand armorstand = iter.next();

        final Vector loc_vec = armorstand.getLocation().toVector().multiply(dis_modulo);

        loc_vec.add(loc_vec_add);

        loc_vec.setX(Math.round(loc_vec.getX()));
        loc_vec.setY(Math.round(loc_vec.getY()));
        loc_vec.setZ(Math.round(loc_vec.getZ()));
        loc_vec.multiply(1.d / dis_modulo);


        final Location loc = armorstand.getLocation();
        loc.setX(loc_vec.getX());
        loc.setY(loc_vec.getY());
        loc.setZ(loc_vec.getZ());

        if (!getPlugin().canBuild(player, loc)) {
          continue;
        }

        if (loc.getBlockY() <= 0) {
          Out.ARMOR_REMOVED_FELL_THROUGH_WORLD.send(player);
          iter.remove();
          armorstand.remove();
          continue;
        }

        armorstand.teleport(loc);
      }

      if (armorstands.isEmpty()) {
        Out.CMD_MOVE_EMPTY_VALID.send(player);
        return false;
      }

      return true;
    }

  }

}
