package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.TwistedVinesFeature;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NetherVines {
    public static final ModCore C = BetterNether.C;

    public static final ConfiguredFeatureKey<AsBlockColumn> LUMABUS_VINE =
            ConfiguredFeatureManager.blockColumn(C.id("lumabus_vine"));
    public static final ConfiguredFeatureKey<AsBlockColumn> GOLDEN_LUMABUS_VINE =
            ConfiguredFeatureManager.blockColumn(C.id("golden_lumabus_vine"));
    public static final ConfiguredFeatureKey<AsBlockColumn> EYE_VINE =
            ConfiguredFeatureManager.blockColumn(C.id("eye_vine"));
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_LUMABUS_VINE =
            ConfiguredFeatureManager.randomPatch(C.id("patch_lumabus_vine"));
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_GOLDEN_LUMABUS_VINE =
            ConfiguredFeatureManager.randomPatch(C.id("patch_golden_lumabus_vine"));
    public static final ConfiguredFeatureKey<AsBlockColumn> PATCH_GOLDEN_VINE =
            ConfiguredFeatureManager.blockColumn(C.id("patch_golden_vine"));
    public static final ConfiguredFeatureKey<AsBlockColumn> PATCH_GOLDEN_VINE_SPARSE =
            ConfiguredFeatureManager.blockColumn(C.id("patch_golden_vine_sparse"));
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_EYE_VINE =
            ConfiguredFeatureManager.randomPatch(C.id("patch_eye_vine"));
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BLACK_VINE =
            ConfiguredFeatureManager.pillar(C.id("patch_black_vine"));
    public static final ConfiguredFeatureKey<WithConfiguration<TwistedVinesFeature, NoneFeatureConfiguration>> PATCH_TWISTING_VINES =
            ConfiguredFeatureManager.configuration(C.id("patch_twisting_vine"), NetherFeatures.TWISTING_VINES);
    public static final ConfiguredFeatureKey<AsPillar> PATCH_BLOOMING_VINE =
            ConfiguredFeatureManager.pillar(C.id("patch_blooming_vine"));
    public static final ConfiguredFeatureKey<AsBlockColumn> NEON_EQUISETUM =
            ConfiguredFeatureManager.blockColumn(C.id("neon_equisetum"));
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_NEON_EQUISETUM =
            ConfiguredFeatureManager.randomPatch(C.id("patch_neon_equisetum"));
    public static final ConfiguredFeatureKey<AsBlockColumn> WHISPERING_GOURD_VINE =
            ConfiguredFeatureManager.blockColumn(C.id("whispering_gourd_vine"));
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_WHISPERING_GOURD_VINE =
            ConfiguredFeatureManager.randomPatch(C.id("patch_whispering_gourd_vine"));
}
