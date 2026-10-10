package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.blocks.BlockMushroomFirSapling;
import org.betterx.betternether.blocks.BlockRubeusSapling;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.BlockWillowSapling;
import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether saplings take bone meal like vanilla saplings and mushrooms, and still grow on their own as
 * slowly as before.
 * <p>
 * The feature saplings used to roll the same chance for a random tick and for bone meal - 1 in 16, or 1 in
 * 8 on BetterNether farmland for rubeus, willow and mushroom fir - and need two successes, so a
 * tree cost about 32 bone meal. The giant mold and jellyfish mushroom saplings grow in one step but rolled
 * the same odds, about 16 bone meal. Bone meal now rolls vanilla's odds (45% for a sapling, 40% for a
 * mushroom), and random ticks keep the old odds, farmland bonus included.
 * <p>
 * The rates are measured directly rather than by growing trees, so every sapling class is covered and its
 * own overrides are what is measured; BetterEnd's {@code SaplingBonemealGameTest} grows real trees through
 * {@code BoneMealItem.growCrop} for the shared {@link FeatureSaplingBlock} path. All bounds are several
 * standard deviations wide (stated next to each), so a failure is a changed rate, not a bad run.
 */
public class SaplingBonemealGameTest {
    private static final BlockPos SOIL = new BlockPos(1, 1, 1);
    private static final BlockPos PLANT = SOIL.above();

    private static final int SAMPLES = 4000;
    /** Expected 0.45, standard deviation 0.0079 at 4000 samples: 6.3 sigma either way. */
    private static final double MIN_SAPLING_BONEMEAL_RATE = 0.40;
    private static final double MAX_SAPLING_BONEMEAL_RATE = 0.50;
    /** Expected 0.40, standard deviation 0.0077 at 4000 samples: 6.4 sigma either way. */
    private static final double MIN_MUSHROOM_BONEMEAL_RATE = 0.35;
    private static final double MAX_MUSHROOM_BONEMEAL_RATE = 0.45;
    /** Expected 1/16 = 0.0625, standard deviation 0.0038: 4.6 sigma below, 6 above. */
    private static final double MIN_RANDOM_TICK_RATE = 0.045;
    private static final double MAX_RANDOM_TICK_RATE = 0.085;
    /** Expected 1/8 = 0.125, standard deviation 0.0052: 5.7 sigma either way. */
    private static final double MIN_FERTILE_RANDOM_TICK_RATE = 0.095;
    private static final double MAX_FERTILE_RANDOM_TICK_RATE = 0.155;

    @GameTest(maxTicks = 100)
    public void boneMealTakesAtVanillaRates(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final RandomSource random = RandomSource.create(11L);
        final BlockPos abs = helper.absolutePos(PLANT);
        final List<String> failures = new ArrayList<>();

        final List<FeatureSaplingBlock> saplings = featureSaplings();
        if (saplings.isEmpty()) failures.add("no BetterNether FeatureSaplingBlock is registered");
        for (FeatureSaplingBlock sapling : saplings) {
            final BlockState state = growingState(sapling);
            place(helper, Blocks.NETHERRACK.defaultBlockState(), state);
            final double rate = boneMealRate(level, random, abs, sapling, state);
            check(failures, name(sapling) + " bone meal (about " + mean(2, rate) + " per tree)",
                    rate, MIN_SAPLING_BONEMEAL_RATE, MAX_SAPLING_BONEMEAL_RATE);
        }

        for (Block block : List.of(NetherSaplingBlocks.GIANT_MOLD_SAPLING, NetherSaplingBlocks.JELLYFISH_MUSHROOM_SAPLING)) {
            final BlockState state = block.defaultBlockState();
            place(helper, Blocks.NETHERRACK.defaultBlockState(), state);
            final double rate = boneMealRate(level, random, abs, (BonemealableBlock) block, state);
            check(failures, name(block) + " bone meal (about " + mean(1, rate) + " per mushroom)",
                    rate, MIN_MUSHROOM_BONEMEAL_RATE, MAX_MUSHROOM_BONEMEAL_RATE);
        }

        clear(helper);
        failIfAny(helper, "Bone meal rate regression", failures);
        helper.succeed();
    }

    /**
     * A random tick on a {@code STAGE 0} sapling either does nothing or moves it to {@code STAGE 1}, so
     * counting the stage changes from {@code STAGE 0} measures the natural growth roll, on plain netherrack
     * and on BetterNether farmland.
     */
    @GameTest(maxTicks = 100)
    public void randomTicksKeepTheirRate(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final RandomSource random = RandomSource.create(13L);
        final BlockPos abs = helper.absolutePos(PLANT);
        final List<String> failures = new ArrayList<>();

        for (FeatureSaplingBlock sapling : featureSaplings()) {
            final BlockState stage0 = growingState(sapling).setValue(SaplingBlock.STAGE, 0);

            place(helper, Blocks.NETHERRACK.defaultBlockState(), stage0);
            check(failures, name(sapling) + " random tick on netherrack",
                    randomTickRate(level, random, abs, sapling, stage0), MIN_RANDOM_TICK_RATE, MAX_RANDOM_TICK_RATE);

            place(helper, NetherTerrainBlocks.FARMLAND.defaultBlockState(), stage0);
            final boolean fertileBonus = sapling instanceof BlockRubeusSapling
                    || sapling instanceof BlockWillowSapling
                    || sapling instanceof BlockMushroomFirSapling;
            check(failures, name(sapling) + " random tick on farmland",
                    randomTickRate(level, random, abs, sapling, stage0),
                    fertileBonus ? MIN_FERTILE_RANDOM_TICK_RATE : MIN_RANDOM_TICK_RATE,
                    fertileBonus ? MAX_FERTILE_RANDOM_TICK_RATE : MAX_RANDOM_TICK_RATE);
        }

        clear(helper);
        failIfAny(helper, "Natural sapling growth rate regression", failures);
        helper.succeed();
    }

    private static double boneMealRate(
            ServerLevel level, RandomSource random, BlockPos abs, BonemealableBlock block, BlockState state
    ) {
        int takes = 0;
        for (int i = 0; i < SAMPLES; i++) {
            if (block.isBonemealSuccess(level, random, abs, state)) takes++;
        }
        return takes / (double) SAMPLES;
    }

    private static double randomTickRate(
            ServerLevel level, RandomSource random, BlockPos abs, Block sapling, BlockState stage0
    ) {
        int advances = 0;
        for (int i = 0; i < SAMPLES; i++) {
            level.setBlock(abs, stage0, Block.UPDATE_CLIENTS);
            stage0.randomTick(level, abs, random);
            final BlockState after = level.getBlockState(abs);
            if (after.is(sapling) && after.getValue(SaplingBlock.STAGE) == 1) advances++;
        }
        return advances / (double) SAMPLES;
    }

    /**
     * The stalagnate seed only grows towards air, and its default state grows downwards into the soil
     * this test puts below it; the standing variant grows up into the empty arena instead.
     */
    private static BlockState growingState(FeatureSaplingBlock sapling) {
        final BlockState state = sapling.defaultBlockState();
        return sapling instanceof BlockStalagnateSeed ? state.setValue(BlockStalagnateSeed.TOP, false) : state;
    }

    /** Soil first, then the plant, without neighbour updates that could pop a plant off its support. */
    private static void place(GameTestHelper helper, BlockState soil, BlockState plant) {
        helper.getLevel().setBlock(helper.absolutePos(SOIL), soil, Block.UPDATE_CLIENTS);
        helper.getLevel().setBlock(helper.absolutePos(PLANT), plant, Block.UPDATE_CLIENTS);
    }

    private static void clear(GameTestHelper helper) {
        place(helper, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState());
    }

    private static List<FeatureSaplingBlock> featureSaplings() {
        final List<FeatureSaplingBlock> saplings = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof FeatureSaplingBlock sapling
                    && BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("betternether")) {
                saplings.add(sapling);
            }
        }
        return saplings;
    }

    private static void check(List<String> failures, String what, double rate, double min, double max) {
        if (rate < min || rate > max) {
            failures.add(String.format(Locale.ROOT, "%s: rate %.4f over %d rolls, expected %.3f-%.3f",
                    what, rate, SAMPLES, min, max));
        }
    }

    private static String mean(int successesNeeded, double rate) {
        return rate <= 0 ? "infinite" : String.format(Locale.ROOT, "%.1f", successesNeeded / rate);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private static void failIfAny(GameTestHelper helper, String title, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(title + ":\n - " + String.join("\n - ", failures)));
        }
    }
}
