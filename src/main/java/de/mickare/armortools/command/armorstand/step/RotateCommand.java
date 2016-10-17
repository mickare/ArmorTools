package de.mickare.armortools.command.armorstand.step;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.StepAction;
import de.mickare.armortools.StepManager;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand1;
import de.mickare.armortools.event.ArmorEventFactory;
import de.mickare.armortools.event.ArmorRotateEvent;
import lombok.Getter;
import lombok.NonNull;

public class RotateCommand extends AbstractModifyCommand1 {

  public static final int roation_modulo = 256;
  public static final int rotation_normal_modulo = 32;
  public static final int rotation_sneak_modulo = 4;

  public RotateCommand(ArmorToolsPlugin plugin) {
    super(plugin, "rotate", "rotate [area]", Out.CMD_ROTATE);
    this.addPermission(Permissions.ROTATE);
  }

  @Override
  protected ModifyAction createAction(Player player, int area) {

    if (area > 0) {
      
      return ModifyAction.area(ModifyAction.Type.ROTATE, area, (action, armorstands) -> {
        return execute(player, action, armorstands);
      });

    } else {

      return ModifyAction.click(ModifyAction.Type.ROTATE, (action, armorstands) -> {
        return execute(player, action, armorstands);
      });
    }

  }

  private int execute(Player player, ModifyAction action, Collection<ArmorStand> armorstands) {
    if (armorstands.size() == 0) {
      Out.CMD_ROTATE_AREA_EMPTY.send(player);
      return 0;
    }
    StepManager.getInstance().putMove(player, new RotateStepAction(Sets.newHashSet(armorstands)));
    Out.CMD_ROTATE_START.send(player, armorstands.size());
    return armorstands.size();
  }

  public class RotateStepAction implements StepAction {

    private @Getter int total_steps = 0;
    private final @Getter @NonNull Map<ArmorStand, Location> armorstands;

    private final @Getter Vector center;

    private ArmorRotateEvent lastEvent = null;

    public RotateStepAction(Set<ArmorStand> armorstands) {
      this.armorstands = Maps.newHashMap();
      armorstands.forEach(a -> this.armorstands.put(a, a.getLocation()));

      center = new Vector();
      for (ArmorStand armor : armorstands) {
        center.add(armor.getLocation().toVector());
      }
      center.multiply(1d / armorstands.size());

    }

    @Override
    public void callEndEvent() {
      if (lastEvent != null) {
        ArmorEventFactory.callEndRotateEvent(lastEvent);
      }
    }

    @Override
    public boolean move(StepManager moveManager, Player player, int step) {

      Iterator<ArmorStand> check = armorstands.keySet().iterator();
      while (check.hasNext()) {
        if (!check.next().isValid()) {
          check.remove();
        }
      }

      if (armorstands.isEmpty()) {
        Out.CMD_ROTATE_EMPTY_VALID.send(player);
        return false;
      }

      if (step == 0) {
        return true; // nothing to do
      }

      final int modulo = (player.isSneaking() ? rotation_sneak_modulo : rotation_normal_modulo);
      total_steps += step * modulo;


      // int degrees = step * modulo;



      final double rotationRADIAN = total_steps * 2 * Math.PI / roation_modulo;
      final double sin = Math.sin(rotationRADIAN);
      final double cos = Math.cos(rotationRADIAN);
      
      Iterator<Entry<ArmorStand, Location>> iter = armorstands.entrySet().iterator();
      Map<ArmorStand, Location> targetLocations = Maps.newHashMap();
      while (iter.hasNext()) {
        Entry<ArmorStand, Location> entry = iter.next();
        final ArmorStand armorstand = entry.getKey();
        final Location originalLoc = entry.getValue();


        if (!armorstand.isValid()) {
          iter.remove();
          armorstand.remove();
        }

        Location loc = originalLoc.clone();

        if (armorstands.size() == 1) {
          int modYaw = Math.round(loc.getYaw() * roation_modulo / 360f);
          modYaw += total_steps;
          loc.setYaw(modYaw * 360f / roation_modulo);
        } else {

          Vector rel = loc.toVector().subtract(center);

          loc.setX(center.getX() + (cos * rel.getX()) + (sin * rel.getZ()));
          loc.setY(loc.getY());
          loc.setZ(center.getZ() + (-sin * rel.getX()) + (cos * rel.getZ()));

          int modYaw = Math.round(loc.getYaw() * roation_modulo / 360f);
          modYaw -= total_steps;
          loc.setYaw(modYaw * 360f / roation_modulo);

          // loc.setYaw((float) (loc.getYaw() - total_steps * 360f / roation_modulo));

        }

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
      
      this.lastEvent = ArmorEventFactory.callRotateEvent(this, targetLocations);
      if(lastEvent.isCancelled()) {
        return false;
      }

      for (Entry<ArmorStand, Location> e : targetLocations.entrySet()) {
        e.getKey().teleport(e.getValue());
      }

      if (armorstands.isEmpty()) {
        Out.CMD_ROTATE_EMPTY_VALID.send(player);
        return false;
      }

      return true;
    }


  }

}
