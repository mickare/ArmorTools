package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.Player;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.armorstand.CloneCommand.ArmorSetting;

public class PasteCommand extends AbstractModifyCommand1 {

  public PasteCommand(ArmorToolsPlugin plugin) {
    super(plugin, "paste", "paste [area]", Out.CMD_PASTE);
    this.addPermission(Permissions.PASTE);
  }

  @Override
  protected ModifyAction createAction(Player player, int area) {

    final ArmorSetting setting = CloneCommand.SETTINGS.get(player);
    if (setting == null) {
      Out.CMD_PASTE_MISSING_CLONE.send(player);
      return null;
    }

    if (area > 0) {

      return ModifyAction.area(ModifyAction.Type.PASTE, area, setting);

    } else {

      Out.CMD_MODIFY_HIT.send(player, this.getCommand());

      return ModifyAction.click(ModifyAction.Type.PASTE, (action, armorstands) -> {
        armorstands.forEach(setting);
        Out.CMD_PASTE_DONE.send(player);
        return armorstands.size();
      });

    }

  }
}
