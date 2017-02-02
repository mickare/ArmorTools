package de.mickare.armortools.permission;

import java.util.Set;

import org.bukkit.permissions.Permissible;

import com.google.common.collect.Sets;

import de.mickare.armortools.Permission;
import de.mickare.armortools.Permissions;

public class PermissionAnd implements Permission {

  private final Set<Permission> delegates;

  public PermissionAnd(final Set<Permission> delegates) {
    this.delegates = Sets.newHashSet(delegates);
    this.delegates.remove(this);
  }

  public PermissionAnd(Permissions first, Permissions... others) {
    this.delegates = Sets.newHashSet(others);
    delegates.add(first);
  }

  @Override
  public boolean checkPermission(final Permissible permissible) {
    return !delegates.stream().filter(p -> !p.checkPermission(permissible)).findAny().isPresent();
  }

  @Override
  public boolean checkPermission(final Permissible permissible, final String extension) {
    return !delegates.stream().filter(p -> !p.checkPermission(permissible, extension)).findAny()
        .isPresent();
  }

}
