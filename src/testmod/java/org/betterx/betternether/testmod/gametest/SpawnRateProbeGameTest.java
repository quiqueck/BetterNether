package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.api.v2.levelgen.structures.StructureNBT;
import org.betterx.betternether.BetterNether;

import com.mojang.logging.LogUtils;
import de.ambertation.wover.test.api.gametest.MockPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Drives a real city guard spawner with a player standing next to it and asserts it actually
 * produces skeletons.
 * <p>
 * Everything else about these spawners is checked statically - {@code CitySpawnerGameTest} proves the
 * template decodes into a fully equipped guard - but "the data is readable" and "the spawner
 * actually spawns" are different claims, and only the second is what a player experiences. This
 * covers the gates between the two: {@code isNearPlayer}, the spawner-blocks gamerule,
 * {@code custom_spawn_rules}, the {@code MaxNearbyEntities} ceiling, and whether a 2.4-block-tall mob
 * can fit any of the {@code SpawnCount} candidate positions.
 * <p>
 * The floor is deliberately loose. Measured yield in this arena is 14-17 over 4000 ticks; the burst
 * interval alone is 300-1600 ticks, so a tight bound would be flaky. This is here to catch a spawner
 * that produces <i>nothing</i>, which is what a missing or undecodable {@code SpawnData} looks like
 * from the outside.
 * <p>
 * <b>Scope warning.</b> This hollows out its own arena, so it measures an <i>unobstructed</i>
 * spawner and says nothing about the geometry the templates actually ship. That matters: measured
 * against a real generated city, the guard spawner at (-2806, 61, -1658) found room on only 7.8% of
 * its attempts - 0.62 spawns per burst of 8 - because it sits inside the tower's brickwork and the
 * only cells with 2.4 blocks of clearance are 2-4 blocks out, where the triangular x/z distribution
 * rarely lands. A passing run here does not mean the guards are a threat in game; only a generated
 * world can answer that.
 */
public class SpawnRateProbeGameTest {
    /** Bursts are 300-1600 ticks apart, so 4000 ticks is only a handful of them. */
    private static final int TICKS = 4000;
    private static final int MIN_EXPECTED = 5;

    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest
    public void probeCitySpawnerRate(GameTestHelper helper) {
        final StructureTemplate template = StructureNBT
                .create(BetterNether.C.id("city/city_tower_01"))
                .getTemplate();
        final StructureBlockInfo info = template.filterBlocks(
                BlockPos.ZERO, new StructurePlaceSettings(), Blocks.SPAWNER, false
        ).get(0);
        final CompoundTag tag = info.nbt();

        LOGGER.warn("=== SPAWN PROBE: spawner tag ===");
        LOGGER.warn("  Delay={} Min={} Max={} SpawnCount={} MaxNearby={} Range={} PlayerRange={}",
                tag.getShortOr("Delay", (short) -99),
                tag.getShortOr("MinSpawnDelay", (short) -99),
                tag.getShortOr("MaxSpawnDelay", (short) -99),
                tag.getShortOr("SpawnCount", (short) -99),
                tag.getShortOr("MaxNearbyEntities", (short) -99),
                tag.getShortOr("SpawnRange", (short) -99),
                tag.getShortOr("RequiredPlayerRange", (short) -99));

        final BlockPos pos = new BlockPos(3, 2, 3);
        // Hollow out generous headroom: a wither skeleton is 2.4 blocks tall and the spawn Y is
        // pos.y-1 .. pos.y+1, so a cramped arena would itself throttle spawning.
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -3; z <= 3; z++) {
                    helper.setBlock(pos.offset(x, y, z), Blocks.AIR);
                }
            }
        }
        helper.setBlock(pos, Blocks.SPAWNER);

        final ServerLevel level = helper.getLevel();
        final SpawnerBlockEntity spawner = helper.getBlockEntity(pos, SpawnerBlockEntity.class);
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            spawner.loadWithComponents(TagValueInput.create(reporter, level.registryAccess(), tag.copy()));
        }

        // A real ServerPlayer, because isNearPlayer() gates the whole tick.
        MockPlayers.survival(helper, new BlockPos(3, 2, 5));
        LOGGER.warn("  spawnerBlocksWork={} nearbyPlayer={}",
                level.isSpawnerBlockEnabled(),
                level.hasNearbyAlivePlayer(
                        helper.absolutePos(pos).getX() + 0.5,
                        helper.absolutePos(pos).getY() + 0.5,
                        helper.absolutePos(pos).getZ() + 0.5,
                        20));

        final AABB around = new AABB(helper.absolutePos(pos)).inflate(16);
        int previous = 0;
        for (int tick = 1; tick <= TICKS; tick++) {
            spawner.getSpawner().serverTick(level, helper.absolutePos(pos));
            if (tick % 250 == 0) {
                final int now = level.getEntities(EntityTypes.WITHER_SKELETON, around, e -> true).size();
                LOGGER.warn("  tick {}: wither skeletons nearby = {} (+{})", tick, now, now - previous);
                previous = now;
            }
        }

        final int total = level.getEntities(EntityTypes.WITHER_SKELETON, around, e -> true).size();
        LOGGER.warn("=== SPAWN PROBE: {} skeletons after {} ticks ===", total, TICKS);
        level.getEntities(EntityTypes.WITHER_SKELETON, around, e -> true).forEach(e -> e.discard());

        if (total < MIN_EXPECTED) {
            helper.fail("city guard spawner produced only " + total + " skeletons in " + TICKS
                    + " ticks (expected at least " + MIN_EXPECTED + "). A count of 0 usually means"
                    + " SpawnData is missing or did not decode, so BaseSpawner has no entity to spawn.");
            return;
        }
        helper.succeed();
    }
}
