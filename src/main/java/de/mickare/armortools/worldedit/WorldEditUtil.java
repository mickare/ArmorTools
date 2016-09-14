package de.mickare.armortools.worldedit;

import java.lang.reflect.Method;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldEditUtil {

  private static Method we_getBukkitImplAdapter = null;

  public static BukkitImplAdapter getBukkitImplAdapter(WorldEditPlugin plugin) throws Exception {
    if (we_getBukkitImplAdapter == null) {
      we_getBukkitImplAdapter = WorldEditPlugin.class.getDeclaredMethod("getBukkitImplAdapter");
      we_getBukkitImplAdapter.setAccessible(true);
    }
    return (BukkitImplAdapter) we_getBukkitImplAdapter.invoke(plugin);
  }

  public static Clipboard bake(ClipboardHolder holder) throws WorldEditException {

    Clipboard clipboard = holder.getClipboard();
    Transform transform = holder.getTransform();
    Clipboard target;

    if (!transform.isIdentity()) {
      FlattenedClipboardTransform result =
          FlattenedClipboardTransform.transform(clipboard, transform, holder.getWorldData());
      target = new BlockArrayClipboard(result.getTransformedRegion());
      target.setOrigin(clipboard.getOrigin());
      Operations.completeLegacy(result.copyTo(target));
    } else {
      target = clipboard;
    }

    return target;
  }

}
