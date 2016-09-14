package de.mickare.armortools.worldedit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.CombinedTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.registry.WorldData;

/**
 * Helper class to 'bake' a transform into a clipboard.
 *
 * <p>
 * This class needs a better name and may need to be made more generic.
 * </p>
 *
 * https://github.com/sk89q/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/command/FlattenedClipboardTransform.java
 * https://github.com/sk89q/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/command/SchematicCommands.java#L154
 *
 * @see Clipboard
 * @see Transform
 */
class FlattenedClipboardTransform {
	
	private final Clipboard original;
	private final Transform transform;
	private final WorldData worldData;
	
	/**
	 * Create a new instance.
	 *
	 * @param original
	 *            the original clipboard
	 * @param transform
	 *            the transform
	 * @param worldData
	 *            the world data instance
	 */
	public FlattenedClipboardTransform( Clipboard original, Transform transform, WorldData worldData ) {
		Preconditions.checkNotNull( original );
		Preconditions.checkNotNull( transform );
		Preconditions.checkNotNull( worldData );
		this.original = original;
		this.transform = transform;
		this.worldData = worldData;
	}
	
	/**
	 * Get the transformed region.
	 *
	 * @return the transformed region
	 */
	public Region getTransformedRegion() {
		Region region = original.getRegion();
		Vector minimum = region.getMinimumPoint();
		Vector maximum = region.getMaximumPoint();
		
		Transform transformAround = new CombinedTransform( new AffineTransform().translate( original.getOrigin().multiply( -1 ) ), transform, new AffineTransform().translate( original.getOrigin() ) );
		
		Vector[] corners = new Vector[] { minimum, maximum, minimum.setX( maximum.getX() ), minimum.setY( maximum.getY() ), minimum.setZ( maximum.getZ() ), maximum.setX( minimum.getX() ), maximum.setY( minimum.getY() ), maximum.setZ( minimum.getZ() ) };
		
		for ( int i = 0; i < corners.length; i++ ) {
			corners[i] = transformAround.apply( corners[i] );
		}
		
		Vector newMinimum = corners[0];
		Vector newMaximum = corners[0];
		
		for ( int i = 1; i < corners.length; i++ ) {
			newMinimum = Vector.getMinimum( newMinimum, corners[i] );
			newMaximum = Vector.getMaximum( newMaximum, corners[i] );
		}
		
		// After transformation, the points may not really sit on a block,
		// so we should expand the region for edge cases
		newMinimum = newMinimum.setX( Math.floor( newMinimum.getX() ) );
		newMinimum = newMinimum.setY( Math.floor( newMinimum.getY() ) );
		newMinimum = newMinimum.setZ( Math.floor( newMinimum.getZ() ) );
		
		newMaximum = newMaximum.setX( Math.ceil( newMaximum.getX() ) );
		newMaximum = newMaximum.setY( Math.ceil( newMaximum.getY() ) );
		newMaximum = newMaximum.setZ( Math.ceil( newMaximum.getZ() ) );
		
		return new CuboidRegion( newMinimum, newMaximum );
	}
	
	/**
	 * Create an operation to copy from the original clipboard to the given extent.
	 *
	 * @param target
	 *            the target
	 * @return the operation
	 */
	public Operation copyTo( Extent target ) {
		BlockTransformExtent extent = new BlockTransformExtent( original, transform, worldData.getBlockRegistry() );
		ForwardExtentCopy copy = new ForwardExtentCopy( extent, original.getRegion(), original.getOrigin(), target, original.getOrigin() );
		copy.setTransform( transform );
		return copy;
	}
	
	/**
	 * Create a new instance to bake the transform with.
	 *
	 * @param original
	 *            the original clipboard
	 * @param transform
	 *            the transform
	 * @param worldData
	 *            the world data instance
	 * @return a builder
	 */
	public static FlattenedClipboardTransform transform( Clipboard original, Transform transform, WorldData worldData ) {
		return new FlattenedClipboardTransform( original, transform, worldData );
	}
	
}