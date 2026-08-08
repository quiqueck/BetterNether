package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.TwistedVinesFeature;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.configurators.AsBlockColumn;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;
import de.ambertation.wover.feature.api.configured.configurators.RandomPatch;
import de.ambertation.wover.feature.api.configured.configurators.WithFeature;

public class NetherVines {
    public static final ModCore C = BetterNether.C;

    public static final FeatureKey<AsBlockColumn> LUMABUS_VINE =
            FeatureContentManager.blockColumn(C.id("lumabus_vine"));
    public static final FeatureKey<AsBlockColumn> GOLDEN_LUMABUS_VINE =
            FeatureContentManager.blockColumn(C.id("golden_lumabus_vine"));
    public static final FeatureKey<AsBlockColumn> EYE_VINE =
            FeatureContentManager.blockColumn(C.id("eye_vine"));
    public static final FeatureKey<RandomPatch> PATCH_LUMABUS_VINE =
            FeatureContentManager.randomPatch(C.id("patch_lumabus_vine"));
    /**
     * The lumabus patch for the gloomwood: identical to {@link #PATCH_LUMABUS_VINE} except that the
     * column it scatters may also hang from a sculk ceiling. The ordinary one asks for nether ground,
     * which in the gloomwood is only the netherrack islands breaking up the sculk - it would have grown
     * on those and nowhere else.
     */
    public static final FeatureKey<RandomPatch> PATCH_LUMABUS_VINE_GLOOM =
            FeatureContentManager.randomPatch(C.id("patch_lumabus_vine_gloom"));
    public static final FeatureKey<RandomPatch> PATCH_GOLDEN_LUMABUS_VINE =
            FeatureContentManager.randomPatch(C.id("patch_golden_lumabus_vine"));
    public static final FeatureKey<AsBlockColumn> PATCH_GOLDEN_VINE =
            FeatureContentManager.blockColumn(C.id("patch_golden_vine"));
    public static final FeatureKey<AsBlockColumn> PATCH_GOLDEN_VINE_SPARSE =
            FeatureContentManager.blockColumn(C.id("patch_golden_vine_sparse"));
    public static final FeatureKey<RandomPatch> PATCH_EYE_VINE =
            FeatureContentManager.randomPatch(C.id("patch_eye_vine"));
    public static final FeatureKey<AsPillar> PATCH_GLOOMSCULK_VINE =
            FeatureContentManager.pillar(C.id("patch_gloomsculk_vine"));
    public static final FeatureKey<AsPillar> PATCH_BLACK_VINE =
            FeatureContentManager.pillar(C.id("patch_black_vine"));
    public static final FeatureKey<WithFeature<TwistedVinesFeature>> PATCH_TWISTING_VINES =
            FeatureContentManager.withFeature(C.id("patch_twisting_vine"), NetherFeatures.TWISTING_VINES);
    public static final FeatureKey<AsPillar> PATCH_BLOOMING_VINE =
            FeatureContentManager.pillar(C.id("patch_blooming_vine"));
    public static final FeatureKey<AsBlockColumn> NEON_EQUISETUM =
            FeatureContentManager.blockColumn(C.id("neon_equisetum"));
    public static final FeatureKey<RandomPatch> PATCH_NEON_EQUISETUM =
            FeatureContentManager.randomPatch(C.id("patch_neon_equisetum"));
    public static final FeatureKey<AsBlockColumn> WHISPERING_GOURD_VINE =
            FeatureContentManager.blockColumn(C.id("whispering_gourd_vine"));
    public static final FeatureKey<RandomPatch> PATCH_WHISPERING_GOURD_VINE =
            FeatureContentManager.randomPatch(C.id("patch_whispering_gourd_vine"));
}
