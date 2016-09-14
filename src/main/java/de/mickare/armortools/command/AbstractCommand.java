package de.mickare.armortools.command;

import java.util.Collections;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import de.mickare.armortools.Permission;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public abstract @RequiredArgsConstructor class AbstractCommand<P extends JavaPlugin>
    implements CommandExecutor {

  private final @NonNull @Getter P plugin;

  private final @NonNull @Getter String command;
  private @NonNull @Getter @Setter String usage, description;


  private final Set<Permission> permissions = Sets.newHashSet();

  public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

  protected boolean addPermission(Permission permission) {
    Preconditions.checkNotNull(permission);
    return permissions.add(permission);
  }

  protected boolean removePermission(Permission permission) {
    return permissions.remove(permission);
  }

  public Set<Permission> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }

  public boolean checkPermission(final Permissible permissible) {
    return permissions.parallelStream().filter(p -> p.checkPermission(permissible)).findAny()
        .isPresent();
  }

  public boolean checkPermission(final Permissible permissible, final String extension) {
    return permissions.parallelStream().filter(p -> p.checkPermission(permissible, extension))
        .findAny().isPresent();
  }

}
