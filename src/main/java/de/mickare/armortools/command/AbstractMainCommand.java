package de.mickare.armortools.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractMainCommand<P extends JavaPlugin> extends AbstractCommand<P>
    implements CommandInterface {

  private final PluginCommand command;

  public AbstractMainCommand(P plugin, PluginCommand command) {
    super(plugin, command.getName(), command.getUsage(), command.getDescription());
    //
    this.command = command;
  }

  public AbstractMainCommand<P> register() {
    command.setExecutor(this);
    return this;
  }

  public PluginCommand getPluginCommand() {
    return command;
  }

}
