package de.mickare.armortools.command.armorstand;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.ArmorUtil;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.AbstractModifyCommand.ModifyAction;
import net.md_5.bungee.api.ChatColor;

public class MarkerCommand extends AbstractModifyCommand2 implements TabCompleter {

  public MarkerCommand(ArmorToolsPlugin plugin) {
    super(plugin, "marker", "marker [on|off] [area]", Out.CMD_MARKER);
    this.addPermission(Permissions.MARKER);
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

      final AtomicInteger gravity = new AtomicInteger(0);
      return ModifyAction.area(ModifyAction.Type.MARKER, area, a -> {
        if (a.hasGravity() && on) {
          a.setGravity(false);
          gravity.incrementAndGet();
        }
        ArmorUtil.setMarker(a, on);
      }).setFinish(() -> {
        if (gravity.get() > 0) {
          Out.CMD_MARKER_GRAVITY_TURNED_OFF_MULTI.send(player, gravity.get());
        }
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(ModifyAction.Type.MARKER, a -> {
        if (a.hasGravity() && on) {
          a.setGravity(false);
          Out.CMD_MARKER_GRAVITY_TURNED_OFF.send(player);
        }
        ArmorUtil.setMarker(a, on);
        // Out.CMD_MARKER_MODIFIED.send(player, (on ? "on" : "off"));
      });

    }

  }
}
