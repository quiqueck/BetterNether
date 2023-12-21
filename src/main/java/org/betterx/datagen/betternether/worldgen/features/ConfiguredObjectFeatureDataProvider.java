package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherObjects;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ConfiguredObjectFeatureDataProvider extends WoverRegistryContentProvider<ConfiguredFeature<?, ?>> {
    public ConfiguredObjectFeatureDataProvider(
            ModCore modCore
    ) {
        super(modCore, "Configured Object Features", Registries.CONFIGURED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherObjects.PATCH_BASALT_STALACTITE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(NetherBlocks.BASALT_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(4, 11))
                .inlinePlace()
                .isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
                .inRandomPatch()
                .register();

        NetherObjects.PATCH_BASALT_STALAGMITE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.UP)
                .blockState(NetherBlocks.BASALT_STALACTITE)
                .maxHeight(BiasedToBottomInt.of(3, 9))
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
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
}
