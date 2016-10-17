package de.mickare.armortools.event;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import de.mickare.armortools.command.armorstand.step.MoveCommand.MoveStepAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ArmorMoveEvent extends ArmorEvent implements Cancellable {

  private static @Getter final HandlerList handlerList = new HandlerList();

  private @Getter @NonNull final MoveStepAction action;
  private @Getter @NonNull final Vector moved;
  private @Getter @NonNull final Map<ArmorStand, Location> targetLocations;

  private @Getter @Setter @NonNull Result result = Result.NONE;

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

  @RequiredArgsConstructor
  public static class EndArmorMoveEvent extends ArmorEvent {

    private static @Getter final HandlerList handlerList = new HandlerList();

    private @Getter @NonNull final ArmorMoveEvent event;

    @Override
    public HandlerList getHandlers() {
      return handlerList;
    }

  }

  public static enum Result {
    NONE,
    CANCEL,
    STOP;
  }

  @Override
  public boolean isCancelled() {
    return result == Result.CANCEL;
  }

  @Override
  public void setCancelled(boolean cancel) {
    if (cancel) {
      if (result == Result.NONE) {
        result = Result.CANCEL;
      }
    } else {
      result = Result.NONE;
    }
  }

}
