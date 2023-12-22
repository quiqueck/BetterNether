package org.betterx.betternether.registry.features.placed;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION;

public class NetherTreesPlaced {
    private static ModCore C = BetterNether.C;

    public static PlacedFeatureKey CRIMSON_GLOWING_TREE = PlacedFeatureManager
            .createKey(C.id("tree_crimson_glowing"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey CRIMSON_PINE = PlacedFeatureManager
            .createKey(C.id("tree_crimson_pine"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey RUBEUS_TREE = PlacedFeatureManager
            .createKey(NetherTrees.RUBEUS_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey MUSHROOM_FIR = PlacedFeatureManager
            .createKey(NetherTrees.MUSHROOM_FIR)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey STALAGNATE = PlacedFeatureManager
            .createKey(C.id("patch_stalagnate"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey GIANT_MOLD = PlacedFeatureManager
            .createKey(C.id("patch_giant_mold"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey BIG_RED_MUSHROOM = PlacedFeatureManager
            .createKey(NetherTrees.PATCH_BIG_RED_MUSHROOM)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey BIG_BROWN_MUSHROOM = PlacedFeatureManager
            .createKey(NetherTrees.PATCH_BIG_BROWN_MUSHROOM)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey OLD_RED_MUSHROOM = PlacedFeatureManager
            .createKey(C.id("old_red_mushroom"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey OLD_BROWN_MUSHROOM = PlacedFeatureManager
            .createKey(C.id("old_brown_mushroom"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey SOUL_LILY = PlacedFeatureManager
            .createKey(C.id("patch_soul_lily"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey WART_TREE = PlacedFeatureManager
            .createKey(NetherTrees.WART_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey WILLOW_TREE = PlacedFeatureManager
            .createKey(NetherTrees.WILLOW_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey OLD_WILLOW_TREE = PlacedFeatureManager
            .createKey(C.id("tree_old_willow"))
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey SAKURA_TREE = PlacedFeatureManager
            .createKey(NetherTrees.SAKURA_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey ANCHOR_TREE = PlacedFeatureManager
            .createKey(NetherTrees.ANCHOR_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey ANCHOR_TREE_SPARSE = PlacedFeatureManager
            .createKey(C.id("anchor_tree_sparse"), NetherTrees.ANCHOR_TREE)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey ANCHOR_TREE_BRANCH = PlacedFeatureManager
            .createKey(NetherTrees.ANCHOR_TREE_BRANCH)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedConfiguredFeatureKey ANCHOR_TREE_ROOT = PlacedFeatureManager
            .createKey(NetherTrees.ANCHOR_TREE_ROOT)
            .setDecoration(VEGETAL_DECORATION);

    public static PlacedFeatureKey BIG_WARPED_TREE = PlacedFeatureManager
            .createKey(C.id("big_warped_tree"))
            .setDecoration(VEGETAL_DECORATION);
}
