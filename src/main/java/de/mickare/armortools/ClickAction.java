package de.mickare.armortools;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public abstract @RequiredArgsConstructor class ClickAction<E extends Entity> {

  private @Getter @Setter boolean done = false;

  private final @NonNull Class<E> type;

  public boolean isApplicable(Entity entity) {
    return type.isInstance(entity);
  }

  @SuppressWarnings("unchecked")
  protected boolean castExecute(Player player, Entity entity) {
    if (isApplicable(entity)) {
      execute(player, (E) entity);
      return true;
    }
    return false;
  }

  public abstract void execute(Player player, E entity);

}
