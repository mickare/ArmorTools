package de.mickare.armortools.command;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.command.armorstand.AbstractEulerAngleCommand;
import de.mickare.armortools.command.armorstand.ArmsCommand;
import de.mickare.armortools.command.armorstand.ChairCommand;
import de.mickare.armortools.command.armorstand.CloneCommand;
import de.mickare.armortools.command.armorstand.CountCommand;
import de.mickare.armortools.command.armorstand.GravityCommand;
import de.mickare.armortools.command.armorstand.HelmetCommand;
import de.mickare.armortools.command.armorstand.HelmetIDCommand;
import de.mickare.armortools.command.armorstand.HideCommand;
import de.mickare.armortools.command.armorstand.MarkerCommand;
import de.mickare.armortools.command.armorstand.MinifyClipboardCommand;
import de.mickare.armortools.command.armorstand.NameCommand;
import de.mickare.armortools.command.armorstand.PasteCommand;
import de.mickare.armortools.command.armorstand.PlateCommand;
import de.mickare.armortools.command.armorstand.ProtectCommand;
import de.mickare.armortools.command.armorstand.RideCommand;
import de.mickare.armortools.command.armorstand.RotateCommand;
import de.mickare.armortools.command.armorstand.ShowCommand;
import de.mickare.armortools.command.armorstand.SizeCommand;
import de.mickare.armortools.command.armorstand.hand.AbstractHandCommand;
import de.mickare.armortools.command.armorstand.hand.AbstractHandIDCommand;
import de.mickare.armortools.command.armorstand.move.FunMoveCommand;
import de.mickare.armortools.command.armorstand.move.MoveCommand;

public class MainArmorCommand extends AbstractMainMenuCommand<ArmorToolsPlugin> {

  public MainArmorCommand(ArmorToolsPlugin plugin) {
    super(plugin, plugin.getCommand("armor"));
  }

  @Override
  public MainArmorCommand register() {
    super.register();

    ArmorToolsPlugin plugin = this.getPlugin();

    // Euler Angle Commands
    this.setCommand(new AbstractEulerAngleCommand.BodyCommand(plugin));
    this.setCommand(new AbstractEulerAngleCommand.HeadCommand(plugin));
    this.setCommand(new AbstractEulerAngleCommand.LeftArmCommand(plugin));
    this.setCommand(new AbstractEulerAngleCommand.LeftLegCommand(plugin));
    this.setCommand(new AbstractEulerAngleCommand.RightArmCommand(plugin));
    this.setCommand(new AbstractEulerAngleCommand.RightLegCommand(plugin));


    this.setCommand(new ArmsCommand(plugin));
    this.setCommand(new ChairCommand(plugin));
    this.setCommand(new CountCommand(plugin));
    this.setCommand(new CloneCommand(plugin));
    this.setCommand(new GravityCommand(plugin));
    this.setCommand(new AbstractHandCommand.HandCommand(plugin));
    this.setCommand(new AbstractHandCommand.OffHandCommand(plugin));
    this.setCommand(new HelmetCommand(plugin));
    this.setCommand(new HideCommand(plugin));
    this.setCommand(new MarkerCommand(plugin));
    this.setCommand(new NameCommand(plugin));
    this.setCommand(new PasteCommand(plugin));
    this.setCommand(new PlateCommand(plugin));
    this.setCommand(new ProtectCommand(plugin));
    this.setCommand(new ShowCommand(plugin));
    this.setCommand(new SizeCommand(plugin));


    this.setCommand(new MoveCommand(plugin));
    this.setCommand(new RotateCommand(plugin));
    this.setCommand(new FunMoveCommand(plugin));
    this.setCommand(new RideCommand(plugin));

    this.setCommand(new AbstractHandIDCommand.HandIDCommand(plugin));
    this.setCommand(new AbstractHandIDCommand.OffHandIDCommand(plugin));
    this.setCommand(new HelmetIDCommand(plugin));

    if (plugin.getWorldEdit() != null) {
      this.setCommand(new MinifyClipboardCommand(plugin));
    } else {
      plugin.getLogger()
          .warning("Could not register MinifyClipboard-Command: WorldEdit not found.");
    }

    return this;
  }

}
