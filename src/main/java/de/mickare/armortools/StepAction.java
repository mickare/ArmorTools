package de.mickare.armortools;

import org.bukkit.entity.Player;

public interface StepAction {

  boolean move(StepManager moveManager, Player player, int step);

}
