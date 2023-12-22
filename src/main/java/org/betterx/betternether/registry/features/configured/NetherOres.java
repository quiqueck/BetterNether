package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsOre;

public class NetherOres {
    private static final ModCore C = BetterNether.C;
    public static final ConfiguredFeatureKey<AsOre> CINCINNASITE_ORE =
            ConfiguredFeatureManager.ore(C.id("cincinnasite_ore"));
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
