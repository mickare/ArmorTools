package de.mickare.armortools.command.armorstand;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.util.RS_StringUtils;
import net.md_5.bungee.api.ChatColor;

public class NameCommand extends AbstractModifyCommand {

  private final static Pattern CHATCOLOR_COLOR = Pattern.compile("&([0-9abcdefr])");
  private final static Pattern CHATCOLOR_FORMAT = Pattern.compile("&([lmnor])");
  private final static Pattern CHATCOLOR_MAGIC = Pattern.compile("&([k])");

  private static String translateColor(Pattern pattern, String str) {
    Matcher m = pattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    int last = 0;
    while (m.find()) {
      sb.append(str, last, m.start());
      String g = m.group();
      if (g != null) {
        sb.append(ChatColor.COLOR_CHAR).append(g.charAt(1));
      }
      last = m.end();
    }
    sb.append(str, last, str.length());
    return sb.toString();
  }

  public NameCommand(ArmorToolsPlugin plugin) {
    super(plugin, "name", "name [name] [area]", Out.CMD_NAME);
    this.addPermission(Permissions.NAME);
  }

  @Override
  protected ModifyAction parseAction(final Player player, String[] args) {

    int area = -1;
    if (args.length > 0) {
      try {
        area = Integer.parseInt(args[args.length - 1]);
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
      } catch (NumberFormatException nfe) {
      }
    }

    String name = null;

    if (args.length > 0) {
      String[] nameArgs = args;
      if (area > 0) {
        nameArgs = Arrays.copyOfRange(args, 0, args.length - 1);
      }
      if (nameArgs.length > 0) {
        name = RS_StringUtils.join(nameArgs, " ");
      }
    }

    if (name != null && name.length() > 32) {
      Out.ARG_INVALID_NAME_LENGTH.send(player, 32);
      return null;
    }

    if (name != null) {
      // name = ChatColor.translateAlternateColorCodes( '&', name );
      if (checkPermission(player, "color")) {
        name = translateColor(CHATCOLOR_COLOR, name);
      }
      if (checkPermission(player, "format")) {
        name = translateColor(CHATCOLOR_FORMAT, name);
      }
      if (checkPermission(player, "magic")) {
        name = translateColor(CHATCOLOR_MAGIC, name);
      }
    }


    if (area <= 0) {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      final String fname = name;
      if (name != null) {
        return ModifyAction.click(a -> {
          a.setCustomName(fname);
          a.setCustomNameVisible(true);
          Out.CMD_NAME_SET.send(player, fname);
        });
      } else {
        return ModifyAction.click(a -> {
          a.setCustomName(fname);
          a.setCustomNameVisible(false);
          Out.CMD_NAME_REMOVED.send(player);
        });
      }
    } else {

      final String fname = name;
      if (name != null) {
        return ModifyAction.area(area, a -> {
          a.setCustomName(fname);
          a.setCustomNameVisible(true);
        });
      } else {
        return ModifyAction.area(area, a -> {
          a.setCustomName(fname);
          a.setCustomNameVisible(false);
        });
      }

    }

  }
}
