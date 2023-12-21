package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.bclib.api.v3.levelgen.features.BCLFeatureBuilder;
import org.betterx.betternether.registry.features.configured.NetherTerrain;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ConfiguredTerrainFeatureDataProvider extends WoverRegistryContentProvider<ConfiguredFeature<?, ?>> {
    public ConfiguredTerrainFeatureDataProvider(
            ModCore modCore
    ) {
        super(modCore, "Configured Terrain Features", Registries.CONFIGURED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherTerrain.LAVA_PITS
                .bootstrap(ctx)
                .block(Blocks.LAVA)
                .register();
    }

    public static void bootstrapLegacy(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        BCLFeatureBuilder.registerUnbound(ctx);
    }
}
