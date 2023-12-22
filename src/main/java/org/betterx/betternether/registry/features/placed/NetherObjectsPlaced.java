package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES;
import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION;

public class NetherObjectsPlaced {
    private static final ModCore C = BetterNether.C;
    public static final PlacedFeatureKey PATCH_TERRACOTTA_CLUMP = PlacedFeatureManager.createKey(C.id("terracotta_clump"));
    public static final PlacedFeatureKey BONES = PlacedFeatureManager
            .createKey(C.id("bones"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey JUNGLE_BONES = PlacedFeatureManager
            .createKey(C.id("jungle_bones"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey OBSIDIAN_CRYSTAL = PlacedFeatureManager
            .createKey(C.id("obsidian_crystal"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BONE_STALAGMITE = PlacedFeatureManager
            .createKey(C.id("patch_bone_stalagmite_on_ground"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey STALACTITE = PlacedFeatureManager
            .createKey(C.id("patch_stalactite"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey STALAGMITE = PlacedFeatureManager
            .createKey(C.id("patch_stalagmite"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BASALT_STALACTITE_2 = PlacedFeatureManager
            .createKey(C.id("patch_basalt_stalactite"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BASALT_STALACTITE = PlacedFeatureManager
            .createKey(C.id("patch_basalt_stalactite"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BASALT_STALACTITE_SPARSE = PlacedFeatureManager
            .createKey(C.id("patch_basalt_stalactite_sparse"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BASALT_STALAGMITE = PlacedFeatureManager
            .createKey(C.id("patch_basalt_stalagmite"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BASALT_STALAGMITE_SPARSE = PlacedFeatureManager
            .createKey(C.id("patch_basalt_stalagmite_sparse"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BLACKSTONE_STALACTITE = PlacedFeatureManager
            .createKey(C.id("patch_blackstone_stalactite_on_ceil"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey BLACKSTONE_STALAGMITE = PlacedFeatureManager
            .createKey(C.id("patch_blackstone_stalagmite_on_ground"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey SMOKER = PlacedFeatureManager
            .createKey(C.id("patch_smoker"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey SMOKER_SPARSE = PlacedFeatureManager
            .createKey(C.id("patch_smoker_sparse"))
            .setDecoration(SURFACE_STRUCTURES);
    public static final PlacedFeatureKey WART_DEADWOOD = PlacedFeatureManager
            .createKey(C.id("war_deadwood"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey SCULK_HIDDEN = PlacedFeatureManager
            .createKey(C.id("sculk_hidden"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey SCULK_TOP = PlacedFeatureManager
            .createKey(C.id("sculk_top"))
            .setDecoration(VEGETAL_DECORATION);
    public static final PlacedFeatureKey FOREST_LITTER = PlacedFeatureManager
            .createKey(C.id("forest_litter"))
            .setDecoration(SURFACE_STRUCTURES);
}
