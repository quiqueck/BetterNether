package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.ForSimpleBlock;

public class NetherTerrain {
    private static final ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<ForSimpleBlock> LAVA_PITS = ConfiguredFeatureManager.simple(C.id("lava_pit"));
}
