package de.mickare.armortools.event;

import java.util.Collection;
import java.util.Set;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ArmorstandModifyEvent extends ArmorEvent implements Cancellable {

  private static @Getter final HandlerList handlerList = new HandlerList();

  private @Getter @Setter boolean cancelled = false;

  private @Getter @NonNull final Player player;
  private @Getter @NonNull final ModifyAction action;
  private @Getter @NonNull final Set<ArmorStand> entities;


  public ArmorstandModifyEvent(Player player, ModifyAction action,
      Collection<ArmorStand> entities) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(action);
    Preconditions.checkNotNull(entities);
    this.player = player;
    this.action = action;
    this.entities = Sets.newHashSet(entities);
  }


  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }


  public static class PostArmorstandModifyEvent extends ArmorEvent {

    private static @Getter final HandlerList handlerList = new HandlerList();

    private @Getter final ArmorstandModifyEvent preEvent;
    private @Getter final int count;

    public PostArmorstandModifyEvent(ArmorstandModifyEvent preEvent, int count) {
      this.preEvent = preEvent;
      this.count = count;
    }

    public Player getPlayer() {
      return preEvent.getPlayer();
    }

    public ModifyAction getAction() {
      return preEvent.getAction();
    }

    @Override
    public HandlerList getHandlers() {
      return handlerList;
    }

  }

}
