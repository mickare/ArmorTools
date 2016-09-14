package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractModifyCommand2 extends AbstractModifyCommand {

  public AbstractModifyCommand2(ArmorToolsPlugin plugin, String command, String usage,
      String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractModifyCommand2(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    this(plugin, command, usage, desc.toString());
  }

  @Override
  protected ModifyAction parseAction(Player player, String[] args) {
    String arg0 = null;
    if (args.length > 0) {
      arg0 = args[0];
    }

    int area = -1;
    if (args.length > 1) {
      try {
        area = Integer.parseInt(args[1]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "area", args[1]);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return null;
      }
      if (area <= 0) {
        Out.ARG_INVALID_INT_MIN.send(player, "area", area, 1);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return null;
      }
      if (area > ArmorToolsPlugin.MAX_AREA) {
        Out.ARG_INVALID_INT_MAX.send(player, "area", area, ArmorToolsPlugin.MAX_AREA);
        return null;
      }
      if (!this.checkPermission(player, "area")) {
        Out.PERMISSION_MISSING_AREA.send(player);
        return null;
      }
    }

    return parseAction(player, arg0, area);
  }

  protected abstract ModifyAction parseAction(Player player, String arg0, int area);

}
