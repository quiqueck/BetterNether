package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.bclib.api.v3.levelgen.features.blockpredicates.BlockPredicates;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ConfiguredTreeFeatureDataProvider extends WoverRegistryContentProvider<ConfiguredFeature<?, ?>> {
    public ConfiguredTreeFeatureDataProvider(
            ModCore modCore
    ) {
        super(modCore, "Configured Tree Features", Registries.CONFIGURED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherTrees.RUBEUS_TREE
                .bootstrap(ctx)
                .configuration(NaturalTreeConfiguration.natural())
                .register();

        NetherTrees.MUSHROOM_FIR.bootstrap(ctx).register();

        NetherTrees.STALAGNATE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.TRIPLE_SHAPE_FILL)
                .direction(Direction.UP)
                .blockState(NetherBlocks.MAT_STALAGNATE.getTrunk())
                .minHeight(3)
                .maxHeight(64)
                .register();

        NetherTrees.STALAGNATE_DOWN
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.TRIPLE_SHAPE_FILL)
                .direction(Direction.DOWN)
                .blockState(NetherBlocks.MAT_STALAGNATE.getTrunk())
                .minHeight(3)
                .maxHeight(64)
                .register();

        NetherTrees.GIANT_MOLD
                .bootstrap(ctx)
                .direction(Direction.UP)
                .addTripleShape(NetherBlocks.GIANT_MOLD.defaultBlockState(), ClampedNormalInt.of(5, 1.3f, 3, 8))
                .register();

        NetherTrees.PATCH_BIG_RED_MUSHROOM
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .addTripleShape(NetherBlocks.RED_LARGE_MUSHROOM.defaultBlockState(), ClampedNormalInt.of(6, 2.1f, 3, 9))
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .register();

        NetherTrees.PATCH_BIG_BROWN_MUSHROOM
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .tries(30)
                .spreadXZ(7)
                .register();

        NetherTrees.WART_TREE
                .bootstrap(ctx)
                .configuration(NaturalTreeConfiguration.natural())
                .register();

        NetherTrees.WILLOW_TREE.bootstrap(ctx).register();
        NetherTrees.ANCHOR_TREE_BRANCH.bootstrap(ctx).register();
        NetherTrees.ANCHOR_TREE.bootstrap(ctx).register();
        NetherTrees.ANCHOR_TREE_ROOT.bootstrap(ctx).register();
        NetherTrees.SAKURA_TREE.bootstrap(ctx).register();
    }
}
