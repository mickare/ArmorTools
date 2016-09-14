package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class ShowCommand extends AbstractModifyCommand1 {

  public ShowCommand(ArmorToolsPlugin plugin) {
    super(plugin, "show", "show [area]", Out.CMD_SHOW);
    this.addPermission(Permissions.SHOW);
  }

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {

      return ModifyAction.area(area, a -> {
        a.setVisible(true);
        return true;
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        a.setVisible(true);
        Out.CMD_SHOW_DONE.send(player);
        return true;
      });

    }

  }
}
