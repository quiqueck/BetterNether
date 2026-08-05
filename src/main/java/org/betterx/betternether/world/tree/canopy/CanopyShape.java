package org.betterx.betternether.world.tree.canopy;

import org.betterx.betternether.world.tree.TreeSpace;
import org.betterx.betternether.world.tree.math.Volume;
import org.betterx.betternether.world.tree.skeleton.Anchor;

import net.minecraft.util.RandomSource;

/**
 * Turns a foliage {@link Anchor} into a solid.
 * <p>
 * The counterpart of vanilla's {@code FoliagePlacer}, but a {@link Volume} rather than a stack of leaf
 * layers - which is what makes subtractive shapes (a hollowed skirt, a bitten-out underside) expressible
 * at all. Vanilla's placers are all additive and cannot describe them.
 */
public interface CanopyShape {
    /**
     * The solid for one anchor, in tree-local coordinates (i.e. already positioned at the anchor).
     *
     * @return {@link Volume#EMPTY} when there is no room to draw anything here
     */
    Volume volumeAt(Anchor anchor, TreeSpace space, RandomSource random);

    /**
     * The furthest this shape can reach from an anchor, in blocks, at {@code scale} 1. Used to size the
     * voxel buffer; an under-estimate silently truncates the canopy, so round up.
     */
    float reach();
}
