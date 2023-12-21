package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;

public class NetherObjects {
    private static final ModCore C = BetterNether.C;
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BASALT_STALACTITE
            = ConfiguredFeatureManager.pillar(C.id("patch_basalt_stalactite"));
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BASALT_STALAGMITE
            = ConfiguredFeatureManager.pillar(C.id("patch_basalt_stalagmite"));
    public static final ConfiguredFeatureKey<AsBlockColumn> PATCH_SMOKER
            = ConfiguredFeatureManager.blockColumn(C.id("patch_smoker"));
}
