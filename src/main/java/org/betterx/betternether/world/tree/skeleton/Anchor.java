package org.betterx.betternether.world.tree.skeleton;

import org.joml.Vector3f;

/**
 * A point where foliage attaches to the skeleton, in tree-local coordinates.
 * <p>
 * The equivalent of vanilla's {@code FoliagePlacer.FoliageAttachment}, with one addition: {@code scale}
 * lets a {@link org.betterx.betternether.world.tree.canopy.CanopyShape} size itself relative to the
 * branch it hangs on, so a fan of unequal branches does not get identical canopies stapled to it.
 *
 * @param position where the canopy is centred
 * @param scale    multiplier on the canopy's nominal size, normally around 1
 */
public record Anchor(Vector3f position, float scale) {
    public Anchor(Vector3f position) {
        this(position, 1.0F);
    }
}
