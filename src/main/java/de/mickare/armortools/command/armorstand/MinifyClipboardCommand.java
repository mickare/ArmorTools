package de.mickare.armortools.command.armorstand;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Location;

import de.mickare.armortools.ArmorToolsPlugin;
import de.mickare.armortools.Out;
import de.mickare.armortools.Permissions;
import de.mickare.armortools.command.AbstractCommand;
import de.mickare.armortools.util.BlockUtil;
import de.mickare.armortools.util.MathUtil;
import de.mickare.armortools.worldedit.WorldEditUtil;
import lombok.Data;
import lombok.NonNull;

public class MinifyClipboardCommand extends AbstractCommand<ArmorToolsPlugin> {

  private static final int[] SIZE = new int[] {6, 12, 14, 20};

  private static final double[] SIZE_FACTORS = new double[] {//
      SIZE[0] / 32d, //
      SIZE[1] / 32d, //
      SIZE[2] / 32d, //
      SIZE[3] / 32d};

  private static final boolean[] ARMS = new boolean[] {true, true, false, false};
  private static final boolean[] SMALL = new boolean[] {true, false, true, false};

  private static final EulerAngle[] ANGLES_ARM = new EulerAngle[] {//
      MathUtil.transformDegrees(31, 11, 44), //
      MathUtil.transformDegrees(11, 11, 44), //
      new EulerAngle(0, 0, 0), //
      new EulerAngle(0, 0, 0),//
  };

  private static final EulerAngle[] ANGLES_ARM_FLIPPED = new EulerAngle[] {//
      MathUtil.transformDegrees(120, 10, 45), //
      MathUtil.transformDegrees(100, 10, 45), //
      new EulerAngle(0, 0, 0), //
      new EulerAngle(0, 0, 0),//
  };

  private static final EulerAngle ANGLE_HEAD = new EulerAngle(0, 0, 0);
  private static final EulerAngle ANGLE_HEAD_FLIPPED = new EulerAngle(Math.PI, 0, 0);

  private static final Vector[] OFFSET_BLOCK = new Vector[4];
  private static final Vector[] OFFSET_BLOCK_FLIPPED = new Vector[4];

  private static final Vector[] OFFSET_STEP_UPPER = new Vector[] {//
      new Vector(0, SIZE_FACTORS[0] * 0.5, 0), //
      new Vector(0, SIZE_FACTORS[1] * 0.5, 0), //
      new Vector(0, SIZE_FACTORS[2] * 0.5, 0), //
      new Vector(0, SIZE_FACTORS[3] * 0.5, 0),//
  };

  static {

    // Smallest Corner (X,Y,Z lowest) in the block corner of original armorstand block
    // OFFSET_BLOCK[0] = new Vector(-17, -15, -14); // Hand Small
    // OFFSET_BLOCK[1] = new Vector(-21, -21, -12); // Hand Big
    OFFSET_BLOCK[0] = new Vector(-12.2, -15, -17.8); // Hand Small
    OFFSET_BLOCK[1] = new Vector(-8.3, -21, -20.5); // Hand Big
    OFFSET_BLOCK[2] = new Vector(-9, -23, -9); // Helmet Small
    OFFSET_BLOCK[3] = new Vector(-6, -44, -6); // Helmet big

    // Center X/Z in armorstands spawn middle (to rotate later around that point)
    for (int i = 0; i < SIZE.length; ++i) {
      int distToBlockCorner = (32 - SIZE[i]) / 2;
      OFFSET_BLOCK[i] = OFFSET_BLOCK[i].add(distToBlockCorner, 0, distToBlockCorner);
    }

    // Offsets of stairs that are upside down (flipped).
    // OFFSET_BLOCK_FLIPPED[0] = OFFSET_BLOCK[0].add(-3, -14, 0);
    // OFFSET_BLOCK_FLIPPED[1] = OFFSET_BLOCK[1].add(-6, -28, 0);
    OFFSET_BLOCK_FLIPPED[0] = OFFSET_BLOCK[0].add(0, -14, -3.1);
    OFFSET_BLOCK_FLIPPED[1] = OFFSET_BLOCK[1].add(0, -28, -6.5);
    OFFSET_BLOCK_FLIPPED[2] = OFFSET_BLOCK[2].add(0, 11, 0);
    OFFSET_BLOCK_FLIPPED[3] = OFFSET_BLOCK[3].add(0, 16, 0);

    // Normalize from 32 to 1
    for (int i = 0; i < OFFSET_BLOCK.length; ++i) {
      OFFSET_BLOCK[i] = OFFSET_BLOCK[i].divide(32);
    }
    // Normalize from 32 to 1
    for (int i = 0; i < OFFSET_BLOCK_FLIPPED.length; ++i) {
      OFFSET_BLOCK_FLIPPED[i] = OFFSET_BLOCK_FLIPPED[i].divide(32);
    }

  }



  public MinifyClipboardCommand(ArmorToolsPlugin plugin) {
    super(plugin, "minify", "minify [size=1-4]", Out.CMD_MINIFY_CLIPBOARD.get());
    this.addPermission(Permissions.MINIFY_CLIPBOARD);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (!(sender instanceof Player)) {
      Out.ERROR_CMD_ONLY_PLAYERS.send(sender);
      return false;
    }
    final Player player = (Player) sender;

    if (!Permissions.MODIFY.checkPermission(player)) {
      Out.PERMISSION_MISSING_MODIFY.send(player);
      return true;
    }

    if (!Permissions.MINIFY_CLIPBOARD.checkPermission(player)) {
      Out.PERMISSION_MISSING.send(player);
      return true;

    }

    int size = 1;
    if (args.length > 0) {
      try {
        size = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {
        Out.ARG_INVALID_INT_ONLY.send(player, "size", args[0]);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return true;
      }
      if (size < 1) {
        Out.ARG_INVALID_INT_MIN.send(player, "size", size, 1);
        player.sendMessage(ChatColor.RED + this.getUsage());
        return true;
      }
      if (size > 4) {
        Out.ARG_INVALID_INT_MAX.send(player, "size", size, 4);
        return true;
      }
    }

    LocalSession session = getPlugin().getWorldEdit().getSession(player);
    if (session == null) {
      Out.CMD_MINIFY_CLIPBOARD_EMPTY_CLIPBOARD.send(player);
      return true;
    }

    try {
      ClipboardHolder holder = session.getClipboard();
      if (holder == null) {
        Out.CMD_MINIFY_CLIPBOARD_EMPTY_CLIPBOARD.send(player);
        return true;
      }

      minify(player, session, holder, size);

    } catch (EmptyClipboardException e) {
      Out.CMD_MINIFY_CLIPBOARD_EMPTY_CLIPBOARD.send(player);
      return true;
    }


    return true;
  }

  // *****************************************************

  public static int convertMcRotationToClockRotation(int rot) {
    return ((rot & 0x1) << 1) | ((rot >> 1) & 0x1);
  }

  private static int mod(int v, int mod) {
    v %= mod;
    return v < 0 ? v + mod : v;
  }

  public static Vector rotate(int yaw, Vector v) {
    switch (mod(yaw, 360)) {
      case 0:
        return v;
      case 90:
        return new Vector(-v.getZ(), v.getY(), v.getX());
      case 180:
        return new Vector(-v.getX(), v.getY(), -v.getZ());
      case 270:
        return new Vector(v.getZ(), v.getY(), -v.getX());
      default:
        throw new IllegalStateException("Should not reach that part! yaw = " + yaw);
    }
  }

  private static @Data class ModelResult {
    private final @NonNull Vector offset;
    private final float yaw;
  }

  private static ModelResult calculatePosition(final int index, ArmorStand armor, BaseBlock block) {
    int yaw = 0;
    Vector offset = OFFSET_BLOCK[index];

    boolean flipped = false;

    if (BlockUtil.isStep(block)) {
      if ((block.getData()) % 16 >= 8) {
        offset = offset.add(OFFSET_STEP_UPPER[index]);
      }
    } else if (BlockUtil.isStairs(block)) {
      if ((block.getData() % 8) >= 4) {
        offset = OFFSET_BLOCK_FLIPPED[index];
        flipped = true;
      }
      yaw = 180 + 90 * convertMcRotationToClockRotation(block.getData());
    }

    if (ARMS[index]) {
      yaw += 90;
      armor.setHelmet(null);
      armor.setItemInHand(BlockUtil.toHandItem(block));
      if (!flipped) {
        armor.setRightArmPose(ANGLES_ARM[index]);
      } else {
        armor.setRightArmPose(ANGLES_ARM_FLIPPED[index]);
      }
    } else {
      armor.setItemInHand(null);
      armor.setHelmet(BlockUtil.toHelmetItem(block));
      if (!flipped) {
        armor.setHeadPose(ANGLE_HEAD);
      } else {
        armor.setHeadPose(ANGLE_HEAD_FLIPPED);
      }
    }

    yaw = mod(yaw, 360);
    offset = rotate(yaw, offset);

    return new ModelResult(offset, (float) yaw);
  }

  public void minify(Player player, LocalSession session, ClipboardHolder holder, int size) {
    try {

      ArmorStand temp = (ArmorStand) player.getWorld()//
          .spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
      temp.remove();
      final int index = size - 1;
      temp.setArms(ARMS[index]);
      temp.setSmall(SMALL[index]);
      temp.setGravity(false);

      // 4 = big head
      // 3 = small head
      // 2 = big hand
      // 1 = small hand

      final double size_factor = SIZE_FACTORS[index];

      Clipboard baked = WorldEditUtil.bake(holder);

      /*
       * 
       * Vector origin = baked.getOrigin();
       * 
       * Vector minp = baked.getMinimumPoint(); Vector maxp = baked.getMaximumPoint();
       * 
       * Vector dist = maxp.subtract(minp);
       * 
       * Vector toMin = baked.getMinimumPoint().subtract(origin).multiply(size_factor); Vector toMax
       * = toMin.add(dist.multiply(size_factor));
       * 
       * toMin = new Vector(Math.floor(toMin.getX() * 32) / 32, Math.floor(toMin.getX() * 32) / 32,
       * Math.floor(toMin.getX() * 32) / 32); toMax = new Vector(Math.ceil(toMax.getX() * 32) / 32,
       * Math.ceil(toMax.getX() * 32) / 32, Math.ceil(toMax.getX() * 32) / 32);
       * 
       * Vector newMinimum = origin.add(toMin); Vector newMaximum = origin.add(toMax);
       * 
       * System.out.println("Min: " + newMinimum.toString()); System.out.println("Max: " +
       * newMaximum.toString()); System.out.println("Ogn" + origin.toString());
       * 
       */

      CuboidRegion region = new CuboidRegion(baked.getMinimumPoint().subtract(1, 2, 1),
          baked.getMaximumPoint().add(1, 1, 1));
      BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
      clipboard.setOrigin(baked.getOrigin());

      int count = 0;

      final Vector min = baked.getMinimumPoint();
      final Vector max = baked.getMaximumPoint();
      final Vector centerBottom = baked.getRegion().getCenter().setY(min.getY());

      // System.out.println("min: " + min.toString());
      // System.out.println("max: " + max.toString());
      // System.out.println("org: " + baked.getOrigin());

      // System.out.println("minR: " + region.getMinimumPoint().toString());
      // System.out.println("maxR: " + region.getMaximumPoint().toString());

      for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
        for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
          for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
            Vector position = new Vector(x, y, z);
            BaseBlock block = baked.getBlock(position);
            if (!block.isAir()) {
              Material material = Material.getMaterial(block.getId());
              if (material.isBlock() && material.isSolid()) {

                block.getType();


                Vector pos =
                    position.subtract(centerBottom).multiply(size_factor).add(centerBottom);


                ModelResult result = calculatePosition(index, temp, block);
                pos = pos.add(result.getOffset());


                BaseEntity e =
                    WorldEditUtil.getBukkitImplAdapter(getPlugin().getWorldEdit()).getEntity(temp);
                Location loc = new Location(clipboard, pos, result.getYaw(), 0);



                if (!clipboard.getRegion().contains(pos)) {
                  Vector minR = clipboard.getRegion().getMinimumPoint();
                  Vector maxR = clipboard.getRegion().getMaximumPoint();

                  minR = new Vector(Math.min(minR.getX(), pos.getX()),
                      Math.min(minR.getY(), pos.getY()), Math.min(minR.getZ(), pos.getZ()));
                  maxR = new Vector(Math.max(maxR.getX(), pos.getX()),
                      Math.max(maxR.getY(), pos.getY()), Math.max(maxR.getZ(), pos.getZ()));

                  clipboard.getRegion()
                      .expand(minR.subtract(clipboard.getRegion().getMinimumPoint()));

                  clipboard.getRegion()
                      .expand(maxR.subtract(clipboard.getRegion().getMaximumPoint()));
                }



                // System.out.println(clipboard.getRegion().contains(loc.toVector()) + " - "
                // + loc.toVector().toString());

                clipboard.createEntity(loc, e);
                count++;
              }
            }
          }
        }
      }

      session.setClipboard(new ClipboardHolder(clipboard,
          BukkitUtil.getLocalWorld(player.getWorld()).getWorldData()));

      Out.CMD_MINIFY_CLIPBOARD_SUCCESS.send(player, count);
    } catch (Exception e) {
      Out.ERROR_INTERNAL.send(player);
      getPlugin().getLogger().log(Level.SEVERE, "Failed to minify clipboard: " + e.getMessage(), e);
    }

  }

  private static Vector rotate90CW(Vector org) {
    return new Vector(-org.getZ(), org.getY(), org.getX());
  }


  /*
   * private static final CompoundTag DEFAULT_ARMORSTAND;
   * 
   * static { CompoundTagBuilder tag = CompoundTagBuilder.create(); tag.putFloat("AbsorptionAmount",
   * 0); tag.putShort("Air", (short) 300); tag.putShort("DeathTime", (short) 0);
   * tag.putInt("Dimension", 0); tag.putInt("DisabledSlots", 0); tag.putFloat("FallDistance", 0);
   * tag.putShort("Fire", (short) -1); tag.putFloat("HealF", 20f); tag.putShort("Health", (short)
   * 20); tag.putInt("HurtByTimestamp", 0); tag.putInt("HurtTime", 0); tag.putString("id",
   * "ArmorStand"); tag.putByte("Invisible", (byte) 0); tag.putByte("Invulnerable", (byte) 0);
   * tag.putByte("NoBasePlate", (byte) 0); tag.putByte("NoGravity", (byte) 0);
   * tag.putByte("OnGround", (byte) 1); tag.putInt("PortalCooldown", 1); tag.putByte("ShowArms",
   * (byte) 0); tag.putByte("Silent", (byte) 1); tag.putByte("Small", (byte) 0);
   * 
   * 
   * CompoundTagBuilder lposes = CompoundTagBuilder.create(); lposes.put("Body", new ListTag("Body",
   * FloatTag.class, Lists.newArrayList(new FloatTag(0), new FloatTag(0), new FloatTag(0))));
   * lposes.put("Head", new ListTag("Head", FloatTag.class, Lists.newArrayList(new FloatTag(0), new
   * FloatTag(0), new FloatTag(0)))); tag.put("Pose", lposes.build());
   * 
   * tag.put("Equipment", new ListTag("Equipment", CompoundTag.class,
   * Lists.newArrayList(CompoundTagBuilder.create().build(), CompoundTagBuilder.create().build(),
   * CompoundTagBuilder.create().build(), CompoundTagBuilder.create().build(),
   * CompoundTagBuilder.create().build())));
   * 
   * 
   * tag.put("Motion", new ListTag("Motion", DoubleTag.class, Lists.newArrayList(new DoubleTag(0d),
   * new DoubleTag(0d), new DoubleTag(0d)))); tag.put("Rotation", new ListTag("Rotation",
   * FloatTag.class, Lists.newArrayList(new FloatTag(-180f), new FloatTag(0f))));
   * 
   * DEFAULT_ARMORSTAND = tag.build(); }
   * 
   * private static CompoundTag create(ItemStack item) { CompoundTagBuilder tag =
   * CompoundTagBuilder.create(); tag.putByte("Count", (byte) item.getAmount());
   * tag.putShort("Damage", item.getDurability()); tag.putString("id", item.getType().name());
   * return tag.build(); }
   * 
   * private static CompoundTag createArmorStand(Vector pos, boolean arms, boolean small,
   * Map<String, EulerAngle> poses, ItemStack[] equips) { //Preconditions.checkArgument(poses.size()
   * > 0); Preconditions.checkArgument(equips.length == 5);
   * 
   * CompoundTagBuilder tag =CompoundTagBuilder.create(); tag.putAll(DEFAULT_ARMORSTAND.getValue());
   * 
   * tag.putByte("ShowArms", (byte) (arms ? 1 : 0)); tag.putByte("Small", (byte) (small ? 1 : 0));
   * 
   * UUID uuid = UUID.randomUUID(); tag.putLong("UUIDLeast", uuid.getLeastSignificantBits());
   * tag.putLong("UUIDMost", uuid.getMostSignificantBits());
   * 
   * CompoundTagBuilder lposes = CompoundTagBuilder.create(); lposes.put("Body", new ListTag("Body",
   * FloatTag.class, Lists.newArrayList(new FloatTag(0), new FloatTag(0), new FloatTag(0))));
   * lposes.put("Head", new ListTag("Head", FloatTag.class, Lists.newArrayList(new FloatTag(0), new
   * FloatTag(0), new FloatTag(0))));
   * 
   * for(Entry<String, EulerAngle> pose : poses.entrySet()) { EulerAngle a = pose.getValue();
   * lposes.put(pose.getKey(), new ListTag(pose.getKey(), FloatTag.class, Lists.newArrayList(new
   * FloatTag((float) a.getX()), new FloatTag((float) a.getY()), new FloatTag((float) a.getZ()))));
   * }
   * 
   * tag.put("Pose", lposes.build());
   * 
   * 
   * 
   * 
   * tag.put("Equipment", new ListTag("Equipment", CompoundTag.class,
   * Lists.newArrayList(CompoundTagBuilder.create().build(), CompoundTagBuilder.create().build(),
   * CompoundTagBuilder.create().build(), CompoundTagBuilder.create().build(),
   * CompoundTagBuilder.create().build())));
   * 
   * List<CompoundTag> lequips = Lists.newArrayListWithExpectedSize(5); for (int i = 0; i < 5; ++i)
   * { equips[i] tag.put(key, value) } tag.put("Equipment", new ListTag(CompoundTag.class,
   * lequips));
   * 
   * return tag.build(); }
   */



}
