package de.mickare.armortools;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public enum Out {

  ERROR_CMD_ONLY_PLAYERS,
  ERROR_INTERNAL,

  PERMISSION_MISSING,
  PERMISSION_MISSING_EXTENSION,
  PERMISSION_MISSING_MODIFY,
  PERMISSION_MISSING_AREA,
  PERMISSION_MISSING_PROTECT_PART,

  ARG_MISSING,
  ARG_INVALID,
  ARG_INVALID_INT_ONLY,
  ARG_INVALID_INT_MIN,
  ARG_INVALID_INT_MAX,
  ARG_INVALID_NAME_LENGTH,
  ARG_INVALID_NO_MATERIAL,

  CLICK_ACTION_CANCELLED,
  STEP_ACTION_CANCELLED,
  
  CMD_MENU_HELP,
  CMD_MENU_HELP_LINE,
  CMD_MENU_HELP_NONE,
  CMD_MENU_UNKNOWN,

  CMD_MODIFY_MULTI_ARMORSTANDS,
  CMD_MODIFY_YOU_CANT_BUILD_HERE,
  CMD_MODIFY_HIT,
  CMD_MODIFY_DONE,
  
  CMD_ANGLE_LEFTARM,
  CMD_ANGLE_LEFTLEG,
  CMD_ANGLE_RIGHTARM,
  CMD_ANGLE_RIGHTLEG,
  CMD_ANGLE_BODY,
  CMD_ANGLE_HEAD,
  
  CMD_ANGLE_MODEHELP,
  CMD_ANGLE_MODE,
  
  CMD_ANGLE_INFO,
  CMD_INFO_HIT,
  
  CMD_ARMS,
  CMD_CHAIR,
  CMD_COUNT,
  CMD_CLONE,
  CMD_GRAVITY,
  CMD_HAND,
  CMD_OFFHAND,
  CMD_HELMET,
  CMD_HIDE,
  CMD_MARKER,
  CMD_NAME,
  CMD_PASTE,
  CMD_PLATE,
  CMD_PROTECT,
  CMD_ROTATE,
  CMD_SHOW,
  CMD_SIZE,
  
  CMD_MOVE,
  CMD_FUNMOVE,
  
  CMD_MINIFY_CLIPBOARD,
  CMD_RIDE,
  
  CMD_HAND_ID,
  CMD_OFFHAND_ID,
  CMD_HELMET_ID,
  
  CMD_HAND_SWITCHED,
  CMD_HELMET_SWITCHED,
  
  CMD_ARMS_MODIFIED,
  CMD_COUNT_COUNTED_CHUNK,
  CMD_COUNT_COUNTED_RADIUS,
  CMD_CHAIR_MODIFIED,
  CMD_GRAVITY_MODIFIED,
  CMD_MARKER_MODIFIED,
  CMD_MARKER_GRAVITY_TURNED_OFF,
  CMD_MARKER_GRAVITY_TURNED_OFF_MULTI,
  CMD_PLATE_MODIFIED,
  CMD_PROTECT_MODIFIED,
  CMD_SIZE_MODIFIED,

  CMD_CLONE_DONE,
  CMD_PASTE_DONE,
  CMD_PASTE_MISSING_CLONE,
  CMD_NAME_SET,
  CMD_NAME_REMOVED,
  
  CMD_HIDE_DONE,
  CMD_SHOW_DONE,
  
  CMD_ROTATE_START,
  CMD_ROTATE_AREA_EMPTY,
  CMD_ROTATE_EMPTY_VALID,
  
  CMD_MOVE_START,
  CMD_MOVE_AREA_EMPTY,
  CMD_MOVE_EMPTY_VALID,
  
  ARMOR_REMOVED_FELL_THROUGH_WORLD,
  
  CMD_MINIFY_CLIPBOARD_EMPTY_CLIPBOARD,
  CMD_MINIFY_CLIPBOARD_SUCCESS,
  
  CMD_RIDE_CLICK_VEHICLE,
  
  ;

  public String get(final Object... args) {
    return getMessage(this.name(), args);
  }

  public void send(final CommandSender receiver, final Object... args) {
    sendMessage(receiver, this.name(), args);
  }

  public String toString() {
    if (resource != null && resource.containsKey(this.name())) {
      return resource.getString(this.name());
    }
    return super.toString();
  }

  private static @Getter @Setter @NonNull Locale locale = Locale.getDefault();
  private static @Getter @NonNull ResourceBundle resource = null;
  private static final ThreadLocal<MessageFormat> format =
      ThreadLocal.withInitial(() -> new MessageFormat("", locale));

  public static void setResource(ResourceBundle resource) {
    for (Out o : Out.values()) {
      if (!resource.containsKey(o.name())) {
        System.err.println("bundle does not contain: " + o.name());
      }
    }
    Out.resource = resource;
  }

  public static String getMessage(final String key, final Object... args) {
    final String msg = resource.getString(key);
    if (args.length == 0) {
      return msg;
    }
    final MessageFormat f = format.get();
    f.applyPattern(msg);
    return f.format(args);
  }

  public static void sendMessage(final CommandSender receiver, final String key,
      final Object... args) {
    receiver.sendMessage(getMessage(key, args));
  }


}
