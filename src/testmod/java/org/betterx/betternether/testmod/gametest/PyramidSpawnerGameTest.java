package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.api.v2.levelgen.structures.StructureNBT;
import org.betterx.betternether.BetterNether;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Keeps the pyramid spawners' hand-authored spawn setup in place.
 * <p>
 * Those spawners carry {@code DeathLootTable: "empty"} to suppress drops, and that alone makes
 * {@code BaseSpawner}'s {@code hasNoConfiguration} false - it requires the entity tag to hold
 * <i>only</i> {@code id} - so {@code finalizeSpawn} never runs for them. Drop suppression is
 * deliberate, so what {@code finalizeSpawn} would have produced is written into the templates
 * instead:
 * <ul>
 *     <li>{@code AbstractCubeMob.setSpawnSize} rolls a size of 1, 2 or 4; without it every magma
 *     cube is the smallest one. The templates therefore declare {@code Size} explicitly - stored off
 *     by one, so in-world 1/2/4 is {@code Size} 0/1/3.</li>
 *     <li>{@code WitherSkeleton.populateDefaultEquipmentSlots} hands out a stone sword, so the
 *     templates equip the mainhand themselves.</li>
 * </ul>
 * Both are silent when lost - the mobs still spawn, just uniformly tiny and unarmed - which is why
 * this asserts on the entity the spawner would actually produce rather than on the tag.
 */
public class PyramidSpawnerGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final List<String> PYRAMIDS = List.of("lava/pyramid_3", "lava/pyramid_4");

    @GameTest
    public void pyramidMobsGetTheirAuthoredSpawnSetup(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        int cubes = 0;
        int skeletons = 0;

        for (String name : PYRAMIDS) {
            final StructureTemplate template = StructureNBT
                    .create(BetterNether.C.id(name))
                    .getTemplate();
            if (template == null) {
                failures.add(name + ": template failed to load");
                continue;
            }

            for (StructureBlockInfo info : template.filterBlocks(
                    BlockPos.ZERO, new StructurePlaceSettings(), Blocks.SPAWNER, false
            )) {
                if (info.nbt() == null) {
                    failures.add(name + " @ " + info.pos() + ": spawner has no block entity data");
                    continue;
                }

                final String where = name + " @ " + info.pos();
                final Entity entity = spawnedEntity(helper, info.nbt());
                if (entity == null) {
                    failures.add(where + ": spawner produced no entity");
                    continue;
                }

                if (entity instanceof AbstractCubeMob cube) {
                    cubes++;
                    // Asserting on the spawned size alone would not do: one of these cubes is
                    // deliberately size 1, which is also what an undeclared cube defaults to. So
                    // require the declaration, then check it actually took effect - which is where
                    // the off-by-one in Size would show up.
                    final Integer declared = declaredSize(info.nbt());
                    if (declared == null) {
                        failures.add(where + ": " + entity.getType()
                                + " does not declare Size - finalizeSpawn cannot run here, so the"
                                + " size has to be in the template or every cube spawns tiny");
                    } else if (cube.getSize() != declared + 1) {
                        failures.add(where + ": declared Size " + declared + " produced size "
                                + cube.getSize() + ", expected " + (declared + 1)
                                + " (Size is stored as size - 1)");
                    }
                } else if (entity instanceof AbstractSkeleton skeleton) {
                    skeletons++;
                    if (skeleton.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                        failures.add(where + ": " + entity.getType()
                                + " spawned unarmed - finalizeSpawn cannot run here, so the weapon"
                                + " has to be in the template");
                    }
                }
            }
        }

        // Guard against the templates being renamed or emptied out from under this test.
        if (cubes < 4) failures.add("expected at least 4 cube-mob spawners, found " + cubes);
        if (skeletons < 3) failures.add("expected at least 3 skeleton spawners, found " + skeletons);

        if (!failures.isEmpty()) {
            helper.fail(failures.size() + " pyramid spawner problem(s):\n  " + String.join("\n  ", failures));
            return;
        }
        helper.succeed();
    }

    /**
     * The {@code Size} a spawner's entity tag declares, or {@code null} if it declares none. Read
     * from {@code SpawnData} because that is what {@code BaseSpawner} spawns from.
     */
    private static Integer declaredSize(CompoundTag spawnerTag) {
        return spawnerTag
                .getCompound("SpawnData")
                .flatMap(data -> data.getCompound("entity"))
                .flatMap(entity -> entity.getInt("Size"))
                .orElse(null);
    }

    private static Entity spawnedEntity(GameTestHelper helper, CompoundTag spawnerTag) {
        final BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, Blocks.SPAWNER);
        final SpawnerBlockEntity spawner = helper.getBlockEntity(pos, SpawnerBlockEntity.class);

        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            spawner.loadWithComponents(TagValueInput.create(
                    reporter,
                    helper.getLevel().registryAccess(),
                    spawnerTag.copy()
            ));
        }

        final Entity entity = spawner.getSpawner().getOrCreateDisplayEntity(
                helper.getLevel(),
                helper.absolutePos(pos)
        );
        helper.setBlock(pos, Blocks.AIR);
        return entity;
    }
}
