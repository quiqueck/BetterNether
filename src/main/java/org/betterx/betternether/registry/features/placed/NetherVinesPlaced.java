package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherVines;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.placed.BoundPlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class NetherVinesPlaced {
    public static final ModCore C = BetterNether.C;

    public static final BoundPlacedFeatureKey PLACED_LUMABUS_VINE = PlacedFeatureManager.createKey(NetherVines.LUMABUS_VINE);
    public static final BoundPlacedFeatureKey PLACED_LUMABUS_VINE_GLOOM = PlacedFeatureManager
            .createKey(C.id("lumabus_vine_gloom"), NetherVines.LUMABUS_VINE);
    public static final BoundPlacedFeatureKey PLACED_GOLDEN_LUMABUS_VINE = PlacedFeatureManager.createKey(NetherVines.GOLDEN_LUMABUS_VINE);
    public static final BoundPlacedFeatureKey PLACED_EYE_VINE = PlacedFeatureManager.createKey(NetherVines.EYE_VINE);
    public static final BoundPlacedFeatureKey PLACED_NEON_EQUISETUM = PlacedFeatureManager.createKey(NetherVines.NEON_EQUISETUM);
    public static final BoundPlacedFeatureKey PLACED_WHISPERING_GOURD_VINE = PlacedFeatureManager.createKey(NetherVines.WHISPERING_GOURD_VINE);
    public static final BoundPlacedFeatureKey LUMABUS_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_LUMABUS_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey GOLDEN_LUMABUS_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_LUMABUS_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey GOLDEN_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey GOLDEN_VINE_SPARSE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GOLDEN_VINE_SPARSE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey EYE_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_EYE_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey GLOOMSCULK_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_GLOOMSCULK_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    /**
     * The lumabus at a fraction of its usual density, for the gloomwood ceiling. Its pale blue-green
     * glow is the one warm-neutral light in a biome otherwise lit only by the gloomwisps, and it hangs
     * from the same sculk-crusted rock they do - but it is a guest there, so it comes in well under the
     * gloomsculk vine it shares the ceiling with rather than alongside it.
     */
    public static final BoundPlacedFeatureKey LUMABUS_VINE_SPARSE = PlacedFeatureManager
            .createKey(C.id("patch_lumabus_vine_sparse"), NetherVines.PATCH_LUMABUS_VINE_GLOOM)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey BLACK_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_BLACK_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey BLOOMING_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_BLOOMING_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey TWISTING_VINES = PlacedFeatureManager
            .createKey(NetherVines.PATCH_TWISTING_VINES)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey NEON_EQUISETUM = PlacedFeatureManager
            .createKey(NetherVines.PATCH_NEON_EQUISETUM)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    public static final BoundPlacedFeatureKey PATCH_WHISPERING_GOURD_VINE = PlacedFeatureManager
            .createKey(NetherVines.PATCH_WHISPERING_GOURD_VINE)
            .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
}
