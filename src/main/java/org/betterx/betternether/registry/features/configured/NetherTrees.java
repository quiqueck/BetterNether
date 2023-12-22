package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.*;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NetherTrees {
    private static ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<WithConfiguration<RubeusTreeFeature, NaturalTreeConfiguration>> RUBEUS_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_rubeus"), NetherFeatures.RUBEUS_TREE);

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
            = ConfiguredFeatureManager.configuration(
            C.id("patch_big_brown_mushroom"),
            NetherFeatures.BIG_BROWN_MUSHROOM
    );

    public static final ConfiguredFeatureKey<WithConfiguration<WartTreeFeature, NaturalTreeConfiguration>> WART_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_wart"), NetherFeatures.WART_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<WillowTreeFeature, NoneFeatureConfiguration>> WILLOW_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_willow"), NetherFeatures.WILLOW_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeBranchFeature, NoneFeatureConfiguration>> ANCHOR_TREE_BRANCH
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree_branch"), NetherFeatures.ANCHOR_TREE_BRANCH);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeFeature, NoneFeatureConfiguration>> ANCHOR_TREE
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree"), NetherFeatures.ANCHOR_TREE);

    public static final ConfiguredFeatureKey<WithConfiguration<AnchorTreeRootFeature, NoneFeatureConfiguration>> ANCHOR_TREE_ROOT
            = ConfiguredFeatureManager.configuration(C.id("anchor_tree_root"), NetherFeatures.ANCHOR_TREE_ROOT);

    public static final ConfiguredFeatureKey<WithConfiguration<NetherSakuraFeature, NoneFeatureConfiguration>> SAKURA_TREE
            = ConfiguredFeatureManager.configuration(C.id("tree_sakura"), NetherFeatures.SAKURA_TREE);
}
