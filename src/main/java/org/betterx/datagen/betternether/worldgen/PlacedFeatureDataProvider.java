package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v3.levelgen.features.BCLPlacedFeatureBuilder;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatureDataProvider {
    public static void bootstrap(BootstapContext<PlacedFeature> ctx) {
        BCLPlacedFeatureBuilder.registerUnbound(ctx);
    }
}
