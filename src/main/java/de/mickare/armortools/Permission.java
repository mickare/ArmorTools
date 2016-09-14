package de.mickare.armortools;

import org.bukkit.permissions.Permissible;

public interface Permission {

  boolean checkPermission(Permissible permissible);
  
  boolean checkPermission(Permissible permissible, String extension);
  
}
