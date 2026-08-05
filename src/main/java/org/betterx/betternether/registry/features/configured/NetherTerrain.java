package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.ForSimpleBlock;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class NetherTerrain {
    private static final ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<ForSimpleBlock> LAVA_PITS = ConfiguredFeatureManager.simple(C.id("lava_pit"));

    /**
     * Vanilla sculk vein, grown over the gloomwood's sculk in patches.
     * <p>
     * Vanilla's own {@code multiface_growth} rather than anything of ours: sculk vein is a multiface block,
     * and this feature is the only thing that knows how to pick a face, build the right state for it and
     * then let the block's own spreader carry the patch around corners. A simple block placement would put
     * down default-state veins that attach to nothing.
     */
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<MultifaceGrowthConfiguration>, MultifaceGrowthConfiguration>> SCULK_VEIN
            = ConfiguredFeatureManager.configuration(C.id("sculk_vein"), Feature.MULTIFACE_GROWTH);
}
