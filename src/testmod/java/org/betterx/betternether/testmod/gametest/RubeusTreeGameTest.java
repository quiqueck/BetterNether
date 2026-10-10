package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.registry.block.NetherLeavesBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Rubeus trees grown from a sapling (#97): they carry cones, and their leaves decay once the wood is
 * gone.
 * <p>
 * Both halves were silently broken. The crown hangs every cone under a leaf, but the cone's
 * {@code canSurvive} only accepted a sturdy face above it and vanilla gives leaves an empty support
 * shape - so the generator's own placement check rejected almost every cone, and the only ones that
 * survived were the few that happened to land under a log. And the crown placed its leaves
 * {@code persistent} on a leaves block that had decay switched off, so felling the tree left the whole
 * canopy hanging in the air.
 * <p>
 * Trees are grown through {@link FeatureSaplingBlock#growFeatureNow} - the sapling path, the one the
 * report is about - in a {@link #HUGE_ARENA}: a rubeus is up to ~22 blocks tall and ~20 across, which
 * the default 8-tall arena (and even {@code tall_empty}) clips against its barrier walls.
 */
public class RubeusTreeGameTest {
    private static final String HUGE_ARENA = "betternether-testmod:huge_empty";
    private static final int SIZE = 32;
    private static final int GROUND_LAYERS = 3;
    private static final BlockPos PLANT = new BlockPos(SIZE / 2, GROUND_LAYERS, SIZE / 2);

    private static final int TREES = 8;
    /**
     * Cones expected across {@link #TREES} trees, deliberately far below what a working crown yields
     * (about ten per tree) and far above what the broken one did (one in ten trees).
     */
    private static final int MIN_CONES = TREES;
    /** Leaf decay cascades one block per scheduled tick, and the budget is six steps. */
    private static final int DECAY_SETTLE_TICKS = 20;

    @GameTest(structure = HUGE_ARENA, maxTicks = 100)
    public void grownRubeusTreesCarryCones(GameTestHelper helper) {
        final RandomSource random = RandomSource.create(97L);
        int cones = 0;
        final List<String> failures = new ArrayList<>();
        for (int i = 0; i < TREES; i++) {
            clearArena(helper);
            if (!growTree(helper, random)) {
                failures.add("tree " + i + " did not grow from its sapling - check the arena size");
                continue;
            }
            cones += count(helper, NetherWoodBlocks.MAT_RUBEUS.getCone());
        }
        if (cones < MIN_CONES) {
            failures.add(TREES + " grown rubeus trees carry only " + cones + " cones (expected at least "
                    + MIN_CONES + ") - the crown hangs cones under leaves, so check that a cone can survive"
                    + " under a leaf");
        }
        failIfAny(helper, "Rubeus cone regression", failures);
        helper.succeed();
    }

    @GameTest(structure = HUGE_ARENA, maxTicks = 200)
    public void rubeusLeavesDecayOnceTheWoodIsGone(GameTestHelper helper) {
        clearArena(helper);
        if (!growTree(helper, RandomSource.create(9701L))) {
            throw helper.assertionException(Component.literal("the rubeus sapling did not grow"));
        }

        final ServerLevel level = helper.getLevel();
        final Block leavesBlock = NetherLeavesBlocks.RUBEUS_LEAVES;
        final List<String> failures = new ArrayList<>();
        final List<BlockPos> wood = new ArrayList<>();
        int leaves = 0;

        // Every leaf the tree placed must already hold the distance vanilla would compute for it: if it
        // did not, the first neighbour update would correct it, and a leaf that was really out of reach
        // would vanish from a standing tree.
        for (BlockPos rel : arena()) {
            final BlockPos abs = helper.absolutePos(rel);
            final BlockState state = level.getBlockState(abs);
            if (state.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) wood.add(abs);
            if (!state.is(leavesBlock)) continue;
            leaves++;
            if (state.getValue(LeavesBlock.PERSISTENT)) {
                failures.add("generated leaf at " + rel + " is persistent and can never decay");
            } else if (state.getValue(LeavesBlock.DISTANCE) >= LeavesBlock.DECAY_DISTANCE) {
                failures.add("generated leaf at " + rel + " is already out of reach of the wood");
            } else if (state.getValue(LeavesBlock.DISTANCE) != vanillaDistance(level, abs)) {
                failures.add("generated leaf at " + rel + " stores distance " + state.getValue(LeavesBlock.DISTANCE)
                        + " but vanilla computes " + vanillaDistance(level, abs));
            }
        }
        if (leaves == 0) failures.add("the grown tree has no leaves - nothing to test");
        if (wood.isEmpty()) failures.add("the grown tree has no wood - nothing to fell");
        failIfAny(helper, "Rubeus generated-leaf regression", failures.subList(0, Math.min(10, failures.size())));

        // Fell the tree the way a player would: with block updates, so the leaves get told.
        for (BlockPos abs : wood) level.setBlock(abs, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        helper.runAfterDelay(DECAY_SETTLE_TICKS, () -> {
            // Random ticks through vanilla's own gate - a leaf that never asks for one never decays.
            for (BlockPos rel : arena()) {
                final BlockPos abs = helper.absolutePos(rel);
                final BlockState state = level.getBlockState(abs);
                if (state.is(leavesBlock) && state.isRandomlyTicking()) {
                    state.randomTick(level, abs, level.getRandom());
                }
            }
            helper.runAfterDelay(2, () -> {
                final int left = count(helper, leavesBlock);
                final int cones = count(helper, NetherWoodBlocks.MAT_RUBEUS.getCone());
                final List<String> after = new ArrayList<>();
                if (left > 0) after.add(left + " rubeus leaves survived felling the tree and a random tick");
                if (cones > 0) after.add(cones + " rubeus cones are still hanging with no leaves above them");
                failIfAny(helper, "Rubeus leaf decay regression", after);
                helper.succeed();
            });
        });
    }

    /** The {@code distance} vanilla's {@code LeavesBlock.updateDistance} would settle on. */
    private static int vanillaDistance(ServerLevel level, BlockPos pos) {
        int best = LeavesBlock.DECAY_DISTANCE;
        for (Direction dir : Direction.values()) {
            final BlockState n = level.getBlockState(pos.relative(dir));
            if (n.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) return 1;
            if (n.hasProperty(LeavesBlock.DISTANCE)) best = Math.min(best, n.getValue(LeavesBlock.DISTANCE) + 1);
        }
        return best;
    }

    private static boolean growTree(GameTestHelper helper, RandomSource random) {
        final Block sapling = NetherWoodBlocks.MAT_RUBEUS.getSapling();
        helper.setBlock(PLANT, sapling);
        final BlockPos abs = helper.absolutePos(PLANT);
        final BlockState state = helper.getLevel().getBlockState(abs);
        if (!(state.getBlock() instanceof FeatureSaplingBlock<?, ?> feature)) {
            throw helper.assertionException(Component.literal("the rubeus sapling is not a FeatureSaplingBlock"));
        }
        return feature.growFeatureNow(helper.getLevel(), abs, state, random);
    }

    private static void clearArena(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        for (BlockPos rel : arena()) {
            final BlockState target = rel.getY() < GROUND_LAYERS
                    ? Blocks.NETHERRACK.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
            level.setBlock(helper.absolutePos(rel), target, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    private static int count(GameTestHelper helper, Block block) {
        int n = 0;
        for (BlockPos rel : arena()) {
            if (helper.getLevel().getBlockState(helper.absolutePos(rel)).is(block)) n++;
        }
        return n;
    }

    private static Iterable<BlockPos> arena() {
        return BlockPos.betweenClosed(0, 0, 0, SIZE - 1, SIZE - 1, SIZE - 1);
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
