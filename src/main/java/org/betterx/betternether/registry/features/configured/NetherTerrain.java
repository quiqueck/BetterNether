package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.configurators.ForSimpleBlock;
import de.ambertation.wover.feature.api.configured.configurators.WithFeature;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.levelgen.feature.MultifaceGrowthFeature;

public class NetherTerrain {
    private static final ModCore C = BetterNether.C;

    public static final FeatureKey<ForSimpleBlock> LAVA_PITS = FeatureContentManager.simple(C.id("lava_pit"));

    /**
     * Vanilla sculk vein, grown over the gloomwood's sculk in patches.
     * <p>
     * Vanilla's own {@code multiface_growth} rather than anything of ours: sculk vein is a multiface block,
     * and this feature is the only thing that knows how to pick a face, build the right state for it and
     * then let the block's own spreader carry the patch around corners. A simple block placement would put
     * down default-state veins that attach to nothing.
     * <p>
     * 26.3 folded {@code MultifaceGrowthConfiguration} into the feature itself, so the settings live here
     * rather than in the datagen provider - except the anchor tag, which needs a registry lookup. The
     * placeholder below carries an empty anchor and is replaced wholesale by
     * {@code TerrainFeatureDataProvider}, which has the {@code BootstrapContext} to resolve
     * {@code SCULK_LIKE}; nothing reads this instance at runtime, because the feature the game loads comes
     * from the generated json.
     */
    public static final FeatureKey<WithFeature<MultifaceGrowthFeature>> SCULK_VEIN =
            FeatureContentManager.withFeature(C.id("sculk_vein"), sculkVein(HolderSet.empty()));

    /**
     * The sculk vein feature, anchored to {@code canBePlacedOn}.
     *
     * @param canBePlacedOn the blocks a vein may attach itself to
     * @return the configured vanilla multiface growth feature
     */
    public static MultifaceGrowthFeature sculkVein(HolderSet<net.minecraft.world.level.block.Block> canBePlacedOn) {
        return new MultifaceGrowthFeature(
                (MultifaceSpreadeableBlock) Blocks.SCULK_VEIN,
                // Half vanilla's 20. The gloomwood is not the deep dark: the veins are meant to
                // read as patches on the sculk, not as a continuous skin over the whole cavern.
                10,
                true,
                true,
                true,
                // The spread is what turns a single attached face into a patch, and it is the
                // block's own spreader doing it, so it follows the surface it is on.
                0.75F,
                canBePlacedOn
        );
    }
}
