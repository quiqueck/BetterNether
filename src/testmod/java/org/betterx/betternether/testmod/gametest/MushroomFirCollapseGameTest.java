package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.blocks.BlockMushroomFir;
import org.betterx.betternether.blocks.BlockMushroomFir.MushroomFirShape;
import org.betterx.betternether.blocks.NetherLoot;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Felling a mushroom fir gives back its wood: every fir block that breaks - the one the player mined and
 * every block that collapses because it lost its support - drops one mushroom fir stem, and the cap blocks
 * ({@code side_*}, {@code end}) drop a sapling with {@link NetherLoot#MUSHROOM_FIR_SAPLING_CHANCE}.
 * <p>
 * Breaking a trunk block takes down everything above it and everything attached to that (like a chorus
 * plant), while the part below stays. Breaking the bottom block fells the whole tree. The collapse runs
 * through scheduled ticks and {@code destroyBlock(pos, true)}, so each block goes through its loot table.
 * <p>
 * Trees are grown from saplings in a 32-block arena, since a fir can grow taller than the default arena.
 */
public class MushroomFirCollapseGameTest {
    private static final String HUGE_ARENA = "betternether-testmod:huge_empty";
    private static final int SIZE = 32;
    private static final int GROUND_LAYERS = 3;
    /** Nine trees on a grid, far enough apart that neither branches nor drops mix. */
    private static final int[] GRID = {6, 16, 26};
    /** The tallest fir collapses one block per tick; this is plenty. */
    private static final int SETTLE_TICKS = 40;
    /** Loot rolls per cap shape in the statistical test. */
    private static final int ROLLS = 4000;

    @GameTest(structure = HUGE_ARENA, maxTicks = 200)
    public void breakingTheBaseFellsTheWholeTreeWithItsWood(GameTestHelper helper) {
        final List<Set<BlockPos>> trees = growForest(helper, 7101L);
        final ServerPlayer player = player(helper);
        int blocks = 0;
        int caps = 0;
        for (Set<BlockPos> tree : trees) {
            blocks += tree.size();
            caps += caps(helper.getLevel(), tree);
            player.gameMode.destroyBlock(lowest(tree));
        }
        final int total = blocks;
        final int capCount = caps;
        helper.runAfterDelay(SETTLE_TICKS, () -> {
            final List<String> failures = new ArrayList<>();
            final int left = firBlocks(helper).size();
            if (left > 0) failures.add(left + " fir blocks are still standing after the bases were broken");
            checkDrops(helper, failures, total, capCount);
            failIfAny(helper, "Mushroom fir felling regression", failures);
            helper.succeed();
        });
    }

    @GameTest(structure = HUGE_ARENA, maxTicks = 200)
    public void breakingATrunkBlockDropsEverythingAboveAndKeepsTheRest(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final List<Set<BlockPos>> trees = growForest(helper, 7102L);
        final ServerPlayer player = player(helper);
        final Set<BlockPos> falling = new HashSet<>();
        final Set<BlockPos> staying = new HashSet<>();
        int caps = 0;
        for (Set<BlockPos> tree : trees) {
            // A trunk block above the bottom, roughly halfway up, so there is something below and above it.
            final BlockPos bottom = lowest(tree);
            int height = 0;
            while (tree.contains(bottom.above(height + 1)) && isTrunk(level.getBlockState(bottom.above(height + 1)))) {
                height++;
            }
            final BlockPos broken = bottom.above(Math.max(1, (height + 1) / 2));
            final Set<BlockPos> above = connectedExceptDown(tree, broken);
            falling.addAll(above);
            caps += caps(level, above);
            for (BlockPos p : tree) if (!above.contains(p)) staying.add(p);
            player.gameMode.destroyBlock(broken);
        }
        final int fallingCount = falling.size();
        final int capCount = caps;
        helper.runAfterDelay(SETTLE_TICKS, () -> {
            final List<String> failures = new ArrayList<>();
            for (BlockPos p : falling) {
                if (level.getBlockState(p).getBlock() instanceof BlockMushroomFir) {
                    failures.add(helper.relativePos(p) + " is attached above the broken block but still stands");
                }
            }
            for (BlockPos p : staying) {
                if (!(level.getBlockState(p).getBlock() instanceof BlockMushroomFir)) {
                    failures.add(helper.relativePos(p) + " is below the broken block but came down");
                }
            }
            checkDrops(helper, failures, fallingCount, capCount);
            failIfAny(helper, "Mushroom fir partial collapse regression", failures.subList(0, Math.min(12, failures.size())));
            helper.succeed();
        });
    }

    /**
     * The loot table on its own, rolled many times: every shape gives exactly one stem, trunk shapes never a
     * sapling, and cap shapes a sapling at the configured chance (checked within five standard deviations).
     */
    @GameTest
    public void everyShapeDropsOneStemAndCapsDropSaplingsByChance(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        final Block trunk = NetherWoodBlocks.MAT_MUSHROOM_FIR.getTrunk();
        final Item stem = NetherWoodBlocks.MAT_MUSHROOM_FIR.getStem().asItem();
        final Item sapling = NetherWoodBlocks.MAT_MUSHROOM_FIR.getSapling().asItem();
        final double p = NetherLoot.MUSHROOM_FIR_SAPLING_CHANCE;
        final double tolerance = 5 * Math.sqrt(p * (1 - p) / ROLLS);
        final List<String> failures = new ArrayList<>();
        for (MushroomFirShape shape : MushroomFirShape.values()) {
            final BlockState state = trunk.defaultBlockState().setValue(BlockMushroomFir.SHAPE, shape);
            int saplings = 0;
            boolean badStem = false;
            for (int i = 0; i < ROLLS; i++) {
                int stems = 0;
                for (ItemStack drop : Block.getDrops(state, level, pos, null)) {
                    if (drop.is(stem)) stems += drop.getCount();
                    else if (drop.is(sapling)) saplings += drop.getCount();
                    else failures.add(shape + " dropped an unexpected " + drop);
                }
                if (stems != 1) badStem = true;
            }
            if (badStem) failures.add(shape + " did not always drop exactly one mushroom fir stem");
            final double rate = saplings / (double) ROLLS;
            if (isTrunk(state)) {
                if (saplings > 0) failures.add(shape + " (trunk) dropped " + saplings + " saplings, expected none");
            } else if (Math.abs(rate - p) > tolerance) {
                failures.add(shape + " dropped saplings at " + rate + ", expected " + p + " +- " + tolerance);
            }
        }
        failIfAny(helper, "Mushroom fir loot table regression", failures.subList(0, Math.min(12, failures.size())));
        helper.succeed();
    }

    // ------------------------------------------------------------------------------------------------------

    /**
     * Stems must match the broken blocks exactly. Saplings come from the caps by chance: with the dozens of
     * caps nine trees carry, they must lie within five standard deviations of the expected count.
     */
    private static void checkDrops(GameTestHelper helper, List<String> failures, int blocks, int caps) {
        final List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class,
                AABB.encapsulatingFullBlocks(helper.absolutePos(BlockPos.ZERO), helper.absolutePos(new BlockPos(SIZE - 1, SIZE - 1, SIZE - 1)))
        );
        final int stems = count(drops, NetherWoodBlocks.MAT_MUSHROOM_FIR.getStem().asItem());
        final int saplings = count(drops, NetherWoodBlocks.MAT_MUSHROOM_FIR.getSapling().asItem());
        if (stems != blocks) failures.add(blocks + " fir blocks broke but " + stems + " stems dropped");
        final double p = NetherLoot.MUSHROOM_FIR_SAPLING_CHANCE;
        final double mean = caps * p;
        final double sd = Math.sqrt(caps * p * (1 - p));
        if (caps < 10) failures.add("only " + caps + " cap blocks broke - too few to judge the sapling chance");
        if (Math.abs(saplings - mean) > 5 * sd) {
            failures.add(caps + " cap blocks broke and dropped " + saplings + " saplings, expected about "
                    + Math.round(mean) + " (+- " + Math.round(5 * sd) + ")");
        }
    }

    private static List<Set<BlockPos>> growForest(GameTestHelper helper, long seed) {
        final ServerLevel level = helper.getLevel();
        clearArena(helper);
        final RandomSource random = RandomSource.create(seed);
        final Block saplingBlock = NetherWoodBlocks.MAT_MUSHROOM_FIR.getSapling();
        for (int x : GRID) {
            for (int z : GRID) {
                final BlockPos plant = new BlockPos(x, GROUND_LAYERS, z);
                helper.setBlock(plant.below(), NetherTerrainBlocks.NETHER_MYCELIUM);
                helper.setBlock(plant, saplingBlock);
                final BlockPos abs = helper.absolutePos(plant);
                final BlockState state = level.getBlockState(abs);
                if (!(state.getBlock() instanceof FeatureSaplingBlock feature)
                        || !feature.growFeatureNow(level, abs, state, random)) {
                    throw helper.assertionException(Component.literal("the mushroom fir sapling at " + plant + " did not grow"));
                }
            }
        }
        // Split the fir blocks into trees: everything connected to a trunk column.
        final Set<BlockPos> all = firBlocks(helper);
        final List<Set<BlockPos>> trees = new ArrayList<>();
        final Set<BlockPos> seen = new HashSet<>();
        for (BlockPos start : all) {
            if (seen.contains(start)) continue;
            final Set<BlockPos> tree = new HashSet<>();
            final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            seen.add(start);
            while (!queue.isEmpty()) {
                final BlockPos p = queue.poll();
                tree.add(p);
                for (Direction d : Direction.values()) {
                    final BlockPos n = p.relative(d);
                    if (all.contains(n) && seen.add(n)) queue.add(n);
                }
            }
            trees.add(tree);
        }
        if (trees.size() != GRID.length * GRID.length) {
            throw helper.assertionException(Component.literal("expected " + GRID.length * GRID.length
                    + " separate firs, found " + trees.size()));
        }
        return trees;
    }

    /** The broken block plus every fir block reachable from it going up or sideways (never down). */
    private static Set<BlockPos> connectedExceptDown(Set<BlockPos> tree, BlockPos from) {
        final Set<BlockPos> out = new HashSet<>();
        final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(from);
        out.add(from);
        while (!queue.isEmpty()) {
            final BlockPos p = queue.poll();
            for (Direction d : Direction.values()) {
                if (d == Direction.DOWN) continue;
                final BlockPos n = p.relative(d);
                if (tree.contains(n) && out.add(n)) queue.add(n);
            }
        }
        return out;
    }

    private static boolean isTrunk(BlockState state) {
        if (!(state.getBlock() instanceof BlockMushroomFir)) return false;
        final MushroomFirShape shape = state.getValue(BlockMushroomFir.SHAPE);
        return shape == MushroomFirShape.BOTTOM || shape == MushroomFirShape.MIDDLE || shape == MushroomFirShape.TOP;
    }

    private static int caps(ServerLevel level, Set<BlockPos> blocks) {
        int n = 0;
        for (BlockPos p : blocks) if (!isTrunk(level.getBlockState(p))) n++;
        return n;
    }

    private static BlockPos lowest(Set<BlockPos> tree) {
        return tree.stream().min((a, b) -> Integer.compare(a.getY(), b.getY())).orElseThrow();
    }

    /** A survival player in a corner of the arena, far from every tree and its drops, with an axe. */
    private static ServerPlayer player(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, new BlockPos(1, GROUND_LAYERS, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));
        return player;
    }

    private static Set<BlockPos> firBlocks(GameTestHelper helper) {
        final Set<BlockPos> out = new HashSet<>();
        for (BlockPos rel : arena()) {
            final BlockPos abs = helper.absolutePos(rel);
            if (helper.getLevel().getBlockState(abs).getBlock() instanceof BlockMushroomFir) out.add(abs.immutable());
        }
        return out;
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

    private static Iterable<BlockPos> arena() {
        return BlockPos.betweenClosed(0, 0, 0, SIZE - 1, SIZE - 1, SIZE - 1);
    }

    private static int count(List<ItemEntity> drops, Item item) {
        return drops.stream().filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(headline + ":\n - " + String.join("\n - ", failures)));
        }
    }
}
