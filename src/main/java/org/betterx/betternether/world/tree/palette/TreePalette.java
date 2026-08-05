package org.betterx.betternether.world.tree.palette;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;

/**
 * Everything about <em>which blocks</em> a tree is made of, kept apart from everything about what shape
 * it is.
 *
 * <h2>Why this is not just two {@code BlockState}s</h2>
 * Vanilla's {@code TreeConfiguration} takes a {@code BlockStateProvider} for the trunk, and that
 * provider is handed only the absolute world position - it has no idea it is being asked about block 4
 * of 12 of a particular trunk. Any tree whose wood changes along its own height (gloomwood's dark base,
 * transition band and pale crown; rubeus's bottom/middle/top blend) is therefore inexpressible there.
 * {@link LogQuery} carries the trunk-relative height, which is the whole reason this interface exists.
 * <p>
 * The same applies to leaves: {@link LeafQuery} knows where the leaf sits inside its own canopy, which
 * is what lets a palette paint a marked region - glowing eyes, frosted tips, a flowering crown - without
 * the shape layer knowing anything about it.
 */
public interface TreePalette {
    /**
     * The log to place, or {@code null} to leave the cell empty.
     */
    BlockState log(LogQuery query);

    /**
     * The leaf to place, or {@code null} to leave the cell empty.
     */
    BlockState leaf(LeafQuery query);

    /**
     * The last word on a log that ended up with nothing above it. See
     * {@link LogSource#topOfColumn(BlockState)}.
     */
    default BlockState topOfColumn(BlockState state) {
        return state;
    }

    static TreePalette of(LogSource logs, LeafSource leaves) {
        return new TreePalette() {
            @Override
            public BlockState log(LogQuery query) {
                return logs.at(query);
            }

            @Override
            public BlockState leaf(LeafQuery query) {
                return leaves.at(query);
            }

            @Override
            public BlockState topOfColumn(BlockState state) {
                return logs.topOfColumn(state);
            }
        };
    }

    @FunctionalInterface
    interface LogSource {
        BlockState at(LogQuery query);

        /**
         * Asked once per log that turned out to be the top of its column - the cell above it is
         * foliage, or air, or outside the tree.
         * <p>
         * This cannot be decided in {@link #at(LogQuery)}: a palette is asked about a cell while the
         * tree is still being drawn, and whether anything ends up on top of it depends on every later
         * segment, on the loose-wood prune and on the branches the decay solver carves. It is only
         * knowable once the wood is final, which is why it is a second question rather than an
         * argument to the first.
         * <p>
         * Wood that means "the place where two other woods meet" needs it: with nothing above, there
         * is no upper wood, and the block is left under the sky as a transition to something that is
         * not there.
         */
        default BlockState topOfColumn(BlockState state) {
            return state;
        }
    }

    @FunctionalInterface
    interface LeafSource {
        BlockState at(LeafQuery query);
    }

    /**
     * @param world       the block being written
     * @param local       the same position in tree-local coordinates
     * @param heightAbove how far above the tree's base this block sits, in blocks - the number vanilla's
     *                    {@code BlockStateProvider} cannot see
     * @param axis        the dominant direction of the skeleton segment that produced this block
     * @param carved      {@code true} when this log was added by the leaf-decay solver as a support
     *                    branch rather than by the trunk shape, so a palette can use twiggier wood for it
     * @param random      the tree's random source
     */
    record LogQuery(
            BlockPos world,
            Vector3f local,
            float heightAbove,
            Direction.Axis axis,
            boolean carved,
            RandomSource random
    ) {
    }

    /**
     * @param world    the block being written
     * @param local    the same position in tree-local coordinates
     * @param anchor   the canopy anchor this leaf belongs to, in tree-local coordinates
     * @param toAnchor distance from {@code local} to {@code anchor}, in blocks
     * @param primary  {@code true} for the one anchor that dominates the tree's silhouette - the
     *                 highest, breaking ties on size. Lets a palette put a feature on the tree once
     *                 (a face, a crown, a single fruiting cluster) instead of once per canopy lobe,
     *                 which a purely anchor-relative mark cannot express
     * @param random   the tree's random source
     */
    record LeafQuery(
            BlockPos world,
            Vector3f local,
            Vector3f anchor,
            float toAnchor,
            boolean primary,
            RandomSource random
    ) {
    }
}
