package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;

import org.betterx.betternether.blocks.BlockNeonEquisetum;
import org.betterx.betternether.blocks.BlockWhisperingGourdVine;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherVines;
import org.betterx.betternether.registry.features.placed.NetherVinesPlaced;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VineFeatureDataProvider extends WoverFeatureProvider {
    public VineFeatureDataProvider(ModCore modCore) {
        super(modCore, modCore.id("vine"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherVines.LUMABUS_VINE
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .prioritizeTip()
                .addTripleShapeUpsideDown(NetherVineBlocks.LUMABUS_VINE.defaultBlockState(), ClampedNormalInt.of(10, 3, 3, 21))
                .register();
        NetherVines.PATCH_LUMABUS_VINE
                .bootstrap(ctx)
                .featureToPlace(NetherVinesPlaced.PLACED_LUMABUS_VINE)
                .tries(48)
                .spreadXZ(5)
                .register();
        NetherVines.GOLDEN_LUMABUS_VINE
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .prioritizeTip()
                .addTripleShapeUpsideDown(NetherVineBlocks.GOLDEN_LUMABUS_VINE.defaultBlockState(), ClampedNormalInt.of(12, 3.3f, 2, 23))
                .register();
        NetherVines.PATCH_GOLDEN_LUMABUS_VINE
                .bootstrap(ctx)
                .featureToPlace(NetherVinesPlaced.PLACED_GOLDEN_LUMABUS_VINE)
                .tries(64)
                .spreadXZ(6)
                .register();
        NetherVines.PATCH_GOLDEN_VINE
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .prioritizeTip()
                .addBottomShapeUpsideDown(NetherVineBlocks.GOLDEN_VINE.defaultBlockState(), ClampedNormalInt.of(12, 3, 3, 23))
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .tries(64)
                .spreadXZ(6)
                .register();
        NetherVines.PATCH_GOLDEN_VINE_SPARSE
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .addBottomShapeUpsideDown(NetherVineBlocks.GOLDEN_VINE.defaultBlockState(), ClampedNormalInt.of(12, 3, 3, 23))
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .tries(32)
                .spreadXZ(6)
                .register();
        NetherVines.EYE_VINE
                .bootstrap(ctx)
                .prioritizeTip()
                .direction(Direction.DOWN)
                .add(ClampedNormalInt.of(6, 3, 3, 16), NetherVineBlocks.EYE_VINE.defaultBlockState())
                .addRandom(1, NetherPlantBlocks.EYEBALL.defaultBlockState(), NetherPlantBlocks.EYEBALL_SMALL.defaultBlockState())
                .register();
        NetherVines.PATCH_EYE_VINE
                .bootstrap(ctx)
                .featureToPlace(NetherVinesPlaced.PLACED_EYE_VINE)
                .tries(48)
                .spreadXZ(5)
                .register();
        NetherVines.PATCH_BLACK_VINE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.BOTTOM_GROW)
                .direction(Direction.DOWN)
                .blockState(NetherVineBlocks.BLACK_VINE)
                .minHeight(3)
                .maxHeight(ClampedNormalInt.of(12, 2.3f, 3, 16))
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .tries(24)
                .spreadXZ(5)
                .register();
        NetherVines.PATCH_TWISTING_VINES
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(10)
                .spreadXZ(5)
                .register();
        NetherVines.PATCH_BLOOMING_VINE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.BOTTOM_GROW)
                .direction(Direction.DOWN)
                .blockState(NetherVineBlocks.BLOOMING_VINE)
                .minHeight(3)
                .maxHeight(ClampedNormalInt.of(14, 2, 3, 21))
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .tries(32)
                .spreadXZ(5)
                .register();
        NetherVines.NEON_EQUISETUM
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .add(BiasedToBottomInt.of(1, 21), NetherPlantBlocks.NEON_EQUISETUM
                        .defaultBlockState()
                        .setValue(BlockNeonEquisetum.SHAPE, BlockProperties.TripleShape.TOP))
                .add(1, NetherPlantBlocks.NEON_EQUISETUM
                        .defaultBlockState()
                        .setValue(BlockNeonEquisetum.SHAPE, BlockProperties.TripleShape.MIDDLE))
                .add(1, NetherPlantBlocks.NEON_EQUISETUM
                        .defaultBlockState()
                        .setValue(BlockNeonEquisetum.SHAPE, BlockProperties.TripleShape.BOTTOM))
                .prioritizeTip()
                .register();
        NetherVines.PATCH_NEON_EQUISETUM
                .bootstrap(ctx)
                .featureToPlace(NetherVinesPlaced.PLACED_NEON_EQUISETUM)
                .tries(32)
                .spreadXZ(5)
                .register();
        NetherVines.WHISPERING_GOURD_VINE
                .bootstrap(ctx)
                .direction(Direction.DOWN)
                .add(1, NetherVineBlocks.WHISPERING_GOURD_VINE
                        .defaultBlockState()
                        .setValue(BlockWhisperingGourdVine.SHAPE, BlockProperties.TripleShape.BOTTOM))
                .add(BiasedToBottomInt.of(1, 5), new WeightedStateProvider(WeightedList
                        .<BlockState>builder()
                        .add(NetherVineBlocks.WHISPERING_GOURD_VINE
                                .defaultBlockState()
                                .setValue(BlockWhisperingGourdVine.SHAPE, BlockProperties.TripleShape.TOP), 5)
                        .add(NetherVineBlocks.WHISPERING_GOURD_VINE
                                .defaultBlockState()
                                .setValue(BlockWhisperingGourdVine.SHAPE, BlockProperties.TripleShape.MIDDLE), 5)))
                .prioritizeTip()
                .register();
        NetherVines.PATCH_WHISPERING_GOURD_VINE
                .bootstrap(ctx)
                .featureToPlace(NetherVinesPlaced.PLACED_WHISPERING_GOURD_VINE)
                .tries(16)
                .spreadXZ(3)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
        NetherVinesPlaced.PLACED_LUMABUS_VINE.place(ctx).isEmptyAndUnderNetherGround().register();
        NetherVinesPlaced.PLACED_GOLDEN_LUMABUS_VINE.place(ctx).isEmptyAndUnderNetherGround().register();
        NetherVinesPlaced.PLACED_EYE_VINE.place(ctx).isEmptyAndUnderNetherGround().register();
        NetherVinesPlaced.PLACED_NEON_EQUISETUM.place(ctx).isEmptyAndUnderNetherGround().register();
        NetherVinesPlaced.PLACED_WHISPERING_GOURD_VINE.place(ctx).isEmptyAndUnderNetherGround().register();
        NetherVinesPlaced.LUMABUS_VINE.place(ctx).betterNetherCeiling(12).onceEvery(2).register();
        NetherVinesPlaced.GOLDEN_LUMABUS_VINE.place(ctx).betterNetherCeiling(8).onceEvery(2).register();
        NetherVinesPlaced.GOLDEN_VINE.place(ctx).betterNetherCeiling(4).onceEvery(2).register();
        NetherVinesPlaced.GOLDEN_VINE_SPARSE.place(ctx).betterNetherCeiling(4).onceEvery(3).register();
        NetherVinesPlaced.EYE_VINE.place(ctx).betterNetherCeiling(4).onceEvery(2).register();
        NetherVinesPlaced.BLACK_VINE.place(ctx).betterNetherCeiling(4).onceEvery(2).register();
        NetherVinesPlaced.BLOOMING_VINE.place(ctx).betterNetherCeiling(4).onceEvery(2).register();
        NetherVinesPlaced.TWISTING_VINES.place(ctx).vanillaNetherGround(12).onceEvery(2).register();
        NetherVinesPlaced.NEON_EQUISETUM.place(ctx).betterNetherCeiling(12).onceEvery(2).register();
        NetherVinesPlaced.PATCH_WHISPERING_GOURD_VINE.place(ctx).betterNetherCeiling(4).onceEvery(2).register();
    }
}