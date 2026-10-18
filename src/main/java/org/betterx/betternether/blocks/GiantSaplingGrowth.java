package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.levelgen.features.BCLConfigureFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Grows the large variant of a tree from a 2x2 of saplings, the way vanilla grows a dark oak.
 */
public final class GiantSaplingGrowth {
    /**
     * What {@link #tryGrow} found. {@link #NO_QUAD} and {@link #NO_ROOM} lead to opposite decisions:
     * without a 2x2 the small tree is still the right thing to grow, whereas a player who arranged four
     * saplings and lacks the room for the big tree should get nothing rather than a small tree in the
     * middle of the arrangement.
     */
    public enum Result {
        NO_QUAD, GROWN, NO_ROOM
    }

    private GiantSaplingGrowth() {
    }

    /**
     * Grows {@code feature} if the sapling at {@code pos} is part of a 2x2 of saplings matching
     * {@code partOfQuad}. The four offsets are scanned in vanilla's order and the feature is grown from
     * the found corner, so which of the four saplings ticked first does not decide where the tree ends up.
     */
    public static Result tryGrow(
            ServerLevel level,
            BlockPos pos,
            BlockState original,
            BCLConfigureFeature<?, ?> feature,
            Predicate<BlockState> partOfQuad,
            RandomSource random
    ) {
        for (int dx = 0; dx >= -1; dx--) {
            for (int dz = 0; dz >= -1; dz--) {
                final BlockPos corner = pos.offset(dx, 0, dz);
                if (!isQuad(level, corner, partOfQuad)) continue;

                final BlockState empty = Blocks.AIR.defaultBlockState();
                final BlockState[] before = new BlockState[4];
                final int[] i = {0};
                forEachOfQuad(corner, p -> before[i[0]++] = level.getBlockState(p));
                // Flag 4: no neighbour updates while the quad is half cleared.
                forEachOfQuad(corner, p -> level.setBlock(p, empty, 4));

                if (feature.placeInWorld(level, corner, random)) {
                    // Flag 4 kept the clearing off the wire; tell clients about any position the tree
                    // did not cover.
                    forEachOfQuad(corner, p -> {
                        if (level.getBlockState(p) == empty) {
                            level.sendBlockUpdated(p, original, empty, 2);
                        }
                    });
                    return Result.GROWN;
                }

                i[0] = 0;
                forEachOfQuad(corner, p -> level.setBlock(p, before[i[0]++], 4));
                return Result.NO_ROOM;
            }
        }
        return Result.NO_QUAD;
    }

    private static boolean isQuad(ServerLevel level, BlockPos corner, Predicate<BlockState> partOfQuad) {
        return partOfQuad.test(level.getBlockState(corner))
                && partOfQuad.test(level.getBlockState(corner.offset(1, 0, 0)))
                && partOfQuad.test(level.getBlockState(corner.offset(0, 0, 1)))
                && partOfQuad.test(level.getBlockState(corner.offset(1, 0, 1)));
    }

    private static void forEachOfQuad(BlockPos corner, Consumer<BlockPos> action) {
        action.accept(corner);
        action.accept(corner.offset(1, 0, 0));
        action.accept(corner.offset(0, 0, 1));
        action.accept(corner.offset(1, 0, 1));
    }
}
