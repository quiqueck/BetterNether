package org.betterx.betternether.world.tree.skeleton;

import org.betterx.betternether.world.tree.TreeSpace;

import net.minecraft.util.RandomSource;

/**
 * Builds the woody skeleton of a tree.
 * <p>
 * The counterpart of vanilla's {@code TrunkPlacer}, with two differences that the shapes in this library
 * need and vanilla's cannot express: the result is geometry (tapered segments) rather than blocks, and
 * the shape is handed the {@link TreeSpace} so it can fit itself to the room available instead of being
 * clipped afterwards.
 */
@FunctionalInterface
public interface TrunkShape {
    Skeleton build(TreeSpace space, RandomSource random);
}
