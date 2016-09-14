package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;

public class HideCommand extends AbstractModifyCommand1 {

  public HideCommand(ArmorToolsPlugin plugin) {
    super(plugin, "hide", "hide [area]", Out.CMD_HIDE);
    this.addPermission(Permissions.HIDE);
  }

  @Override
  protected ModifyAction parseAction(Player player, int area) {

    if (area > 0) {
      
      return ModifyAction.area(area, a -> {
        a.setVisible(false);
        return true;
      });

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(a -> {
        a.setVisible(false);
        Out.CMD_HIDE_DONE.send(player);
        return true;
      });

    }

  }
}
