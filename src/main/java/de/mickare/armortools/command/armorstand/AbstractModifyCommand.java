package de.mickare.armortools.command.armorstand;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.AbstractCommandAndClick;
import de.mickare.armortools.event.ArmorEventFactory;
import de.mickare.armortools.event.ArmorstandModifyEvent;
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
  public Callback<ArmorStand> executeOrCallback(final Player player, String[] args) {

    if (!Permissions.MODIFY.checkPermission(player)) {
      Out.PERMISSION_MISSING_MODIFY.send(player);
      return null;
    }

    final ModifyAction action = parseAction(player, args);
    return executeAction(player, action);

  }

  private Callback<ArmorStand> executeAction(final Player player, final ModifyAction action) {
    if (action == null) {
      return null;
    }
    if (action.isArea()) {

      Set<ArmorStand> armorstands =
          player.getNearbyEntities(action.getAreaSize(), action.getAreaSize(), action.getAreaSize())//
              .stream()//
              .filter(e -> e instanceof ArmorStand)//
              .map(e -> (ArmorStand) e)//
              .filter(a -> getPlugin().canModify(player, a))//
              .collect(Collectors.toSet());

      executeAction(player, action, armorstands);

    } else {
      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return (armorstand) -> {
        if (!getPlugin().canModify(player, armorstand)) {
          Out.CMD_MODIFY_YOU_CANT_BUILD_HERE.send(player);
          return;
        }
        executeAction(player, action, Collections.singleton(armorstand));
      };

    }
    return null;
  }

  private void executeAction(Player player, ModifyAction action, Set<ArmorStand> armorstands) {
    ArmorstandModifyEvent event =
        ArmorEventFactory.callPreModifyEvent(player, action, armorstands);
    if (event.isCancelled()) {
      return;
    }

    int count = action.apply(event.getEntities());

    action.finish();

    ArmorEventFactory.callPostModifyEvent(event, count);

    if (action.isArea()) {
      Out.CMD_MODIFY_MULTI_ARMORSTANDS.send(player, count);
    } else {
      Out.CMD_MODIFY_DONE.send(player);
    }
  }

  protected abstract ModifyAction parseAction(Player player, String[] args);

  public static @RequiredArgsConstructor class ModifyAction {
    private final @Getter boolean area;
    private final @Getter int areaSize;
    private final @NonNull BiFunction<ModifyAction, Set<ArmorStand>, Integer> modifier;
    private final @Getter DataContainer data = new DataContainer();
    private Runnable finish = () -> {
    };

    public static ModifyAction click(BiFunction<ModifyAction, Set<ArmorStand>, Integer> modifier) {
      return new ModifyAction(false, 0, modifier);
    }

    public static ModifyAction click(final Consumer<ArmorStand> modifier) {
      return click((action, armorstands) -> {
        armorstands.forEach(modifier);
        return armorstands.size();
      });
    }

    public static ModifyAction area(int size,
        BiFunction<ModifyAction, Set<ArmorStand>, Integer> modifier) {
      return new ModifyAction(true, size, modifier);
    }

    public static ModifyAction area(int size, final Consumer<ArmorStand> modifier) {
      return area(size, (action, armorstands) -> {
        armorstands.forEach(modifier);
        return armorstands.size();
      });
    }

    public int apply(Set<ArmorStand> armor) {
      return modifier.apply(this, armor);
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

  }

}
