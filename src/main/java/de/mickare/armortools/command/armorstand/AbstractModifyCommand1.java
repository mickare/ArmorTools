package de.mickare.armortools.command.armorstand;

import java.util.Optional;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.command.AbstractCommand;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractModifyCommand1 extends AbstractModifyCommand {

  public AbstractModifyCommand1(ArmorToolsPlugin plugin, String command, String usage,
      String desc) {
    super(plugin, command, usage, desc);
  }

  public AbstractModifyCommand1(ArmorToolsPlugin plugin, String command, String usage, Out desc) {
    this(plugin, command, usage, desc.toString());
  }

  public static Optional<Integer> parseArea(AbstractCommand<?> cmd, Player player, String[] args,
      int index) {
    int area = -1;
    if (args.length > index) {
      try {
        area = Integer.parseInt(args[index]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "area", args[index]);
        player.sendMessage(ChatColor.RED + cmd.getUsage());
        return Optional.empty();
      }
      if (area <= 0) {
        Out.ARG_INVALID_INT_MIN.send(player, "area", area, index + 1);
        player.sendMessage(ChatColor.RED + cmd.getUsage());
        return Optional.empty();
      }
      if (area > ArmorToolsPlugin.MAX_AREA) {
        Out.ARG_INVALID_INT_MAX.send(player, "area", area, ArmorToolsPlugin.MAX_AREA);
        return Optional.empty();
      }
      if (!cmd.checkPermission(player, "area")) {
        Out.PERMISSION_MISSING_AREA.send(player);
        return Optional.empty();
      }
    }
    return Optional.of(area);
  }

  @Override
  protected ModifyAction parseAction(Player player, String[] args) {

    Optional<Integer> area = parseArea(this, player, args, 0);
    if(!area.isPresent()) {
      return null;
    }

    return createAction(player, area.get());
  }

  protected abstract ModifyAction createAction(Player player, int area);

}
