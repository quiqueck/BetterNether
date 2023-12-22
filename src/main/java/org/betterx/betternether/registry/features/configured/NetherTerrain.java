package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.ForSimpleBlock;

public class NetherTerrain {
    private static final ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<ForSimpleBlock> LAVA_PITS = ConfiguredFeatureManager.simple(C.id("lava_pit"));
}
