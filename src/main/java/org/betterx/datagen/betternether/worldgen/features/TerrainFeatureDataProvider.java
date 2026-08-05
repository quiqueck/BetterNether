package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import org.betterx.betternether.blocks.BlockGloomsculkCrystal;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherTerrain;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import de.ambertation.wover.block.api.predicate.BlockPredicates;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.block.api.predicate.IsFullShape;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.Features;
import de.ambertation.wover.feature.api.features.config.ConditionFeatureConfig;
import de.ambertation.wover.feature.api.placed.modifiers.IsBasin;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class TerrainFeatureDataProvider extends WoverFeatureProvider {
    public TerrainFeatureDataProvider(ModCore modCore) {
        super(modCore, modCore.id("terrain"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherTerrain.LAVA_PITS
                .bootstrap(ctx)
                .block(Blocks.LAVA)
                .register();

        // Sculk vein as a surface decoration on the gloomwood's sculk. All three faces are enabled, so a
        // single origin takes whichever of floor, wall or ceiling it happens to find first, and the placed
        // features below simply decide where to look.
        //
        // The anchor is the SCULK_LIKE tag rather than a block list: it covers the vanilla sculk that makes
        // up most of the biome's floor and ceiling, the gloomsculk ground blocks, and the geodes set into
        // them, which is exactly the surface the veins should be creeping over.
        NetherTerrain.SCULK_VEIN
                .bootstrap(ctx)
                .configuration(new MultifaceGrowthConfiguration(
                        (MultifaceSpreadeableBlock) Blocks.SCULK_VEIN,
                        // Half vanilla's 20. The gloomwood is not the deep dark: the veins are meant to
                        // read as patches on the sculk, not as a continuous skin over the whole cavern.
                        10,
                        true,
                        true,
                        true,
                        // The spread is what turns a single attached face into a patch, and it is the
                        // block's own spreader doing it, so it follows the surface it is on.
                        0.75F,
                        ctx.lookup(Registries.BLOCK).getOrThrow(CommonBlockTags.SCULK_LIKE)
                ))
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
        NetherTerrainPlaced.MAGMA_BLOBS
                .inlineConfiguration(ctx)
                .simple()
                .block(Blocks.MAGMA_BLOCK)
                .inlinePlace()
                .countRange(1, 2)
                .spreadHorizontal(ClampedNormalInt.of(0, 2, -4, -4))
                .stencil()
                .onEveryLayer()
                .onlyInBiome()
                .offset(Direction.DOWN)
                .is(BlockPredicates.ONLY_GROUND)
                .extendDown(0, 3)
                .register();

        // Molten gloomsculk, in two passes. The scattered pass keeps a little of it away from the lava so
        // the biome floor is not uniformly cool, and the lava pass is the one that matters: it fires on the
        // ground ring immediately around a pit, which is where the fissures are supposed to be coming from.
        // Both extend a few blocks down so a pit wall shows molten rock in section rather than a 1-deep skin.
        NetherTerrainPlaced.MOLTEN_GLOOMSCULK
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.MOLTEN_GLOOMSCULK)
                .inlinePlace()
                .countRange(1, 2)
                .spreadHorizontal(ClampedNormalInt.of(0, 2, -3, 3))
                .stencil()
                .onEveryLayer()
                .onlyInBiome()
                .offset(Direction.DOWN)
                .is(BlockPredicates.ONLY_GROUND)
                .onceEvery(4)
                .extendDown(0, 2)
                .register();

        NetherTerrainPlaced.MOLTEN_GLOOMSCULK_NEAR_LAVA
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.MOLTEN_GLOOMSCULK)
                .inlinePlace()
                .countRange(2, 4)
                .spreadHorizontal(ClampedNormalInt.of(0, 3, -5, 5))
                .all()
                .onEveryLayer()
                .onlyInBiome()
                .offset(Direction.DOWN)
                .is(BlockPredicates.ONLY_GROUND)
                .isNextTo(BlockPredicates.ONLY_LAVA)
                .extendDown(0, 3)
                .register();

        // Geodes replace the surface block itself: the filter picks an empty position against the
        // rock, then the offset steps into the rock and the geode is written there. Placed the way the
        // molten patches are placed instead they end up buried - those fill terrain volume, and a geode
        // with netherrack on top is a geode nobody will ever see.
        //
        // Both surfaces accept sculk-like ground or ordinary nether ground: the biome's surface rule
        // only sculks the floor, so a sculk-only predicate finds almost nothing overhead.
        final BlockPredicate geodeAnchor = BlockPredicate.anyOf(
                BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE),
                BlockPredicates.ONLY_NETHER_GROUND
        );

        NetherTerrainPlaced.GLOOMSCULK_GEODE_FLOOR
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.GLOOMSCULK_GEODE)
                .inlinePlace()
                .betterNetherGround(8)
                .isEmptyAndOn(geodeAnchor)
                .offset(Direction.DOWN)
                .register();

        // ...and some that simply sit on the sculk rather than being set into it. Same filter, minus the
        // offset: the geode is written into the empty position itself, so it stands proud of the floor
        // with all four sides and its cap showing.
        NetherTerrainPlaced.GLOOMSCULK_GEODE_ON_FLOOR
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.GLOOMSCULK_GEODE)
                .inlinePlace()
                .betterNetherGround(4)
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        NetherTerrainPlaced.GLOOMSCULK_GEODE_CEILING
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.GLOOMSCULK_GEODE)
                .inlinePlace()
                .betterNetherCeiling(3)
                .isEmptyAndUnder(geodeAnchor)
                .offset(Direction.UP)
                .register();

        NetherTerrainPlaced.GLOOMSCULK_CRYSTAL_FLOOR
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.GLOOMSCULK_CRYSTAL
                        .defaultBlockState()
                        .setValue(BlockGloomsculkCrystal.FACING, Direction.UP))
                .inlinePlace()
                // scatter() rather than inRandomPatch(): vanilla dropped the random_patch configured
                // feature in 26.1, WorldWeaver only keeps a shim for it, and scatter stays on the
                // placement builder so the per-layer count can follow it.
                .betterNetherGround(3)
                .scatter(10, 5, 1)
                .isEmptyAndOn(geodeAnchor)
                .register();

        NetherTerrainPlaced.GLOOMSCULK_CRYSTAL_CEILING
                .inlineConfiguration(ctx)
                .simple()
                .block(NetherTerrainBlocks.GLOOMSCULK_CRYSTAL
                        .defaultBlockState()
                        .setValue(BlockGloomsculkCrystal.FACING, Direction.DOWN))
                .inlinePlace()
                .betterNetherCeiling(3)
                .scatter(10, 5, 1)
                .isEmptyAndUnder(geodeAnchor)
                .register();

        // The three sets of origins for the sculk vein feature. Each one only has to land an empty block
        // against the surface it is named for - the feature itself picks the face and spreads from there.
        NetherTerrainPlaced.SCULK_VEIN_FLOOR
                .place(ctx)
                .betterNetherGround(12)
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        NetherTerrainPlaced.SCULK_VEIN_CEILING
                .place(ctx)
                .betterNetherCeiling(8)
                .isEmptyAndUnder(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        // No sculk-only filter on the wall pass: a gloomwood wall is mostly netherrack with the veined
        // transition layer capping it, and requiring sculk at the origin would reject nearly every one of
        // them. The feature's own canBePlacedOn does the filtering a step later, when it looks for a face.
        NetherTerrainPlaced.SCULK_VEIN_WALL
                .place(ctx)
                .betterNetherOnWall(8)
                .isEmpty()
                .register();

        NetherTerrainPlaced.LAVA_PIT.place(ctx).register();

        NetherTerrainPlaced.LAVA_PITS_SPARSE
                .place(ctx)
                .onEveryLayer()
                .stencil()
                .findSolidFloor(3)
                .onlyInBiome()
                .offset(Direction.DOWN)
                .inBasinOf(BlockPredicates.ONLY_GROUND_OR_LAVA)
                .onceEvery(6)
                .register();

        NetherTerrainPlaced.LAVA_PITS_DENSE
                .place(ctx)
                .onEveryLayer()
                .stencil()
                .findSolidFloor(3)
                .onlyInBiome()
                .offset(Direction.DOWN)
                .inBasinOf(BlockPredicates.ONLY_GROUND_OR_LAVA)
                .onceEvery(2)
                .register();

        NetherTerrainPlaced.LAVA_SWAMP
                .place(ctx)
                .all()
                .onEveryLayer()
                .offset(Direction.DOWN)
                .onlyInBiome()
                .noiseAbove(0.1f, 20, 10)
                .inBasinOf(BlockPredicates.ONLY_GROUND_OR_LAVA)
                .register();

        NetherTerrainPlaced.LAVA_TERRACE
                .place(ctx)
                .all()
                .onEveryLayer()
                .onlyInBiome()
                .offset(Direction.DOWN)
                .inBasinOf(BlockPredicates.ONLY_GROUND_OR_LAVA)
                .register();

        NetherTerrainPlaced.BASALT_OR_AIR
                .inlineConfiguration(ctx)
                .randomBlock()
                .add(Blocks.BASALT, 15)
                .add(Blocks.AIR, 15)
                .inlinePlace()
                .register();

        NetherTerrainPlaced.EXTEND_BASALT
                .inlineConfiguration(ctx)
                .simple()
                .block(Blocks.BASALT)
                .inlinePlace()
                .offset(Direction.DOWN)
                .extendDown(1, 3)
                .register();

        NetherTerrainPlaced.MARK
                .inlineConfiguration(ctx)
                .withFeature(Features.MARK_POSTPROCESSING)
                .inlinePlace()
                .is(BlockPredicate.matchesBlocks(Blocks.LAVA))
                .register();

        NetherTerrainPlaced.FLOODED_LAVA_PIT_SURFACE
                .inlineConfiguration(ctx)
                .withFeature(Features.CONDITION)
                .configuration(new ConditionFeatureConfig(
                        IsBasin.simple(
                                BlockPredicate.anyOf(
                                        BlockPredicate.matchesBlocks(Blocks.LAVA),
                                        IsFullShape.HERE
                                )
                        ),
                        NetherTerrainPlaced.LAVA_PIT.getHolder(ctx),
                        NetherTerrainPlaced.BASALT_OR_AIR.getHolder(ctx)

                ))
                .inlinePlace()
                .register();


        NetherTerrainPlaced.FLOODED_LAVA_PIT
                .inlineConfiguration(ctx)
                .sequence()
                .add(NetherTerrainPlaced.EXTEND_BASALT)
                .add(NetherTerrainPlaced.FLOODED_LAVA_PIT_SURFACE)
                .add(NetherTerrainPlaced.MARK)
                .inlinePlace()
                .all()
                .onEveryLayer()
                .offset(Direction.DOWN)
                .onlyInBiome()
                .register();

        NetherTerrainPlaced.REPLACE_SOUL_SANDSTONE
                .inlineConfiguration(ctx)
                .simple()
                .block(Blocks.SOUL_SAND)
                .inlinePlace()
                .all()
                .onEveryLayerMin4()
                .onlyInBiome()
                .offset(Direction.DOWN)
                .is(BlockPredicate.matchesBlocks(NetherStoneBlocks.SOUL_SANDSTONE))
                .register();
    }
}
