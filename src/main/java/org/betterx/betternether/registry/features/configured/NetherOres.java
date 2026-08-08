package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.configurators.AsOre;

public class NetherOres {
    private static final ModCore C = BetterNether.C;
    public static final FeatureKey<AsOre> CINCINNASITE_ORE =
            FeatureContentManager.ore(C.id("cincinnasite_ore"));
    /**
     * Ancient debris worked into the gloomwood's sculk ceiling.
     * <p>
     * Vanilla's own debris features target netherrack in the terrain volume, so they never touch a
     * ceiling made of sculk. This one targets both, and is placed from the ceiling down rather than by
     * absolute height.
     */
    public static final FeatureKey<AsOre> GLOOMWOOD_CEILING_DEBRIS =
            FeatureContentManager.ore(C.id("gloomwood_ceiling_debris"));
    public static final FeatureKey<AsOre> NETHER_RUBY_ORE =
            FeatureContentManager.ore(C.id("nether_ruby_ore"));
    public static final FeatureKey<AsOre> NETHER_RUBY_ORE_SOUL =
            FeatureContentManager.ore(C.id("nether_ruby_soul_ore"));
    public static final FeatureKey<AsOre> NETHER_RUBY_ORE_LARGE =
            FeatureContentManager.ore(C.id("nether_ruby_large_ore"));
    public static final FeatureKey<AsOre> NETHER_RUBY_ORE_RARE =
            FeatureContentManager.ore(C.id("nether_ruby_rare_ore"));
    public static final FeatureKey<AsOre> NETHER_LAPIS_ORE =
            FeatureContentManager.ore(C.id("nether_lapis_ore"));
    public static final FeatureKey<AsOre> NETHER_REDSTONE_ORE =
            FeatureContentManager.ore(C.id("nether_redstone_ore"));
}
