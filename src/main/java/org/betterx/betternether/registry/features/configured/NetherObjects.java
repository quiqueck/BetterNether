package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.AsBlockColumn;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;

public class NetherObjects {
    private static final ModCore C = BetterNether.C;
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BASALT_STALACTITE
            = ConfiguredFeatureManager.pillar(C.id("patch_basalt_stalactite"));
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BASALT_STALAGMITE
            = ConfiguredFeatureManager.pillar(C.id("patch_basalt_stalagmite"));
    public static final ConfiguredFeatureKey<AsBlockColumn> PATCH_SMOKER
            = ConfiguredFeatureManager.blockColumn(C.id("patch_smoker"));
}
