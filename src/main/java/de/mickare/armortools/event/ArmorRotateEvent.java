package de.mickare.armortools.event;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.mickare.armortools.command.armorstand.step.RotateCommand.RotateStepAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ArmorRotateEvent extends ArmorEvent implements Cancellable {

  private static @Getter final HandlerList handlerList = new HandlerList();

  private @Getter @NonNull final RotateStepAction action;
  private @Getter @NonNull final Map<ArmorStand, Location> targetLocations;

  private @Getter @Setter @NonNull Result result = Result.NONE;

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }


  @RequiredArgsConstructor
  public static class EndArmorRotateEvent extends ArmorEvent {

    private static @Getter final HandlerList handlerList = new HandlerList();

    private @Getter @NonNull final ArmorRotateEvent event;

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
