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
  protected ModifyAction createAction(Player player, int area) {

    if (area > 0) {

      return ModifyAction.area(ModifyAction.Type.SHOW, area, a -> {
        a.setVisible(true);
      });

    } else {

      return ModifyAction.click(ModifyAction.Type.SHOW, a -> {
        a.setVisible(true);
      });

    }

  }
}
