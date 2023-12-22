package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherOres;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class NetherOresPlaced {
    private static final ModCore C = BetterNether.C;
    public static final PlacedConfiguredFeatureKey CINCINNASITE_ORE = PlacedFeatureManager
            .createKey(NetherOres.CINCINNASITE_ORE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_RUBY_ORE = PlacedFeatureManager
            .createKey(NetherOres.NETHER_RUBY_ORE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_RUBY_ORE_SOUL = PlacedFeatureManager
            .createKey(NetherOres.NETHER_RUBY_ORE_SOUL)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_RUBY_ORE_LARGE = PlacedFeatureManager
            .createKey(NetherOres.NETHER_RUBY_ORE_LARGE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_RUBY_ORE_RARE = PlacedFeatureManager
            .createKey(NetherOres.NETHER_RUBY_ORE_RARE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_LAPIS_ORE = PlacedFeatureManager
            .createKey(NetherOres.NETHER_LAPIS_ORE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    public static final PlacedConfiguredFeatureKey NETHER_REDSTONE_ORE = PlacedFeatureManager
            .createKey(NetherOres.NETHER_REDSTONE_ORE)
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);


}
