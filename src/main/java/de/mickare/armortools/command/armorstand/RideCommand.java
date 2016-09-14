package de.mickare.armortools.command.armorstand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.ClickAction;
import de.mickare.armortools.ClickManager;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.AbstractCommandAndClick;
import de.mickare.armortools.util.Callback;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

public class RideCommand extends AbstractCommandAndClick<ArmorToolsPlugin> {

  public RideCommand(ArmorToolsPlugin plugin) {
    super(plugin, "ride", "ride", Out.CMD_RIDE.get());
    this.addPermission(Permissions.RIDE);
  }

  @Override
  public Callback<ArmorStand> getClickCallback(final Player player, final String[] args) {

    if (!Permissions.MODIFY.checkPermission(player)) {
      Out.PERMISSION_MISSING_MODIFY.send(player);
      return null;
    }

    return (armor) -> {
      if (!getPlugin().canModify(player, armor)) {
        Out.CMD_MODIFY_YOU_CANT_BUILD_HERE.send(player);
        return;
      }
      ClickManager.getInstance().addAction(player, new RiderClickAction(armor));
      Out.CMD_RIDE_CLICK_VEHICLE.send(player);
    };
  }

  private class RiderClickAction extends ClickAction<Entity> {

    private final @NonNull ArmorStand armorstand;

    public RiderClickAction(ArmorStand armorstand) {
      super(Entity.class);
      Preconditions.checkNotNull(armorstand);
      this.armorstand = armorstand;
    }

    @Override
    public void execute(Player player, Entity entity) {
      if (!this.armorstand.isValid()) {
        player.sendMessage(ChatColor.RED + "ArmorStand invalid or destroyed!");
        return;
      }
      if (!getPlugin().canModify(player, entity)) {
        Out.CMD_MODIFY_YOU_CANT_BUILD_HERE.send(player);
        return;
      }
      if (entity.isValid()) {
        entity.setPassenger(this.armorstand);
      }
    }



  }

}
