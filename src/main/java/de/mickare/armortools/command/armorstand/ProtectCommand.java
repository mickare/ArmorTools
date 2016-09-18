package de.mickare.armortools.command.armorstand;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.ArmorUtil;
import de.mickare.armortools.DisabledPart;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import net.md_5.bungee.api.ChatColor;

public class ProtectCommand extends AbstractModifyCommand3 implements TabCompleter {

  private final static ImmutableList<String> PARTS = ImmutableList
      .copyOf(Stream.of(DisabledPart.values()).map(e -> e.name().toLowerCase()).iterator());

  public ProtectCommand(ArmorToolsPlugin plugin) {
    super(plugin, "protect", "protect [on|off] [" + String.join("|", PARTS) + "] [area]",
        Out.CMD_PROTECT);
    this.addPermission(Permissions.PROTECT);
  }


  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    if (args.length == 1) {
      return Lists.newArrayList("on", "off");
    }
    if (args.length == 2) {
      final String search = args[1].toLowerCase();
      return PARTS.stream().filter(p -> p.contains(search)).collect(Collectors.toList());
    }
    return null;
  }

  @Override
  protected ModifyAction parseAction(Player player, String arg0, String arg1, int area) {

    if (arg0 == null || arg1 == null) {
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

    final DisabledPart part;
    try {
      part = DisabledPart.valueOf(arg1.toUpperCase());
    } catch (Exception e) {
      Out.ARG_INVALID.send(player, arg1);
      player.sendMessage(ChatColor.RED + this.getUsage());
      return null;
    }

    if (!this.checkPermission(player, part.name().toLowerCase())) {
      Out.PERMISSION_MISSING_PROTECT_PART.send(player, part.name().toLowerCase());
      return null;
    }

    if (area > 0) {

      return ModifyAction.area(area, a -> {
        ArmorUtil.setProtected(a, on, part);
        return true;
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        ArmorUtil.setProtected(a, on, part);
        Out.CMD_PROTECT_MODIFIED.send(player, (on ? "on" : "off"), part.name().toLowerCase());
        return true;
      });

    }

  }
}
