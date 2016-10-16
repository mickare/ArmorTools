package de.mickare.armortools.command.armorstand.step;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.StepAction;
import de.mickare.armortools.StepManager;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;
import de.mickare.armortools.event.ArmorEventFactory;
import de.mickare.armortools.event.ArmorMoveEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class MoveCommand extends AbstractModifyCommand implements TabCompleter {

  private static final boolean USE_GRID_DEFAULT = false;

  public static final int dis_modulo = 32;
  public static final int dis_normal_modulo = 16;
  public static final int dis_sneak_modulo = 1;

  public MoveCommand(ArmorToolsPlugin plugin) {
    super(plugin, "move", "move [-grid] [area]", Out.CMD_MOVE);
    this.addPermission(Permissions.MOVE);
  }


  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    if (args.length == 0) {
      return Lists.newArrayList("-grid");
    } else if ("-grid".startsWith(args[0].toLowerCase())) {
      return Lists.newArrayList("-grid");
    }
    return null;
  }

  @Override
  protected ModifyAction parseAction(Player player, String[] args) {

    final AtomicBoolean useGrid = new AtomicBoolean(USE_GRID_DEFAULT);

    int index = 0;
    if (args.length > index) {
      if (args[index].equalsIgnoreCase("-grid")) {
        useGrid.set(!USE_GRID_DEFAULT);
        index++;
      }
    }

    Optional<Integer> area = AbstractModifyCommand1.parseArea(this, player, args, index);

    if (!area.isPresent()) {
      return null;
    }

    if (area.get() > 0) {

      return ModifyAction.area(area.get(), (action, armorstands) -> {
        return execute(player, action, armorstands, useGrid.get());
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click((action, armorstands) -> {
        return execute(player, action, armorstands, useGrid.get());
      });

    }


  }

  private int execute(Player player, ModifyAction action, Set<ArmorStand> armorstands,
      boolean useGrid) {
    if (armorstands.size() == 0) {
      Out.CMD_MOVE_AREA_EMPTY.send(player);
      return 0;
    }
    StepManager.getInstance().putMove(player,
        new MoveStepAction(Sets.newHashSet(armorstands), useGrid));

    Out.CMD_MOVE_START.send(player, armorstands.size());
    return armorstands.size();
  }



  @RequiredArgsConstructor
  public class MoveStepAction implements StepAction {

    private final @Getter @NonNull Set<ArmorStand> armorstands;
    private final @Getter boolean useGrid;

    private ArmorMoveEvent lastEvent = null;

    @Override
    public void callEndEvent() {
      if (lastEvent != null) {
        ArmorEventFactory.callEndMoveEvent(lastEvent);
      }
    }

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
      Map<ArmorStand, Location> targetLocations = Maps.newHashMap();
      while (iter.hasNext()) {
        final ArmorStand armorstand = iter.next();

        final Vector loc_vec = armorstand.getLocation().toVector().multiply(dis_modulo);

        loc_vec.add(loc_vec_add);

        if (useGrid) {
          loc_vec.setX(Math.round(loc_vec.getX()));
          loc_vec.setY(Math.round(loc_vec.getY()));
          loc_vec.setZ(Math.round(loc_vec.getZ()));
        }
        loc_vec.multiply(1.d / dis_modulo);


        final Location loc = armorstand.getLocation();
        loc.setX(loc_vec.getX());
        loc.setY(loc_vec.getY());
        loc.setZ(loc_vec.getZ());

        if (loc.getBlockY() <= 0) {
          Out.ARMOR_REMOVED_FELL_THROUGH_WORLD.send(player);
          iter.remove();
          armorstand.remove();
          continue;
        }

        if (!getPlugin().canBuild(player, loc)) {
          return true;
        }

        targetLocations.put(armorstand, loc);
      }

      this.lastEvent =
          ArmorEventFactory.callMoveEvent(this, loc_vec_add.clone().multiply(1.d / dis_modulo), targetLocations);
      if (lastEvent.isCancelled()) {
        return false;
      }

      for (Entry<ArmorStand, Location> e : targetLocations.entrySet()) {
        e.getKey().teleport(e.getValue());
      }

      if (armorstands.isEmpty()) {
        Out.CMD_MOVE_EMPTY_VALID.send(player);
        return false;
      }

      return true;
    }

  }


}
