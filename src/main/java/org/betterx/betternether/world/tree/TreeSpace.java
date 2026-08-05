package org.betterx.betternether.world.tree;

import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import org.joml.Vector3f;

/**
 * Where a tree is being grown and how much room it has.
 *
 * <h2>Why the room matters</h2>
 * During world generation a feature may only touch the chunks its {@code ChunkStep} declares - the 3x3
 * around the one being decorated. A canopy that ignores that gets sliced off on a flat plane at the
 * boundary. {@link WriteZone} already knows how to <em>fit</em> geometry to the available room rather
 * than clip it; this record is the handle every shape in the library receives so it can do the fitting
 * before it commits to a size. Outside world generation (sapling growth, tests) the zone is
 * {@link WriteZone#UNBOUNDED} and every fit is a no-op.
 *
 * @param origin the block the tree grows from - tree-local {@code (0, 0, 0)}
 * @param zone   the horizontal box that may be touched
 */
public record TreeSpace(BlockPos origin, WriteZone zone) {
    public static TreeSpace of(LevelAccessor level, BlockPos origin) {
        return new TreeSpace(origin, WriteZone.of(level));
    }

    /**
     * An unbounded space, for tests and for growth in a live level.
     */
    public static TreeSpace unbounded(BlockPos origin) {
        return new TreeSpace(origin, WriteZone.UNBOUNDED);
    }

    /**
     * {@code radius} shrunk to what fits around a tree-local point, or a negative number when not even
     * {@code minRadius} fits.
     */
    public float fitRadius(Vector3f localCentre, float radius, float minRadius) {
        return zone.fitRadius(
                origin.getX() + Math.round(localCentre.x),
                origin.getZ() + Math.round(localCentre.z),
                radius, minRadius
        );
    }

    /**
     * {@code end} pulled back along the segment until a branch of the given radius fits. Direction is
     * preserved, so a radial fan of branches keeps its angles.
     */
    public Vector3f fitSegment(Vector3f start, Vector3f end, float radius) {
        return zone.fitSegment(start, end, origin, radius);
    }

    public boolean canWrite(int localX, int localZ) {
        return zone.contains(origin.getX() + localX, origin.getZ() + localZ);
    }
}
