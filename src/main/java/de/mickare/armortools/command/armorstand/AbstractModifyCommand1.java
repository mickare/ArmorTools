package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractModifyCommand1 extends AbstractModifyCommand {

  public AbstractModifyCommand1(ArmorToolsPlugin plugin, String command, String usage,
      String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractModifyCommand1(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    this(plugin, command, usage, desc.toString());
  }
  
  @Override
  protected ModifyAction parseAction(Player player, String[] args) {

    int area = -1;
    if (args.length > 0) {
      try {
        area = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "area", args[0]);
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

    return parseAction(player, area);
  }

  protected abstract ModifyAction parseAction(Player player, int area);

}
