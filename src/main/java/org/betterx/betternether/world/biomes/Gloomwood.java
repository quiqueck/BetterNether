package org.betterx.betternether.world.biomes;

import de.ambertation.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import de.ambertation.wover.surface.api.Conditions;
import de.ambertation.wover.surface.api.conditions.NoiseCondition;
import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.SoundsRegistry;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.features.placed.*;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

/**
 * A quiet, bleached forest growing out of a sculk floor.
 * <p>
 * The ground is bleached gloomsculk broken up by patches of vanilla sculk and a trace of molten
 * gloomsculk, with more molten crazing the rock around the lava pits. Gloomwood trees stand over
 * gloomgrass - the pale variant on the bleached floor, the dark one on the sculk patches, so the
 * cover follows the ground rather than being scattered across it - and gloomwisps,
 * and old bones surface through the sculk here and there.
 */
public class Gloomwood extends NetherBiomeConfig {
    /**
     * The netherrack islands breaking up the sculk ceiling.
     * <p>
     * A finer scale than {@link Conditions#NETHER_SURFACE_NOISE_LARGE} so the patches come out as small
     * islands rather than continents, and the same roughness band, which leaves them a clear minority
     * against the sculk they sit in.
     */
    private static final NoiseCondition CEILING_NETHERRACK = Conditions.threshold(
            0x61006EL, 0, UniformFloat.of(-0.4F, -0.3F), 0.25, 0.25
    );

    /**
     * Where the ceiling sculk reaches its full five blocks - about a third of it, in broad patches.
     */
    private static final NoiseCondition CEILING_DEEP = Conditions.threshold(
            0x61005DL, 0, UniformFloat.of(-0.35F, -0.15F), 0.08, 0.08
    );

    /**
     * The vanilla sculk patches in the bleached floor - a measured 19% of it.
     * <p>
     * The roughness band is narrow against the noise it is added to, so the noise decides and the
     * patches come out as coherent blotches about ten blocks across rather than as per-block speckle.
     * That is the point of the whole floor mix: three materials at these shares scattered block by
     * block read as static, and the biome was noisy for exactly that reason.
     */
    private static final NoiseCondition FLOOR_SCULK = Conditions.threshold(
            0x6100A1L, 0, UniformFloat.of(-0.468F, -0.268F), 0.1, 0.1
    );

    /**
     * The molten crazing in the floor - a measured 1%, on a finer scale than {@link #FLOOR_SCULK} so it
     * lands as fissures a few blocks wide instead of small patches.
     * <p>
     * This is the molten that is spread over the whole biome. The molten <i>around the lava</i> is a
     * separate thing and stays a feature ({@code MOLTEN_GLOOMSCULK_NEAR_LAVA}), because it has to find
     * the pits, which a surface rule cannot see.
     */
    private static final NoiseCondition FLOOR_MOLTEN = Conditions.threshold(
            0x6100B2L, 0, UniformFloat.of(-0.835F, -0.635F), 0.25, 0.25
    );

    @Override
    public void addCustomBuildData(NetherBiomeBuilder builder) {
        builder.fogColor(84, 116, 122)
               .loop(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
               .additions(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS)
               .mood(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD)
               .music(SoundsRegistry.MUSIC_GLOOMWOOD, 1800, 7200, false)
               .particles(ParticleTypes.WARPED_SPORE, 0.015F)
               .structure(BiomeTags.HAS_NETHER_FORTRESS)
               .structure(NetherStructures.MEGA_LAVA_LAKE)
               // Order matters inside a decoration step: the pits are cut first so the molten rock that
               // keys off them has something to find. Both sit in LAKES, ahead of everything vegetal.
               .feature(NetherTerrainPlaced.LAVA_PITS_SPARSE)
               .feature(NetherTerrainPlaced.MOLTEN_GLOOMSCULK_NEAR_LAVA)
               .feature(NetherTerrainPlaced.MOLTEN_GLOOMSCULK)
               .feature(NetherVegetationPlaced.GLOOMWISP_VINE)
               .feature(NetherVegetationPlaced.GLOOMWISP_VINE_HEAD)
               .feature(NetherTerrainPlaced.GLOOMSCULK_GEODE_FLOOR)
               .feature(NetherTerrainPlaced.GLOOMSCULK_GEODE_ON_FLOOR)
               .feature(NetherTerrainPlaced.GLOOMSCULK_GEODE_CEILING)
               .feature(NetherOresPlaced.GLOOMWOOD_CEILING_DEBRIS)
               .feature(NetherObjectsPlaced.BONES)
               .feature(NetherObjectsPlaced.BONE_STALAGMITE)
               // The three grove densities. All three are needed: they take disjoint slices of one noise
               // field, so leaving one out would leave that share of the biome treeless rather than
               // thinner. See NetherTreesPlaced.GLOOMWOOD_TREE.
               .feature(NetherTreesPlaced.GLOOMWOOD_TREE)
               .feature(NetherTreesPlaced.GLOOMWOOD_TREE_EDGE)
               .feature(NetherTreesPlaced.GLOOMWOOD_TREE_SOLITARY)
               // The grass follows the floor: pale on the bleached gloomsculk, dark on the sculk patches.
               .feature(NetherVegetationPlaced.VEGETATION_GLOOMWOOD_BLEACHED)
               .feature(NetherVegetationPlaced.VEGETATION_GLOOMWOOD_SCULK)
               // after the geodes, so the crystals have something to have grown out of
               .feature(NetherTerrainPlaced.GLOOMSCULK_CRYSTAL_FLOOR)
               .feature(NetherTerrainPlaced.GLOOMSCULK_CRYSTAL_CEILING)
               // Vanilla sculk vein creeping over all three surfaces. Last of the sculk work, so it runs
               // over the molten patches and the geodes rather than being buried by them.
               .feature(NetherTerrainPlaced.SCULK_VEIN_FLOOR)
               .feature(NetherTerrainPlaced.SCULK_VEIN_WALL)
               .feature(NetherTerrainPlaced.SCULK_VEIN_CEILING)
               .feature(NetherVinesPlaced.GLOOMSCULK_VINE)
               // A few strands of lumabus among the gloomsculk vine. Its cold blue-green glow is the one
               // light in the biome that is not the gloomwisps' own, and it is deliberately well under
               // them in frequency - a guest on the ceiling rather than a second cover.
               .feature(NetherVinesPlaced.LUMABUS_VINE_SPARSE)
               .feature(NetherObjectsPlaced.STALACTITE)
               .addNetherClimate(-0.25f, -0.35f, 0.0f)
               .genChance(0.3f)
        ;
    }

    /**
     * Fireflies over the sculk. The gloomwood is otherwise a silent, unmoving biome, and a drifting
     * light is the cheapest thing that makes it feel alive.
     */
    @Override
    public <M extends Mob> int spawnWeight(NetherEntities.KnownSpawnTypes type) {
        int res = super.spawnWeight(type);
        switch (type) {
            case FIREFLY -> res = type.weight * 3;
        }
        return res;
    }

    @Override
    public void surface(BiomeSurfaceRuleBuilder<NetherBiomeBuilder> builder) {
        super.surface(builder);
        // Bleached gloomsculk is the floor; the other two are what breaks it up. Measured over a
        // 1500x1500 sample of the conditions below, the split comes out at 1% molten, 19% sculk and 80%
        // bleached - the shares are a property of the roughness bands, not of the order, so retuning one
        // of them means re-measuring rather than guessing.
        //
        // First match wins, so the rarest block is asked first: written the other way round the sculk
        // rule would answer for most of the columns the molten rule wanted, and molten would come out at
        // a fraction of its 2%.
        builder.chancedFloor(
                NetherTerrainBlocks.MOLTEN_GLOOMSCULK.defaultBlockState(),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(FLOOR_SCULK, SurfaceRules.state(Blocks.SCULK.defaultBlockState())),
                        SurfaceRules.state(NetherTerrainBlocks.BLEACHED_GLOOMSCULK.defaultBlockState())
                ),
                FLOOR_MOLTEN
        );

        // Exactly one block of the transition layer, directly under the floor. The depth is not a
        // tuning choice: the block carries sculk on its top face and netherrack on its bottom, so a
        // second one stacked below would show its netherrack bottom against the first's sculk top and
        // reintroduce the very seam the layer exists to remove.
        builder.belowFloor(NetherTerrainBlocks.VEINED_GLOOMSCULK.defaultBlockState(), 1);

        // The ceiling is sculk mixed into the netherrack rather than a clean sheet of either, hanging
        // one to five blocks deep. Three tiers of thickness instead of a smooth range: a surface rule
        // can only test a fixed depth, so depth comes from stacking rules at descending priorities and
        // letting the first match win. The noises are independent, so the tiers do not nest neatly and
        // the underside comes out lumpy rather than terraced.
        final var sculk = Blocks.SCULK.defaultBlockState();
        // The skin itself is the mix. ceil() takes a single state and there is no chancedCeil, so this
        // is a sequence, and the noise picks out the minority block: netherrack in islands, sculk as the
        // fallback everywhere else. Written this way round deliberately - a threshold condition is true
        // well under half the time, so whichever state it guards is the one that ends up scattered.
        builder.rule(
                SurfaceRules.ifTrue(
                        SurfaceRules.stoneDepthCheck(1, false, CaveSurface.CEILING),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        CEILING_NETHERRACK,
                                        SurfaceRules.state(Blocks.NETHERRACK.defaultBlockState())
                                ),
                                SurfaceRules.state(sculk)
                        )
                ),
                BaseSurfaceRuleBuilder.CEILING_PRIORITY
        );
        builder.rule(
                SurfaceRules.ifTrue(
                        CEILING_DEEP,
                        SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(5, false, CaveSurface.CEILING),
                                SurfaceRules.state(sculk)
                        )
                ),
                BaseSurfaceRuleBuilder.ABOVE_CEILING_PRIORITY + 10
        );
        builder.aboveCeil(sculk, 3);
    }
}
