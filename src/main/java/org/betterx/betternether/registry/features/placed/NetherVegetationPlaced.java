package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION;

public class NetherVegetationPlaced {
    private static final ModCore C = BetterNether.C;

    public static final PlacedConfiguredFeatureKey VEGETATION_MUSHROOM_FORREST_EDGE = PlacedFeatureManager
            .createKey(C.id("vegetation_mushroom_forrest_edge"), NetherVegetation.VEGETATION_MUSHROOM_FORREST)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey JELLYFISH_MUSHROOM = PlacedFeatureManager
            .createKey(NetherVegetation.JELLYFISH_MUSHROOM)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey PATCH_JELLYFISH_MUSHROOM = PlacedFeatureManager
            .createKey(NetherVegetation.PATCH_JELLYFISH_MUSHROOM)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey JELLYFISH_MUSHROOM_DENSE = PlacedFeatureManager
            .createKey(C.id("patch_jellyfish_mushroom_dense"), NetherVegetation.PATCH_JELLYFISH_MUSHROOM)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey BLACK_BUSH = PlacedFeatureManager
            .createKey(NetherVegetation.PATCH_BLACK_BUSH)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey BLACK_BUSH_SPARSE = PlacedFeatureManager
            .createKey(C.id("patch_black_bush_sparse"), NetherVegetation.PATCH_BLACK_BUSH)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedConfiguredFeatureKey WALL_LUCIS = PlacedFeatureManager
            .createKey(C.id("patch_lucis"), NetherVegetation.WALL_LUCIS)
            .setDecoration(VEGETAL_DECORATION);


    public static final PlacedFeatureKey VEGETATION_BONE_REEF = PlacedFeatureManager
            .createKey(C.id("vegetation_bone_reef"))
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedFeatureKey VEGETATION_SULFURIC_BONE_REEF = PlacedFeatureManager
            .createKey(C.id("vegetation_sulfuric_bone_reef"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_MAGMA_LAND = PlacedFeatureManager
            .createKey(C.id("vegetation_magma_land"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_CRIMSON_GLOWING_WOODS = PlacedFeatureManager
            .createKey(C.id("vegetation_crimson_glowing_woods"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_GRASSLANDS = PlacedFeatureManager
            .createKey(C.id("vegetation_nether_grasslands"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_GRAVEL_DESERT = PlacedFeatureManager
            .createKey(C.id("vegetation_nether_gravel_desert"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_JUNGLE = PlacedFeatureManager
            .createKey(C.id("vegetation_nether_jungle"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedConfiguredFeatureKey VEGETATION_MUSHROOM_FORREST = PlacedFeatureManager
            .createKey(NetherVegetation.VEGETATION_MUSHROOM_FORREST)
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedFeatureKey VEGETATION_POOR_GRASSLANDS = PlacedFeatureManager
            .createKey(C.id("vegetation_nether_poor_grassland"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_SOUL_PLAIN = PlacedFeatureManager
            .createKey(C.id("vegetation_soul_plain"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_WART_FOREST = PlacedFeatureManager
            .createKey(C.id("vegetation_wart_forest"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_WART_FOREST_EDGE = PlacedFeatureManager
            .createKey(C.id("vegetation_wart_forest_edge"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_SWAMPLAND = PlacedFeatureManager
            .createKey(C.id("vegetation_nether_swampland"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_OLD_SWAMPLAND = PlacedFeatureManager
            .createKey(C.id("vegetation_old_swampland"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey VEGETATION_OLD_WARPED_WOODS = PlacedFeatureManager
            .createKey(C.id("vegetation_old_warped_woods"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey NETHER_CACTUS = PlacedFeatureManager
            .createKey(C.id("patch_nether_cactus"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WALL_MUSHROOM_RED_WITH_MOSS = PlacedFeatureManager
            .createKey(C.id("patch_wall_mushroom_red_with_moss"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WALL_MUSHROOMS_WITH_MOSS = PlacedFeatureManager
            .createKey(C.id("patch_wall_mushrooms_with_moss"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WALL_MUSHROOMS = PlacedFeatureManager
            .createKey(C.id("patch_wall_mushrooms"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WALL_JUNGLE = PlacedFeatureManager
            .createKey(C.id("patch_wall_jungle"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WALL_UPSIDE_DOWN = PlacedFeatureManager
            .createKey(C.id("patch_upside_down"))
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedFeatureKey NETHER_REED = PlacedFeatureManager
            .createKey(C.id("patch_nether_reed"))
            .setDecoration(VEGETAL_DECORATION);

    public static final PlacedFeatureKey WART_BUSH = PlacedFeatureManager
            .createKey(C.id("patch_wart_bush"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WILLOW_BUSH = PlacedFeatureManager
            .createKey(C.id("patch_willow_bush"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey RUBEUS_BUSH = PlacedFeatureManager
            .createKey(C.id("patch_rubeus_bush"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey SAKURA_BUSH = PlacedFeatureManager
            .createKey(C.id("patch_sakura_bush"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey WART_CAP = PlacedFeatureManager
            .createKey(C.id("wart_cap"));

    public static final PlacedFeatureKey SCULK_VEGETATION = PlacedFeatureManager
            .createKey(C.id("sculk_vegetation"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey HOOK_MUSHROOM = PlacedFeatureManager
            .createKey(C.id("patch_hook_mushroom"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey MOSS_COVER = PlacedFeatureManager
            .createKey(C.id("patch_moss_cover"))
            .setDecoration(VEGETAL_DECORATION);
}
