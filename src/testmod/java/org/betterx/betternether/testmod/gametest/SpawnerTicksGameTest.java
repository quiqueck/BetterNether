package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.api.v2.levelgen.structures.StructureNBT;
import org.betterx.betternether.BetterNether;

import com.mojang.logging.LogUtils;
import de.ambertation.wover.test.api.gametest.MockPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Checks that a city guard spawner is actually <b>ticked by the level</b>, not merely that its data
 * spawns things when driven by hand.
 * <p>
 * This distinction is the reason a real-world failure went undiagnosed for a long time: every other
 * spawner test here calls {@code BaseSpawner.serverTick} directly, which bypasses the block entity
 * ticker completely. They prove the spawn logic is sound and say nothing about whether the game ever
 * calls it. In a real world the guard spawners sat with {@code Delay: 0} forever - and {@code Delay}
 * can only stay 0 if {@code delay()} is never reached, which a spawner that is never ticked satisfies
 * trivially.
 * <p>
 * So this one places the spawner, loads a real template's NBT into it, puts a player in range, and
 * then <b>lets the level run</b>, watching {@code Delay} for movement. Any change proves the ticker
 * fires; a value stuck at 0 across the whole window proves it does not.
 */
public class SpawnerTicksGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Long enough to cover MinSpawnDelay (300) with margin. */
    private static final int WINDOW = 420;

    @GameTest(maxTicks = 600)
    public void aCityGuardSpawnerIsTickedByTheLevel(GameTestHelper helper) {
        final BlockPos pos = new BlockPos(3, 2, 3);

        // Generous headroom, so a failure cannot be blamed on a 2.4-block mob not fitting.
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -3; z <= 3; z++) {
                    helper.setBlock(pos.offset(x, y, z), Blocks.AIR);
                }
            }
        }
        helper.setBlock(pos, Blocks.SPAWNER);

        final StructureTemplate template = StructureNBT
                .create(BetterNether.C.id("city/city_tower_01"))
                .getTemplate();
        final CompoundTag tag = template
                .filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.SPAWNER, false)
                .get(0)
                .nbt();

        final SpawnerBlockEntity spawner = helper.getBlockEntity(pos, SpawnerBlockEntity.class);
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            spawner.loadWithComponents(TagValueInput.create(
                    reporter, helper.getLevel().registryAccess(), tag.copy()
            ));
        }

        MockPlayers.survival(helper, new BlockPos(3, 2, 5));

        final short initial = readDelay(spawner);
        LOGGER.warn("=== SPAWNER TICK CHECK: Delay starts at {} ===", initial);

        // Deliberately does NOT call serverTick - the level must do it.
        helper.runAfterDelay(WINDOW, () -> {
            final short now = readDelay(spawner);
            LOGGER.warn("=== SPAWNER TICK CHECK: Delay after {} level ticks = {} ===", WINDOW, now);
            if (now == initial) {
                helper.fail("Delay never moved from " + initial + " over " + WINDOW
                        + " level ticks with a player in range - the spawner block entity is not being"
                        + " ticked at all. This is what the broken world showed.");
                return;
            }
            helper.succeed();
        });
    }

    private static short readDelay(SpawnerBlockEntity spawner) {
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            final TagValueOutput out = TagValueOutput.createWithContext(reporter, spawner.getLevel().registryAccess());
            spawner.saveCustomOnly(out);
            return out.buildResult().getShortOr("Delay", (short) -1);
        }
    }
}
