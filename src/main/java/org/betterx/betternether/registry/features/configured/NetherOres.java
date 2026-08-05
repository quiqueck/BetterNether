package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.AsOre;

public class NetherOres {
    private static final ModCore C = BetterNether.C;
    public static final ConfiguredFeatureKey<AsOre> CINCINNASITE_ORE =
            ConfiguredFeatureManager.ore(C.id("cincinnasite_ore"));
    /**
     * Ancient debris worked into the gloomwood's sculk ceiling.
     * <p>
     * Vanilla's own debris features target netherrack in the terrain volume, so they never touch a
     * ceiling made of sculk. This one targets both, and is placed from the ceiling down rather than by
     * absolute height.
     */
    public static final ConfiguredFeatureKey<AsOre> GLOOMWOOD_CEILING_DEBRIS =
            ConfiguredFeatureManager.ore(C.id("gloomwood_ceiling_debris"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_RUBY_ORE =
            ConfiguredFeatureManager.ore(C.id("nether_ruby_ore"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_RUBY_ORE_SOUL =
            ConfiguredFeatureManager.ore(C.id("nether_ruby_soul_ore"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_RUBY_ORE_LARGE =
            ConfiguredFeatureManager.ore(C.id("nether_ruby_large_ore"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_RUBY_ORE_RARE =
            ConfiguredFeatureManager.ore(C.id("nether_ruby_rare_ore"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_LAPIS_ORE =
            ConfiguredFeatureManager.ore(C.id("nether_lapis_ore"));
    public static final ConfiguredFeatureKey<AsOre> NETHER_REDSTONE_ORE =
            ConfiguredFeatureManager.ore(C.id("nether_redstone_ore"));
}
