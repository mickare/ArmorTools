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

public class ArmsCommand extends AbstractModifyCommand2 implements TabCompleter {

  public ArmsCommand(ArmorToolsPlugin plugin) {
    super(plugin, "arms", "arms [on|off] [area]", Out.CMD_ARMS);
    this.addPermission(Permissions.ARMS);
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
  protected ModifyAction createAction(Player player, String arg0, int area) {

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

      return ModifyAction.area(area, (a) -> a.setArms(on));

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click((action, armorstands) -> {
        armorstands.forEach(a -> a.setArms(on));
        Out.CMD_ARMS_MODIFIED.send(player, (on ? "on" : "off"));
        return armorstands.size();
      });

    }

  }

}
