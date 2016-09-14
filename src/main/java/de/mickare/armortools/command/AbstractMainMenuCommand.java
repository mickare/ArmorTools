package de.mickare.armortools.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractMainMenuCommand<P extends JavaPlugin> extends AbstractMenuCommand<P>
    implements CommandInterface {

  private final PluginCommand command;

  public AbstractMainMenuCommand(P plugin, PluginCommand command) {
    super(plugin, command.getName(), command.getUsage(), command.getDescription());
    //
    this.command = command;
  }

  public AbstractMainMenuCommand<P> register() {
    command.setExecutor(this);
    return this;
  }

  public PluginCommand getPluginCommand() {
    return command;
  }

}
