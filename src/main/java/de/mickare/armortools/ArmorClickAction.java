package de.mickare.armortools;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public abstract class ArmorClickAction extends ClickAction<ArmorStand> {

  public ArmorClickAction() {
    super(ArmorStand.class);
  }

  public abstract void execute(Player player, ArmorStand armorstand);

}
