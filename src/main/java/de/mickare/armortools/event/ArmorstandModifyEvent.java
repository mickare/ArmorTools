package de.mickare.armortools.event;

import java.util.Collection;
import java.util.Map;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ArmorstandModifyEvent extends ArmorEvent implements Cancellable {

  private static @Getter final HandlerList handlerList = new HandlerList();

  private @Getter @Setter boolean cancelled = false;

  private @Getter @NonNull final Player player;
  private @Getter @NonNull final ModifyAction action;
  private @Getter @NonNull final Map<Integer, ArmorStand> entities = Maps.newHashMap();


  public ArmorstandModifyEvent(Player player, ModifyAction action,
      Collection<ArmorStand> entities) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(action);
    Preconditions.checkNotNull(entities);
    this.player = player;
    this.action = action;
    addAllEntities(entities);
  }

  public void addAllEntities(Collection<ArmorStand> c) {
    c.forEach(a -> this.entities.put(a.getEntityId(), a));
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
