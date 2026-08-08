package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.configurators.AsBlockColumn;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;

public class NetherObjects {
    private static final ModCore C = BetterNether.C;
    public static final FeatureKey<AsPillar> PATCH_BASALT_STALACTITE
            = FeatureContentManager.pillar(C.id("patch_basalt_stalactite"));
    public static final FeatureKey<AsPillar> PATCH_BASALT_STALAGMITE
            = FeatureContentManager.pillar(C.id("patch_basalt_stalagmite"));
    public static final FeatureKey<AsBlockColumn> PATCH_SMOKER
            = FeatureContentManager.blockColumn(C.id("patch_smoker"));
}
