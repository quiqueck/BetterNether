package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherObsidianBlocks;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the two custom advancement triggers behind BetterNether's obsidian advancements, and the block
 * conversions that fire them.
 * <p>
 * Both triggers only reach players inside an {@code AABB} around the converted block - roughly ten
 * blocks horizontally - so each test keeps its player next to the conversion. That proximity rule is
 * itself part of what is being asserted: a conversion nobody is standing near awards nothing.
 * <p>
 * {@code betternether:obsidian_blocks} is deliberately not covered here. Its criteria are plain
 * {@code minecraft:inventory_changed} triggers with no mod code behind them.
 */
public class ObsidianAdvancementGameTest {
    private static final BlockPos PLAYER_POS = new BlockPos(1, 2, 1);

    private static AdvancementHolder advancement(GameTestHelper helper, String path) {
        return helper.getLevel()
                     .getServer()
                     .getAdvancements()
                     .get(Identifier.parse("betternether:" + path));
    }

    /**
     * Lava flowing over soul soil into water becomes BetterNether's blue obsidian instead of vanilla
     * obsidian, and awards {@code betternether:blue_obsidian} to anyone nearby.
     */
    @GameTest(maxTicks = 300)
    public void blueObsidianConversionAwardsAdvancement(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.inLevel(helper, PLAYER_POS);

        final BlockPos soulSoil = new BlockPos(3, 1, 3);
        final BlockPos lava = new BlockPos(3, 2, 3);
        final BlockPos water = new BlockPos(4, 2, 3);

        helper.setBlock(soulSoil, Blocks.SOUL_SOIL);
        helper.setBlock(lava, Blocks.LAVA);
        helper.setBlock(water, Blocks.WATER);

        helper.startSequence()
              .thenIdle(60)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();

                  final Block got = helper.getBlockState(lava).getBlock();
                  if (got != NetherObsidianBlocks.BLUE_OBSIDIAN) {
                      failures.add("lava over soul soil turned into " + got.getName().getString()
                              + " instead of blue obsidian");
                  }

                  requireDone(helper, player, "blue_obsidian", failures);
                  failIfAny(helper, "Blue obsidian advancement regression", failures);
              })
              .thenSucceed();
    }

    /**
     * All four lightning conversions behind {@code betternether:make_crying}. The advancement requires
     * every one of them, so a single broken link makes it unobtainable - which is exactly the kind of
     * thing that goes unnoticed without a test.
     */
    @GameTest(maxTicks = 300)
    public void lightningConversionsAwardEveryCriterion(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.inLevel(helper, PLAYER_POS);

        final BlockPos rod = new BlockPos(4, 2, 4);
        final BlockPos obsidian = new BlockPos(5, 2, 4);
        final BlockPos crying = new BlockPos(3, 2, 4);
        final BlockPos blueObsidian = new BlockPos(4, 2, 5);
        final BlockPos blueCrying = new BlockPos(4, 2, 3);

        // 26.2: lightning rods weather like copper, so Blocks.LIGHTNING_ROD is a collection of eight
        // blocks. The unaffected (unwaxed) variant is the plain rod a player crafts.
        helper.setBlock(rod, Blocks.LIGHTNING_ROD.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED));
        helper.setBlock(obsidian, Blocks.OBSIDIAN);
        helper.setBlock(crying, Blocks.CRYING_OBSIDIAN);
        helper.setBlock(blueObsidian, NetherObsidianBlocks.BLUE_OBSIDIAN);
        helper.setBlock(blueCrying, NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN);

        helper.startSequence()
              .thenIdle(5)
              .thenExecute(() -> {
                  // A bolt strikes the block below its own position, so it has to be summoned one above
                  // the rod for LightningRodBlock#onLightningStrike to run.
                  final LightningBolt bolt = helper.spawn(EntityTypes.LIGHTNING_BOLT, rod.above());
                  bolt.setVisualOnly(false);
              })
              .thenIdle(40)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();

                  requireBlock(helper, obsidian, Blocks.CRYING_OBSIDIAN,
                          "obsidian -> crying obsidian", failures);
                  requireBlock(helper, crying, NetherObsidianBlocks.WEEPING_OBSIDIAN,
                          "crying obsidian -> weeping obsidian", failures);
                  requireBlock(helper, blueObsidian, NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN,
                          "blue obsidian -> blue crying obsidian", failures);
                  requireBlock(helper, blueCrying, NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN,
                          "blue crying obsidian -> blue weeping obsidian", failures);

                  requireDone(helper, player, "make_crying", failures);
                  failIfAny(helper, "make_crying advancement regression", failures);
              })
              .thenSucceed();
    }

    private static void requireBlock(
            GameTestHelper helper,
            BlockPos pos,
            Block expected,
            String what,
            List<String> failures
    ) {
        final Block got = helper.getBlockState(pos).getBlock();
        if (got != expected) {
            failures.add(what + ": got " + got.getName().getString()
                    + " instead of " + expected.getName().getString());
        }
    }

    private static void requireDone(
            GameTestHelper helper,
            ServerPlayer player,
            String path,
            List<String> failures
    ) {
        final AdvancementHolder holder = advancement(helper, path);
        if (holder == null) {
            failures.add("betternether:" + path + " is not loaded at all");
            return;
        }
        final AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            final List<String> remaining = new ArrayList<>();
            progress.getRemainingCriteria().forEach(remaining::add);
            failures.add("betternether:" + path + " was not awarded - still missing " + remaining);
        }
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
