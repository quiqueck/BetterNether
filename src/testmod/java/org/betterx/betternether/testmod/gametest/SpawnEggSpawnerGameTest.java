package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherEntities;

import de.ambertation.wover.test.api.gametest.MockPlayers;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.AABB;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * A spawner configured the way a spawn egg configures it ({@code SpawnerBlockEntity#setEntityId},
 * no {@code custom_spawn_rules}) has to actually produce our mobs when the level ticks it (#281).
 * <p>
 * Without custom rules the spawner gates every attempt on {@code SpawnPlacements.checkSpawnRules}
 * with {@link net.minecraft.world.entity.EntitySpawnReason#SPAWNER} and then on
 * {@code Mob#checkSpawnRules}, so this is where a natural-spawn predicate that forgets about
 * spawners shows up. The passive mobs run in the open, lit arena (the report says lighting the
 * spawner does not help); the hostile ones run inside a sealed, dark box, as a vanilla monster
 * spawner would need.
 * <p>
 * Every case also parks {@link #CROWD} unrelated entities next to the spawner. BCLib's
 * {@code SpawnRuleBuilder#maxNearby} used to filter by {@code EntityType#getBaseClass()}, which is
 * always {@code Entity.class}, so it counted <i>every</i> entity in a 129-block-wide column against
 * a per-type limit of 16-32. Near any base, farm or item pile the spawner silently rejected every
 * attempt - the most likely cause of #281. Markers are used because they neither collide nor block
 * spawning, so the only thing they can influence is that count.
 */
public class SpawnEggSpawnerGameTest {
    private static final BlockPos SPAWNER = new BlockPos(3, 2, 3);
    private static final int TICKS = 400;
    /** More than the largest {@code maxNearby} limit any BetterNether mob uses (32). */
    private static final int CROWD = 40;

    @GameTest(maxTicks = TICKS)
    public void fireflySpawner(GameTestHelper helper) {
        run(helper, NetherEntities.FIREFLY.type(), false);
    }

    @GameTest(maxTicks = TICKS)
    public void fireflySpawnerNextToLight(GameTestHelper helper) {
        run(helper, NetherEntities.FIREFLY.type(), false, true);
    }

    @GameTest(maxTicks = TICKS)
    public void hydrogenJellyfishSpawner(GameTestHelper helper) {
        run(helper, NetherEntities.HYDROGEN_JELLYFISH.type(), false);
    }

    @GameTest(maxTicks = TICKS)
    public void flyingPigSpawner(GameTestHelper helper) {
        run(helper, NetherEntities.FLYING_PIG.type(), false);
    }

    @GameTest(maxTicks = TICKS)
    public void nagaSpawner(GameTestHelper helper) {
        run(helper, NetherEntities.NAGA.type(), true);
    }

    @GameTest(maxTicks = TICKS)
    public void jungleSkeletonSpawner(GameTestHelper helper) {
        run(helper, NetherEntities.JUNGLE_SKELETON.type(), true);
    }

    @GameTest(maxTicks = TICKS)
    public void skullSpawner(GameTestHelper helper) {
        run(helper, NetherEntities.SKULL.type(), true);
    }


    private static void run(GameTestHelper helper, EntityType<?> type, boolean dark) {
        run(helper, type, dark, false);
    }

    private static void run(GameTestHelper helper, EntityType<?> type, boolean dark, boolean lit) {
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -3; z <= 3; z++) {
                    final BlockPos p = SPAWNER.offset(x, y, z);
                    if (helper.getBlockState(p).isAir()) continue;
                    helper.setBlock(p, Blocks.AIR);
                }
            }
        }
        if (dark) {
            // Seal a 7x5x7 cavity so neither sky nor block light reaches it.
            for (int x = -4; x <= 4; x++) {
                for (int y = -1; y <= 5; y++) {
                    for (int z = -4; z <= 4; z++) {
                        if (Math.abs(x) == 4 || Math.abs(z) == 4 || y == -1 || y == 5) {
                            helper.setBlock(SPAWNER.offset(x, y, z), Blocks.STONE);
                        }
                    }
                }
            }
        }
        if (lit) {
            helper.setBlock(SPAWNER.offset(1, 0, 0), Blocks.GLOWSTONE);
        }
        for (int i = 0; i < CROWD; i++) {
            helper.spawn(EntityType.MARKER, SPAWNER.offset(-3, 0, -3));
        }
        helper.setBlock(SPAWNER, Blocks.SPAWNER);

        final SpawnerBlockEntity spawner = helper.getBlockEntity(SPAWNER, SpawnerBlockEntity.class);
        // Exactly what SpawnEggItem#useOn does to a spawner.
        spawner.setEntityId(type, helper.getLevel().getRandom());

        MockPlayers.survival(helper, SPAWNER.offset(0, 0, 2));

        final AABB box = new AABB(helper.absolutePos(SPAWNER)).inflate(8);
        helper.succeedWhen(() -> {
            final int count = helper.getLevel().getEntities(type, box, e -> true).size();
            helper.assertTrue(count > 0, "spawner configured with " + type + " spawned nothing");
        });
    }
}
