package de.mickare.armortools.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

import de.mickare.armortools.ArmorClickAction;
import de.mickare.armortools.ClickManager;
import de.mickare.armortools.Out;
import de.mickare.armortools.util.Callback;

public abstract class AbstractCommandAndClick<P extends JavaPlugin> extends AbstractCommand<P> {

  public AbstractCommandAndClick(P plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  @Override
  public final boolean onCommand(final CommandSender sender, final Command cmd, final String label,
      final String[] args) {
    Preconditions.checkNotNull(sender);
    if (!(sender instanceof Player)) {
      Out.ERROR_CMD_ONLY_PLAYERS.send(sender);
      return false;
    }

    final Callback<ArmorStand> c = this.executeOrCallback((Player) sender, args);
    if (c != null) {
      ClickManager.getInstance().addAction((Player) sender, new CommandClickAction(c));
    }
    
    return true;
  }

  public abstract Callback<ArmorStand> executeOrCallback(Player player, String[] args);

  private static class CommandClickAction extends ArmorClickAction {

    private final Callback<ArmorStand> c;

    public CommandClickAction(Callback<ArmorStand> c) {
      this.c = c;
    }

    @Override
    public void execute(Player player, ArmorStand armorstand) {
      c.call(armorstand);
    }

  }

}
