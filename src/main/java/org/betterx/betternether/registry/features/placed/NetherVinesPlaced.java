package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherVines;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class NetherVinesPlaced {
    public static final ModCore C = BetterNether.C;

    public static final PlacedConfiguredFeatureKey PLACED_LUMABUS_VINE = PlacedFeatureManager.createKey(NetherVines.LUMABUS_VINE);
    public static final PlacedConfiguredFeatureKey PLACED_GOLDEN_LUMABUS_VINE = PlacedFeatureManager.createKey(NetherVines.GOLDEN_LUMABUS_VINE);
    public static final PlacedConfiguredFeatureKey PLACED_EYE_VINE = PlacedFeatureManager.createKey(NetherVines.EYE_VINE);
    public static final PlacedConfiguredFeatureKey PLACED_NEON_EQUISETUM = PlacedFeatureManager.createKey(NetherVines.NEON_EQUISETUM);
    public static final PlacedConfiguredFeatureKey PLACED_WHISPERING_GOURD_VINE = PlacedFeatureManager.createKey(NetherVines.WHISPERING_GOURD_VINE);
    public static final PlacedConfiguredFeatureKey LUMABUS_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_LUMABUS_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey GOLDEN_LUMABUS_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_LUMABUS_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey GOLDEN_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey GOLDEN_VINE_SPARSE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_VINE_SPARSE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey EYE_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_EYE_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey BLACK_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_BLACK_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey BLOOMING_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_BLOOMING_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey TWISTING_VINES = PlacedFeatureManager
            .createKey(NetherVines.PATCH_TWISTING_VINES)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey NEON_EQUISETUM = PlacedFeatureManager
            .createKey(NetherVines.PATCH_NEON_EQUISETUM)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey PATCH_WHISPERING_GOURD_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_WHISPERING_GOURD_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
}
