package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.configured.NetherObjects;
import org.betterx.betternether.registry.features.placed.NetherObjectsPlaced;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;
import org.betterx.wover.feature.api.placed.modifiers.ExtendXYZ;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ObjectFeatureDataProvider extends WoverFeatureProvider {
    public ObjectFeatureDataProvider(ModCore modCore) {
        super(modCore, modCore.id("objects"));
    }

    @Override
    protected void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherObjects.PATCH_BASALT_STALACTITE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(NetherBlocks.BASALT_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(4, 11))
                .inlinePlace()
                .isEmptyAndUnder(org.betterx.wover.block.api.predicate.BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
                .inRandomPatch()
                .register();

        NetherObjects.PATCH_BASALT_STALAGMITE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.UP)
                .blockState(NetherBlocks.BASALT_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(3, 9))
                .inlinePlace()
                .isEmptyAndOn(org.betterx.wover.block.api.predicate.BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
                .inRandomPatch()
                .register();

        NetherObjects.PATCH_SMOKER
                .bootstrap(ctx)
                .direction(Direction.UP)
                .addTripleShape(NetherBlocks.SMOKER.defaultBlockState(), BiasedToBottomInt.of(0, 4))
                .prioritizeTip()
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(18)
                .spreadXZ(4)
                .spreadY(3)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstapContext<PlacedFeature> ctx) {
        NetherObjectsPlaced.PATCH_TERRACOTTA_CLUMP
                .inlineConfiguration(ctx)
                .simple()
                .block(Blocks.BLACK_GLAZED_TERRACOTTA)
                .inlinePlace()
                .extendXYZ(
                        UniformInt.of(3, 4), ConstantFloat.of(1.0f), UniformFloat.of(0.1f, 0.3f),
                        UniformFloat.of(0.8f, 1.5f),
                        false, ExtendXYZ.HeightPropagation.SPHERE_DOWN
                )
                .is(BlockPredicate.matchesBlocks(Blocks.BASALT))
                .register();

        NetherObjectsPlaced.BONES
                .inlineConfiguration(ctx)
                .templates()
                .add(modCore.id("bone_01"), 0, 1.0f)
                .add(modCore.id("bone_02"), 0, 1.0f)
                .add(modCore.id("bone_03"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(5)
                .register();

        NetherObjectsPlaced.JUNGLE_BONES
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("jungle_bones_3"), 0, 1.0f)
                .add(BN.id("jungle_bones_2"), 0, 1.0f)
                .add(BN.id("jungle_bones_1"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(6)
                .register();

        NetherObjectsPlaced.OBSIDIAN_CRYSTAL
                .inlineConfiguration(ctx)
                .configuration(NetherFeatures.OBSIDIAN_CRYSTAL)
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.BONE_STALAGMITE
                .inlineConfiguration(ctx)
                .sequence()
                .add(ConfiguredFeatureManager.INLINE_BUILDER
                        .simple()
                        .block(NetherBlocks.BONE_BLOCK)
                        .inlinePlace()
                        .extendXYZ(
                                UniformInt.of(3, 4), ConstantFloat.of(1.0f), UniformFloat.of(0.1f, 0.3f),
                                UniformFloat.of(0.8f, 1.5f),
                                false, ExtendXYZ.HeightPropagation.SPIKES_DOWN
                        )
                        .is(BlockPredicates.ONLY_NETHER_GROUND)
                        .directHolder()
                )
                .add(ConfiguredFeatureManager.INLINE_BUILDER
                        .pillar()
                        .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                        .direction(Direction.UP)
                        .blockState(NetherBlocks.BONE_STALACTITE)
                        .maxHeight(BiasedToBottomInt.of(2, 7))
                        .inlinePlace()
                        .isEmpty()
                        .isOn(BlockPredicate.matchesBlocks(NetherBlocks.BONE_BLOCK, Blocks.BONE_BLOCK))
                        .inRandomPatch()
                        .spreadXZ(4)
                        .inlinePlace()
                        .directHolder()
                )
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.STALACTITE
                .inlineConfiguration(ctx)
                .pillar()
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(NetherBlocks.NETHERRACK_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(2, 7))
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .inlinePlace()
                .betterNetherCeiling(1)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.STALAGMITE
                .inlineConfiguration(ctx)
                .pillar()
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.UP)
                .blockState(NetherBlocks.NETHERRACK_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(2, 7))
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.BASALT_STALACTITE
                .place(ctx, NetherObjects.PATCH_BASALT_STALACTITE)
                .betterNetherCeiling(8)
                .register();

        NetherObjectsPlaced.BASALT_STALACTITE_SPARSE
                .place(ctx, NetherObjects.PATCH_BASALT_STALACTITE)
                .betterNetherCeiling(1)
                .onceEvery(5)
                .register();

        NetherObjectsPlaced.BASALT_STALAGMITE
                .place(ctx, NetherObjects.PATCH_BASALT_STALAGMITE)
                .vanillaNetherGround(1)
                .onceEvery(1)
                .register();

        NetherObjectsPlaced.BASALT_STALAGMITE_SPARSE
                .place(ctx, NetherObjects.PATCH_BASALT_STALAGMITE)
                .vanillaNetherGround(1)
                .onceEvery(5)
                .register();

        NetherObjectsPlaced.BLACKSTONE_STALACTITE
                .inlineConfiguration(ctx)
                .sequence()
                .add(NetherObjectsPlaced.PATCH_TERRACOTTA_CLUMP)
                .add(ConfiguredFeatureManager.INLINE_BUILDER
                        .pillar()
                        .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                        .direction(Direction.DOWN)
                        .blockState(NetherBlocks.BLACKSTONE_STALACTITE)
                        .maxHeight(BiasedToBottomInt.of(3, 9))
                        .inlinePlace()
                        .isOn(BlockPredicate.matchesBlocks(Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.BASALT))
                        .inRandomPatch()
                        .inlinePlace()
                        .directHolder()
                )
                .inlinePlace()
                .betterNetherCeiling(4)
                .register();

        NetherObjectsPlaced.BLACKSTONE_STALAGMITE
                .inlineConfiguration(ctx)
                .sequence()
                .add(NetherObjectsPlaced.PATCH_TERRACOTTA_CLUMP)
                .add(ConfiguredFeatureManager.INLINE_BUILDER
                        .pillar()
                        .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                        .direction(Direction.UP)
                        .blockState(NetherBlocks.BLACKSTONE_STALACTITE)
                        .maxHeight(BiasedToBottomInt.of(3, 8))
                        .inlinePlace()
                        .isOn(BlockPredicate.matchesBlocks(Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.BASALT))
                        .inRandomPatch()
                        .inlinePlace()
                        .directHolder()
                )
                .inlinePlace()
                .vanillaNetherGround(1)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.SMOKER
                .place(ctx, NetherObjects.PATCH_SMOKER)
                .vanillaNetherGround(3)
                .onceEvery(2)
                .register();

        NetherObjectsPlaced.SMOKER_SPARSE
                .place(ctx, NetherObjects.PATCH_SMOKER)
                .vanillaNetherGround(1)
                .onceEvery(4)
                .register();

        NetherObjectsPlaced.WART_DEADWOOD
                .inlineConfiguration(ctx)
                .templates()
                .add(BetterNether.C.id("trees/wart_root_01"), -0, 1.0f)
                .add(BetterNether.C.id("trees/wart_root_02"), -0, 1.0f)
                .add(BetterNether.C.id("trees/wart_root_03"), -1, 1.0f)
                .add(BetterNether.C.id("trees/wart_fallen_log"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(8)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherObjectsPlaced.SCULK_HIDDEN
                .inlineConfiguration(ctx)
                .randomBlock()
                .add(Blocks.SCULK_CATALYST, 30)
                .add(Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 10)
                .inlinePlace()
                .vanillaNetherGround(8)
                .onceEvery(4)
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .offset(Direction.DOWN)
                .register();

        NetherObjectsPlaced.SCULK_TOP
                .inlineConfiguration(ctx)
                .simple()
                .block(Blocks.SCULK_SENSOR)
                .inlinePlace()
                .vanillaNetherGround(8)
                .onceEvery(5)
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .register();

        NetherObjectsPlaced.FOREST_LITTER
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("upside_down_forest/tree_fallen"), 1, 1.0f)
                .add(BN.id("upside_down_forest/tree_needle"), 1, 1.0f)
                .add(BN.id("upside_down_forest/tree_root"), -1, 1.0f)
                .add(BN.id("upside_down_forest/tree_stump"), -1, 1.0f)
                .add(BN.id("upside_down_forest/tree_small_branch"), 1, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(4)
                .onceEvery(2)
                .register();

    }
}
