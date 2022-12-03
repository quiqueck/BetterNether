package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v3.levelgen.features.BCLFeatureBuilder;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ConfiguredFeatureDataProvider {
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        BCLFeatureBuilder.registerUnbound(ctx);
    }
}
