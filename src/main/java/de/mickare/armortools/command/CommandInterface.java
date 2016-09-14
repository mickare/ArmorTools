package de.mickare.armortools.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public interface CommandInterface extends CommandExecutor {

  public CommandInterface register();

  public PluginCommand getPluginCommand();

}
