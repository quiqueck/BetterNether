package org.betterx.betternether.registry.features.configured;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.*;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.configurators.*;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.world.level.block.Blocks;

public class NetherVegetation {
    private static final ModCore C = BetterNether.C;
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_NETHERRACK_MOSS =
            FeatureContentManager.bonemeal(C.id("bonemeal_netherrack_moss"));
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_NETHER_MYCELIUM =
            FeatureContentManager.bonemeal(C.id("bonemeal_nether_mycelium"));
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_JUNGLE_GRASS =
            FeatureContentManager.bonemeal(C.id("bonemeal_jungle_grass"));
    public static final FeatureKey<NetherForrestVegetation> BONEMEAL_MUSHROOM_GRASS =
            FeatureContentManager.bonemealNetherForrest(C.id("bonemeal_mushroom_grass"));
    public static final FeatureKey<NetherForrestVegetation> BONEMEAL_SEPIA_MUSHROOM_GRASS =
            FeatureContentManager.bonemealNetherForrest(C.id("bonemeal_sepia_mushroom_grass"));
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_SWAMPLAND_GRASS =
            FeatureContentManager.bonemeal(C.id("bonemeal_swampland_grass"));
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_CEILING_MUSHROOMS =
            FeatureContentManager.bonemeal(C.id("bonemeal_ceiling_mushrooms"));
    public static final FeatureKey<AsMultiPlaceRandomSelect> VEGETATION_MUSHROOM_FORREST =
            FeatureContentManager.multiPlaceRandomFeature(C.id("vegetation_mushroom_forrest"));
    public static final FeatureKey<WithFeature<JellyfishMushroomFeature>> JELLYFISH_MUSHROOM =
            FeatureContentManager.withFeature(C.id("jellyfish_mushroom"), NetherFeatures.JELLYFISH_MUSHROOM);
    public static final FeatureKey<RandomPatch> PATCH_JELLYFISH_MUSHROOM =
            FeatureContentManager.randomPatch(C.id("patch_jellyfish_mushroom"));
    public static final FeatureKey<ForSimpleBlock> PATCH_BLACK_BUSH =
            FeatureContentManager.simple(C.id("patch_back_bush"));
    public static final FeatureKey<WithFeature<LucisFeature>> WALL_LUCIS =
            FeatureContentManager.withFeature(C.id("patch_lucis"), NetherFeatures.LUCIS);
    public static final FeatureKey<WeightedBlockPatch> BONEMEAL_SOUL_SOIL =
            FeatureContentManager.bonemeal(C.id("bonemeal_soul_soil"));
    /**
     * Gloomwood ground cover, one feature per floor material: the pale gloomgrass grows on the bleached
     * gloomsculk, the dark one on the vanilla sculk patches in it. Two features rather than one weighted
     * pick because the choice is made by the ground, and a block provider cannot see the ground - only a
     * placement filter can, and there is one filter per feature.
     * <p>
     * A random block patch rather than {@code netherForrestVegetation()}: that wraps vanilla's
     * nether-forest-vegetation feature, which refuses to place unless the block below is
     * {@code #minecraft:nylium}, and the gloomwood floor is sculk. Same reason
     * {@code VEGETATION_MAGMA_LAND} below is built this way.
     */
    public static final FeatureKey<ForSimpleBlock> VEGETATION_GLOOMWOOD_BLEACHED =
            FeatureContentManager.simple(C.id("vegetation_gloomwood_bleached"));
    /**
     * @see #VEGETATION_GLOOMWOOD_BLEACHED
     */
    public static final FeatureKey<ForSimpleBlock> VEGETATION_GLOOMWOOD_SCULK =
            FeatureContentManager.simple(C.id("vegetation_gloomwood_sculk"));
    public static final FeatureKey<WeightedBlock> VEGETATION_MAGMA_LAND =
            FeatureContentManager.randomBlock(C.id("vegetation_magma_land"));
    /**
     * A lone wisp head, for the shortest of the four heights.
     * <p>
     * Its own column because the block-column feature draws each layer independently: asking one column
     * for 0-2 stalk segments and 0-1 bright ones would produce a dark segment directly under the head a
     * third of the time, which is the join the two stalk textures exist to avoid.
     */
    public static final FeatureKey<AsBlockColumn> GLOOMWISP_VINE_HEAD =
            FeatureContentManager.blockColumn(C.id("patch_gloomwisp_vine_head"));
    public static final FeatureKey<AsBlockColumn> GLOOMWISP_VINE =
            FeatureContentManager.blockColumn(C.id("patch_gloomwisp_vine"));
    public static final FeatureKey<WeightedBlock> VEGETATION_GRASSLANDS =
            FeatureContentManager.randomBlock(C.id("vegetation_nether_grasslands"));
    public static final FeatureKey<WeightedBlock> VEGETATION_GRAVEL_DESERT =
            FeatureContentManager.randomBlock(C.id("vegetation_nether_gravel_desert"));
    public static final FeatureKey<WeightedBlock> VEGETATION_JUNGLE =
            FeatureContentManager.randomBlock(C.id("vegetation_jungle"));
    public static final FeatureKey<AsMultiPlaceRandomSelect> VEGETATION_POOR_GRASSLANDS =
            FeatureContentManager.multiPlaceRandomFeature(C.id("vegetation_nether_poor_grasslands"));
    public static final FeatureKey<WeightedBlock> VEGETATION_SOUL_PLAIN =
            FeatureContentManager.randomBlock(C.id("vegetation_soul_plain"));
    public static final FeatureKey<WeightedBlock> VEGETATION_WART_FOREST =
            FeatureContentManager.randomBlock(C.id("vegetation_wart_forest"));
    public static final FeatureKey<WeightedBlock> VEGETATION_WART_FOREST_EDGE =
            FeatureContentManager.randomBlock(C.id("vegetation_wart_forest_edge"));
    public static final FeatureKey<WeightedBlock> VEGETATION_SWAMPLAND =
            FeatureContentManager.randomBlock(C.id("vegetation_nether_swampland"));
    public static final FeatureKey<WeightedBlock> VEGETATION_OLD_SWAMPLAND =
            FeatureContentManager.randomBlock(C.id("vegetation_old_swampland"));
    public static final FeatureKey<WeightedBlock> VEGETATION_OLD_WARPED_WOODS =
            FeatureContentManager.randomBlock(C.id("vegetation_old_warped_woods"));
    public static final FeatureKey<AsBlockColumn> NETHER_CACTUS =
            FeatureContentManager.blockColumn(C.id("patch_nether_cactus"));
    public static final FeatureKey<FacingBlock> WALL_MUSHROOM_RED_WITH_MOSS =
            FeatureContentManager.facingBlock(C.id("patch_wall_mushroom_red_with_moss"));
    public static final FeatureKey<FacingBlock> WALL_MUSHROOMS_WITH_MOSS =
            FeatureContentManager.facingBlock(C.id("patch_wall_mushrooms_with_moss"));
    public static final FeatureKey<FacingBlock> WALL_MUSHROOMS =
            FeatureContentManager.facingBlock(C.id("patch_wall_mushrooms"));
    public static final FeatureKey<FacingBlock> WALL_JUNGLE =
            FeatureContentManager.facingBlock(C.id("patch_wall_jungle"));
    public static final FeatureKey<FacingBlock> WALL_UPSIDE_DOWN =
            FeatureContentManager.facingBlock(C.id("patch_upside_down"));
    public static final FeatureKey<AsBlockColumn> NETHER_REED =
            FeatureContentManager.blockColumn(C.id("patch_nether_reed"));
    public static final FeatureKey<WithFeature<WartBushFeature>> WART_BUSH =
            FeatureContentManager.withFeature(C.id("patch_wart_bush"), NetherFeatures.WART_BUSH);
    public static final FeatureKey<WithFeature<WillowBushFeature>> WILLOW_BUSH =
            FeatureContentManager.withFeature(C.id("patch_willow_bush"), NetherFeatures.WILLOW_BUSH);
    public static final FeatureKey<WithFeature<RubeusBushFeature>> RUBEUS_BUSH =
            FeatureContentManager.withFeature(C.id("patch_rubeus_bush"), NetherFeatures.RUBEUS_BUSH);
    public static final FeatureKey<WithFeature<NetherSakuraBushFeature>> SAKURA_BUSH =
            FeatureContentManager.withFeature(C.id("patch_sakura_bush"), NetherFeatures.SAKURA_BUSH);
    public static final FeatureKey<WeightedBlock> SCULK_VEGETATION =
            FeatureContentManager.randomBlock(C.id("sculk_vegetation"));
    public static final FeatureKey<ForSimpleBlock> HOOK_MUSHROOM =
            FeatureContentManager.simple(C.id("patch_hook_mushroom"));
    public static final FeatureKey<ForSimpleBlock> MOSS_COVER =
            FeatureContentManager.simple(C.id("patch_moss_cover"));

    public static void setupBonemealFeatures() {
        NetherTerrainBlocks.NETHERRACK_MOSS.setVegetationFeature(() -> BONEMEAL_NETHERRACK_MOSS.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.NETHER_MYCELIUM.setVegetationFeature(() -> BONEMEAL_NETHER_MYCELIUM.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.JUNGLE_GRASS.setVegetationFeature(() -> BONEMEAL_JUNGLE_GRASS.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.MUSHROOM_GRASS.setVegetationFeature(() -> BONEMEAL_MUSHROOM_GRASS.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.SEPIA_MUSHROOM_GRASS.setVegetationFeature(() -> BONEMEAL_SEPIA_MUSHROOM_GRASS.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.SWAMPLAND_GRASS.setVegetationFeature(() -> BONEMEAL_SWAMPLAND_GRASS.getHolder(WorldState.registryAccess()));
        NetherTerrainBlocks.CEILING_MUSHROOMS.setVegetationFeature(() -> BONEMEAL_CEILING_MUSHROOMS.getHolder(WorldState.registryAccess()));

        BonemealAPI.INSTANCE.addSpreadableFeatures(Blocks.SOUL_SOIL, () -> BONEMEAL_SOUL_SOIL.getHolder(WorldState.registryAccess()));
        BonemealAPI.INSTANCE.addSpreadableFeatures(Blocks.SOUL_SAND, () -> BONEMEAL_SOUL_SOIL.getHolder(WorldState.registryAccess()));
    }
}
