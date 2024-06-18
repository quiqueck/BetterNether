package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherTerrain;
import org.betterx.betternether.registry.features.placed.NetherTerrainPlaced;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.block.api.predicate.IsFullShape;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;
import org.betterx.wover.feature.api.Features;
import org.betterx.wover.feature.api.features.config.ConditionFeatureConfig;
import org.betterx.wover.feature.api.placed.modifiers.IsBasin;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
                .is(BlockPredicate.matchesBlocks(NetherBlocks.SOUL_SANDSTONE))
                .register();
    }
}
