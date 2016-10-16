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

  private @Getter @Setter boolean cancelled = false;

  private @Getter @NonNull final RotateStepAction action;
  private @Getter @NonNull final Map<ArmorStand, Location> targetLocations;

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

}
