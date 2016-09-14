package de.mickare.armortools.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.mickare.armortools.Out;
import de.mickare.armortools.util.RS_StringUtils;

public abstract class AbstractMenuCommand<P extends JavaPlugin> extends AbstractCommand<P>
    implements TabCompleter {

  private ImmutableMap<String, AbstractCommand<?>> commands = ImmutableMap.of();

  public AbstractMenuCommand(P plugin, String command, String usage, String desc) {
    super(plugin, command, usage, desc);
  }

  private AbstractCommand<? extends JavaPlugin> getCommand(String cmdstr) {
    String c = cmdstr.toLowerCase();
    if (this.commands.containsKey(c)) {
      return this.commands.get(c);
    }
    return null;
  }

  public void setCommand(AbstractCommand<?> s) {
    Preconditions.checkNotNull(s);
    Map<String, AbstractCommand<? extends JavaPlugin>> maps = Maps.newHashMap();
    maps.putAll(commands);
    maps.put(s.getCommand().toLowerCase(), s);
    this.commands = ImmutableMap.copyOf(maps);
  }

  public void removeCommand(String command) {
    Preconditions.checkNotNull(command);
    Map<String, AbstractCommand<?>> maps = Maps.newHashMap();
    maps.putAll(commands);
    maps.remove(command.toLowerCase());
    this.commands = ImmutableMap.copyOf(maps);
  }

  public Map<String, ? extends AbstractCommand<?>> getCommands() {
    return this.commands;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    String subcmd = "help";
    if (args.length > 0) {
      subcmd = args[0];
    }

    if (subcmd.equalsIgnoreCase("help") || subcmd.equalsIgnoreCase("hilfe") || subcmd.equals("?")) {
      StringBuilder sb = new StringBuilder();
      sb.append(Out.CMD_MENU_HELP
          .get(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, this.getCommand())));

      int count = 0;

      List<AbstractCommand<? extends JavaPlugin>> cmds = Lists.newArrayList(this.commands.values());
      Collections.sort(cmds, (a, b) -> a.getCommand().compareTo(b.getCommand()));

      for (AbstractCommand<? extends JavaPlugin> sc : cmds) {
        if (sc.checkPermission(sender)) {
          sb.append("\n");//
          sb.append(Out.CMD_MENU_HELP_LINE.get(sc.getUsage(), sc.getDescription()));
          count++;
        }
      }
      if (count == 0) {
        sb.append(Out.CMD_MENU_HELP_NONE.get());
      }
      sender.sendMessage(sb.toString());
      return true;
    }

    AbstractCommand<? extends JavaPlugin> acom = getCommand(subcmd);

    if (acom != null) {

      if (!acom.checkPermission(sender)) {
        Out.PERMISSION_MISSING.send(sender);
        return true;
      }

      final String[] subargs = Arrays.copyOfRange(args, 1, args.length);
      try {
        return acom.onCommand(sender, cmd, label, subargs);
      } catch (Exception e) {
        this.getPlugin().getLogger()
            .severe("\n" + e.getClass().getName() + ": " + e.getMessage() + "\n Arguments: \""
                + RS_StringUtils.join(subargs, " ") + "\"\n"
                + RS_StringUtils.stackTraceToString(e));
        Out.ERROR_INTERNAL.send(sender);
        return true;
      }
    }

    Out.CMD_MENU_UNKNOWN.send(sender, subcmd, this.getCommand());

    return true;
  }


  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    if (args.length == 0) {
      return Lists.newArrayList(this.commands.keySet());
    } else if (args.length == 1) {
      String search = args[0].toLowerCase();
      List<String> best = Lists.newArrayList();
      this.commands.values().stream().map(c -> c.getCommand().toLowerCase())
          .filter(c -> c.contains(search)).forEach(best::add);;
      return best;
    } else {

      AbstractCommand<?> cmd = this.getCommand(args[0]);
      if (cmd != null && cmd instanceof TabCompleter) {
        return ((TabCompleter) cmd).onTabComplete(sender, command, alias,
            Arrays.copyOfRange(args, 1, args.length));
      }

    }
    return null;
  }

}
