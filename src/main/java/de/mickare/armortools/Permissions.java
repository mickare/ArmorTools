package de.mickare.armortools;

import org.bukkit.permissions.Permissible;

import com.google.common.base.Preconditions;

public enum Permissions implements Permission {

  ARMORSTAND("armortools"),

  MODIFY(ARMORSTAND, "modify"),

  ANGLE(ARMORSTAND, "angle"),
  ANGLE_LEFTARM(ANGLE, "leftarm"),
  ANGLE_RIGHTARM(ANGLE, "rightarm"),
  ANGLE_LEFTLEG(ANGLE, "leftleg"),
  ANGLE_RIGHTLEG(ANGLE, "rightleg"),
  ANGLE_BODY(ANGLE, "body"),
  ANGLE_HEAD(ANGLE, "head"),

  ARMS(ARMORSTAND, "arms"),
  CHAIR(ARMORSTAND, "chair"),
  COUNT(ARMORSTAND, "count"),
  CLONE(ARMORSTAND, "clone"),
  GRAVITY(ARMORSTAND, "gravity"),
  HAND(ARMORSTAND, "hand"),
  OFFHAND(ARMORSTAND, "offhand"),
  HELMET(ARMORSTAND, "helmet"),
  HIDE(ARMORSTAND, "hide"),
  MARKER(ARMORSTAND, "marker"),
  NAME(ARMORSTAND, "name"),
  PASTE(ARMORSTAND, "paste"),
  PLATE(ARMORSTAND, "plate"),
  PROTECT(ARMORSTAND, "protect"),
  ROTATE(ARMORSTAND, "rotate"),
  SHOW(ARMORSTAND, "show"),
  SIZE(ARMORSTAND, "size"),

  MOVE(ARMORSTAND, "move"),
  FUNMOVE(ARMORSTAND, "funmove"),

  RIDE(ARMORSTAND, "RIDE"),

  MINIFY_CLIPBOARD(ARMORSTAND, "minifyclipboard"),

  HAND_ID(HAND, "id"),
  HELMET_ID(HELMET, "id");

  private final String permission;


  private Permissions(Permissions parent, String permission) {
    this(parent + "." + permission);
  }

  private Permissions(String permission) {
    Preconditions.checkArgument(permission.length() > 0);
    this.permission = permission.toLowerCase();
  }

  public String toString() {
    return permission;
  }

  @Override
  public boolean checkPermission(Permissible permissible) {
    return permissible.hasPermission(permission);
  }

  @Override
  public boolean checkPermission(Permissible permissible, String extension) {
    return permissible.hasPermission(permission + "." + extension.toLowerCase());
  }

}
