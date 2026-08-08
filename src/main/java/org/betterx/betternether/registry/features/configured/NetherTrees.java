package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.*;
import org.betterx.betternether.world.features.configs.GloomwoodTreeConfiguration;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.AsBlockColumn;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NetherTrees {
    private static final ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<WithConfiguration<RubeusTreeFeature, NaturalTreeConfiguration>> RUBEUS_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_rubeus"), NetherFeatures.RUBEUS_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<GloomwoodTreeFeature, GloomwoodTreeConfiguration>> GLOOMWOOD_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_gloomwood"), NetherFeatures.GLOOMWOOD_TREE);

    /**
     * The same tree with a chance of coming out bleached, used only by the solitary placement in the
     * gloomwood's open ground. A second configured feature rather than a flag on the placement, because
     * the palette is a property of the tree and the placement layer has no say over it.
     */
    public static final ConfiguredFeatureKey<WithConfiguration<GloomwoodTreeFeature, GloomwoodTreeConfiguration>> GLOOMWOOD_TREE_SOLITARY
            = ConfiguredFeatureManager.configuration(C.id("tree_gloomwood_solitary"), NetherFeatures.GLOOMWOOD_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<MushroomFirFeature, NoneFeatureConfiguration>> MUSHROOM_FIR
            = ConfiguredFeatureManager.configuration(C.id("tree_mushroom_fir"), NetherFeatures.MUSHROOM_FIR);

    public static final ConfiguredFeatureKey<AsPillar> STALAGNATE
            = ConfiguredFeatureManager.pillar(C.id("stalagnate"));

    public static final ConfiguredFeatureKey<AsPillar> STALAGNATE_DOWN
            = ConfiguredFeatureManager.pillar(C.id("stalagnate_down"));

    public static final ConfiguredFeatureKey<AsBlockColumn> GIANT_MOLD
            = ConfiguredFeatureManager.blockColumn(C.id("giant_mold"));

    public static final ConfiguredFeatureKey<AsBlockColumn> PATCH_BIG_RED_MUSHROOM
            = ConfiguredFeatureManager.blockColumn(C.id("patch_big_red_mushroom"));

    public static final ConfiguredFeatureKey<WithConfiguration<BigBrownMushroomFeature, NoneFeatureConfiguration>> PATCH_BIG_BROWN_MUSHROOM
            = ConfiguredFeatureManager.configuration(C.id("patch_big_brown_mushroom"), NetherFeatures.BIG_BROWN_MUSHROOM);

    public static final ConfiguredFeatureKey<WithConfiguration<WartTreeFeature, NaturalTreeConfiguration>> WART_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_wart"), NetherFeatures.WART_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<WillowTreeFeature, NoneFeatureConfiguration>> WILLOW_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_willow"), NetherFeatures.WILLOW_TREE);

    /**
     * The giant willow. Used both for the natural placement in the {@code OldSwampland} and as the 2x2
     * variant of the willow sapling, which is why it is a named configured feature rather than the inline
     * one the placement used to carry - the sapling needs something it can look up by key.
     */
    public static final ConfiguredFeatureKey<WithConfiguration<OldWillowTree, NaturalTreeConfiguration>> OLD_WILLOW_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_old_willow"), NetherFeatures.OLD_WILLOW_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeBranchFeature, NoneFeatureConfiguration>> ANCHOR_TREE_BRANCH
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree_branch"), NetherFeatures.ANCHOR_TREE_BRANCH);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeFeature, NoneFeatureConfiguration>> ANCHOR_TREE
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree"), NetherFeatures.ANCHOR_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeRootFeature, NoneFeatureConfiguration>> ANCHOR_TREE_ROOT
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree_root"), NetherFeatures.ANCHOR_TREE_ROOT);

    public static final ConfiguredFeatureKey<WithConfiguration<NetherSakuraFeature, NoneFeatureConfiguration>> SAKURA_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_sakura"), NetherFeatures.SAKURA_TREE);
}
