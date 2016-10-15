package de.mickare.armortools.command.armorstand;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.AbstractCommandAndClick;
import de.mickare.armortools.event.ArmorEventFactory;
import de.mickare.armortools.util.Callback;
import de.mickare.armortools.util.DataContainer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public abstract class AbstractModifyCommand extends AbstractCommandAndClick<ArmorToolsPlugin> {

  public AbstractModifyCommand(ArmorToolsPlugin plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractModifyCommand(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    this(plugin, command, usage, desc.toString());
  }

  @Override
  public Callback<ArmorStand> getClickCallback(final Player player, String[] args) {

    if (!Permissions.MODIFY.checkPermission(player)) {
      Out.PERMISSION_MISSING_MODIFY.send(player);
      return null;
    }

    final ModifyAction action = parseAction(player, args);
    return doAreaAction(player, action);

  }



  protected Callback<ArmorStand> doAreaAction(final Player player, final ModifyAction action) {
    if (action == null) {
      return null;
    }
    if (action.isArea()) {

      final AtomicInteger count = new AtomicInteger(0);
      player.getNearbyEntities(action.areaSize, action.areaSize, action.areaSize).stream()//
          .filter(e -> e instanceof ArmorStand)//
          .map(e -> (ArmorStand) e)//
          .filter(a -> ArmorEventFactory.callModifyEvent(player, a))//
          .filter(a -> getPlugin().canModify(player, a))//
          .forEach(a -> {
            if (action.apply(a)) {
              count.incrementAndGet();
            }
          });

      action.finish();
      Out.CMD_MODIFY_MULTI_ARMORSTANDS.send(player, count.get());

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
        action.finish();
        Out.CMD_MODIFY_DONE.send(player);
      };

    }
  }

  protected abstract ModifyAction parseAction(Player player, String[] args);

  public static @RequiredArgsConstructor class ModifyAction {
    private final @Getter boolean area;
    private final @Getter int areaSize;
    private final @NonNull Function<ArmorStand, Boolean> modifier;
    private final @Getter DataContainer data = new DataContainer();
    private Runnable finish = () -> {
    };

    public static ModifyAction click(Function<ArmorStand, Boolean> modifier) {
      return new ModifyAction(false, 0, modifier);
    }

    public static ModifyAction area(int size, Function<ArmorStand, Boolean> modifier) {
      return new ModifyAction(true, size, modifier);
    }

    public boolean apply(ArmorStand armor) {
      return modifier.apply(armor);
    }

    public ModifyAction setFinish(Runnable finish) {
      Preconditions.checkNotNull(finish);
      this.finish = finish;
      return this;
    }

    public void finish() {
      this.finish.run();
    }

    public <T> ModifyAction setData(DataContainer.DataKey<T> key, T value) {
      Preconditions.checkNotNull(key);
      this.data.set(key, value);
      return this;
    }

    public <T> T getData(DataContainer.DataKey<T> key) {
      return data.get(key);
    }

  }

}
