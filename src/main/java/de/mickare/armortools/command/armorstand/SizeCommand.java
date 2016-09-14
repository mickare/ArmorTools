package de.mickare.armortools.command.armorstand;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import net.md_5.bungee.api.ChatColor;

public class SizeCommand extends AbstractModifyCommand2 implements TabCompleter {

  public SizeCommand(ArmorToolsPlugin plugin) {
    super(plugin, "size", "size [on|off] [area]", Out.CMD_SIZE);
    this.addPermission(Permissions.SIZE);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    if (args.length == 1) {
      return Lists.newArrayList("on", "off");
    }
    return null;
  }

  @Override
  protected ModifyAction parseAction(Player player, String arg0, int area) {

    if (arg0 == null) {
      Out.ARG_MISSING.send(player);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }

    final boolean on;
    if (arg0.equalsIgnoreCase("on")) {
      on = true;
    } else if (arg0.equalsIgnoreCase("off")) {
      on = false;
    } else {
      Out.ARG_INVALID.send(player, arg0);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }


    if (area > 0) {

      return ModifyAction.area(area, a -> {
        a.setSmall(on);
        return true;
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        a.setSmall(on);
        Out.CMD_SIZE_MODIFIED.send(player, (on ? "small" : "big"));
        return true;
      });

    }

  }
}
