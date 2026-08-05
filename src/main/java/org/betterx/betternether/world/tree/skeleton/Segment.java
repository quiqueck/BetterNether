package org.betterx.betternether.world.tree.skeleton;

import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.math.Volumes;

import net.minecraft.core.Direction;

import org.joml.Vector3f;

/**
 * One tapered run of wood, in tree-local coordinates (origin = the block the tree grows from).
 *
 * @param from       start of the run
 * @param to         end of the run
 * @param radiusFrom radius at {@code from}, in blocks
 * @param radiusTo   radius at {@code to}
 */
public record Segment(Vector3f from, Vector3f to, float radiusFrom, float radiusTo) {
    public Volume toVolume() {
        return Volumes.capsule(from, to, radiusFrom, radiusTo);
    }

    public float maxRadius() {
        return Math.max(radiusFrom, radiusTo);
    }

    /**
     * The axis a {@code RotatedPillarBlock} log should use for this run.
     * <p>
     * Picked from the dominant component of the direction rather than from the block's position, so a
     * near-horizontal branch gets horizontal logs along its whole length instead of flipping axis
     * wherever the rasterised cells happen to step sideways.
     */
    public Direction.Axis axis() {
        final float dx = Math.abs(to.x - from.x);
        final float dy = Math.abs(to.y - from.y);
        final float dz = Math.abs(to.z - from.z);
        if (dy >= dx && dy >= dz) return Direction.Axis.Y;
        return dx >= dz ? Direction.Axis.X : Direction.Axis.Z;
    }
}
