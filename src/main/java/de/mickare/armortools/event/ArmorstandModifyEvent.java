package de.mickare.armortools.event;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ArmorstandModifyEvent extends EntityEvent implements Cancellable {


  private static @Getter final HandlerList handlerList = new HandlerList();


  private @Getter @NonNull final Player player;

  private @Getter @Setter boolean cancelled = false;

  public ArmorstandModifyEvent(Player player, ArmorStand armorstand) {
    super(armorstand);
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(armorstand);
    this.player = player;
  }

  public ArmorStand getEntity() {
    return (ArmorStand) super.getEntity();
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

}
