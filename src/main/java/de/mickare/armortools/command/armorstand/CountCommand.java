package de.mickare.armortools.command.armorstand;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class CountCommand extends AbstractModifyCommand1 implements TabCompleter {

  public CountCommand(ArmorToolsPlugin plugin) {
    super(plugin, "count", "count [area]", Out.CMD_COUNT);
    this.addPermission(Permissions.COUNT);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    return null;
  }

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {

      double sqrdArea = area * area;

      final Location ploc = player.getLocation();

      long count = player.getNearbyEntities(area, area, area).stream()
          .filter(e -> e.getType() == EntityType.ARMOR_STAND
              && e.getLocation().distanceSquared(ploc) <= sqrdArea)//
          .count();

      Out.CMD_COUNT_COUNTED_RADIUS.send(player, count, area);

    } else {

      long count = Arrays.stream(player.getLocation().getChunk().getEntities())
          .filter(e -> e.getType() == EntityType.ARMOR_STAND).count();

      Out.CMD_COUNT_COUNTED_CHUNK.send(player, count);

    }

    return null;
  }
}
