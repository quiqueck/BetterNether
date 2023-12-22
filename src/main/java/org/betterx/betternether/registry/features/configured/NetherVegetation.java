package org.betterx.betternether.registry.features.configured;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.levelgen.features.BCLConfigureFeature;
import org.betterx.bclib.api.v3.levelgen.features.BCLFeatureBuilder;
import org.betterx.bclib.api.v3.levelgen.features.blockpredicates.BlockPredicates;
import org.betterx.betternether.BN;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.world.features.NetherSakuraBushFeature;
import org.betterx.betternether.world.features.WillowBushFeature;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.*;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class NetherVegetation {
    private static final ModCore C = BetterNether.C;
    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_NETHERRACK_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BN.id("bonemeal_netherrack_moss"))
            .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 30)
            .addAllStatesFor(BlockInkBush.AGE, NetherBlocks.INK_BUSH, 80)
            .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 40)
            .add(NetherBlocks.NETHER_GRASS, 200)
            .addAllStatesFor(BlockBlackApple.AGE, NetherBlocks.BLACK_APPLE, 50)
            .add(NetherBlocks.MAT_WART.getSeed(), 60)
            .build();
    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_NETHER_MYCELIUM = BCLFeatureBuilder
            .startBonemealPatch(BN.id("temp_bonemeal_nether_mycelium"))
            .add(NetherBlocks.GRAY_MOLD, 200)
            .add(NetherBlocks.RED_MOLD, 180)
            .addAllStatesFor(BlockCommonPlant.AGE, NetherBlocks.ORANGE_MUSHROOM, 40)
            .add(Blocks.RED_MUSHROOM, 60)
            .add(Blocks.BROWN_MUSHROOM, 60)
            .add(Blocks.CRIMSON_FUNGUS, 80)
            .add(Blocks.WARPED_FUNGUS, 80)
            .add(NetherBlocks.SEPIA_BONE_GRASS, 30)
            .add(NetherBlocks.BONE_GRASS, 30)
            .add(NetherBlocks.JUNGLE_PLANT, 30)
            .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
            .build();
    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_JUNGLE_GRASS = BCLFeatureBuilder
            .startBonemealPatch(BN.id("bonemeal_jungle_grass"))
            .addAllStatesFor(BlockEggPlant.AGE, NetherBlocks.EGG_PLANT, 80)
            .add(NetherBlocks.JUNGLE_PLANT, 80)
            .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 30)
            .addAllStatesFor(BlockFeatherFern.AGE, NetherBlocks.FEATHER_FERN, 20)
            .build();
    public static final BCLConfigureFeature<NetherForestVegetationFeature, NetherForestVegetationConfig> BONEMEAL_MUSHROOM_GRASS = BCLFeatureBuilder
            .startBonemealNetherVegetation(BN.id("bonemeal_mushroom_grass"))
            .add(NetherBlocks.BONE_GRASS, 180)
            .addAllStatesFor(BlockFeatherFern.AGE, NetherBlocks.FEATHER_FERN, 20)
            .build();
    public static final BCLConfigureFeature<NetherForestVegetationFeature, NetherForestVegetationConfig> BONEMEAL_SEPIA_MUSHROOM_GRASS = BCLFeatureBuilder
            .startBonemealNetherVegetation(BN.id("bonemeal_sepia_mushroom_grass"))
            .add(NetherBlocks.SEPIA_BONE_GRASS, 180)
            .build();
    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_SWAMPLAND_GRASS = BCLFeatureBuilder
            .startBonemealPatch(BN.id("bonemeal_swampland_grass"))
            .add(NetherBlocks.SOUL_VEIN, 80)
            .add(NetherBlocks.SWAMP_GRASS, 200)
            .add(NetherBlocks.FEATHER_FERN, 80)
            .build();
    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_CEILING_MUSHROOMS = BCLFeatureBuilder
            .startBonemealPatch(BN.id("bonemeal_ceiling_mushrooms"))
            .add(NetherBlocks.NETHER_GRASS, 80)
            .build();
    public static final ConfiguredFeatureKey<AsMultiPlaceRandomSelect> VEGETATION_MUSHROOM_FORREST =
            ConfiguredFeatureManager.multiPlaceRandomFeature(C.id("vegetation_mushroom_forrest"));
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<NoneFeatureConfiguration>, NoneFeatureConfiguration>> JELLYFISH_MUSHROOM =
            ConfiguredFeatureManager.configuration(C.id("jellyfish_mushroom"), NetherFeatures.JELLYFISH_MUSHROOM);
    public static final ConfiguredFeatureKey<RandomPatch> PATCH_JELLYFISH_MUSHROOM =
            ConfiguredFeatureManager.randomPatch(C.id("patch_jellyfish_mushroom"));
    public static final ConfiguredFeatureKey<ForSimpleBlock> PATCH_BLACK_BUSH =
            ConfiguredFeatureManager.simple(C.id("patch_back_bush"));
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<NoneFeatureConfiguration>, NoneFeatureConfiguration>> WALL_LUCIS =
            ConfiguredFeatureManager.configuration(C.id("patch_lucis"), NetherFeatures.LUCIS);
    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_SOUL_SOIL =
            ConfiguredFeatureManager.bonemealPatch(C.id("bonemeal_soul_soil"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_MAGMA_LAND =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_magma_land"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_GRASSLANDS =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_nether_grasslands"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_GRAVEL_DESERT =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_nether_gravel_desert"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_JUNGLE =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_jungle"));
    public static final ConfiguredFeatureKey<AsMultiPlaceRandomSelect> VEGETATION_POOR_GRASSLANDS =
            ConfiguredFeatureManager.multiPlaceRandomFeature(C.id("vegetation_nether_poor_grasslands"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_SOUL_PLAIN =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_soul_plain"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_WART_FOREST =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_wart_forest"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_WART_FOREST_EDGE =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_wart_forest_edge"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_SWAMPLAND =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_nether_swampland"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_OLD_SWAMPLAND =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_old_swampland"));
    public static final ConfiguredFeatureKey<WeightedBlock> VEGETATION_OLD_WARPED_WOODS =
            ConfiguredFeatureManager.randomBlock(C.id("vegetation_old_warped_woods"));
    public static final ConfiguredFeatureKey<AsBlockColumn> NETHER_CACTUS =
            ConfiguredFeatureManager.blockColumn(C.id("patch_nether_cactus"));
    public static final ConfiguredFeatureKey<FacingBlock> WALL_MUSHROOM_RED_WITH_MOSS =
            ConfiguredFeatureManager.facingBlock(C.id("patch_wall_mushroom_red_with_moss"));
    public static final ConfiguredFeatureKey<FacingBlock> WALL_MUSHROOMS_WITH_MOSS =
            ConfiguredFeatureManager.facingBlock(C.id("patch_wall_mushrooms_with_moss"));
    public static final ConfiguredFeatureKey<FacingBlock> WALL_MUSHROOMS =
            ConfiguredFeatureManager.facingBlock(C.id("patch_wall_mushrooms"));
    public static final ConfiguredFeatureKey<FacingBlock> WALL_JUNGLE =
            ConfiguredFeatureManager.facingBlock(C.id("patch_wall_jungle"));
    public static final ConfiguredFeatureKey<FacingBlock> WALL_UPSIDE_DOWN =
            ConfiguredFeatureManager.facingBlock(C.id("patch_upside_down"));
    public static final ConfiguredFeatureKey<AsBlockColumn> NETHER_REED =
            ConfiguredFeatureManager.blockColumn(C.id("patch_nether_reed"));
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<NoneFeatureConfiguration>, NoneFeatureConfiguration>> WART_BUSH =
            ConfiguredFeatureManager.configuration(C.id("patch_wart_bush"), NetherFeatures.WART_BUSH);
    public static final ConfiguredFeatureKey<WithConfiguration<WillowBushFeature, NoneFeatureConfiguration>> WILLOW_BUSH =
            ConfiguredFeatureManager.configuration(C.id("patch_willow_bush"), NetherFeatures.WILLOW_BUSH);
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<NoneFeatureConfiguration>, NoneFeatureConfiguration>> RUBEUS_BUSH =
            ConfiguredFeatureManager.configuration(C.id("patch_rubeus_bush"), NetherFeatures.RUBEUS_BUSH);
    public static final ConfiguredFeatureKey<WithConfiguration<NetherSakuraBushFeature, NoneFeatureConfiguration>> SAKURA_BUSH =
            ConfiguredFeatureManager.configuration(C.id("patch_sakura_bush"), NetherFeatures.SAKURA_BUSH);
    public static final ConfiguredFeatureKey<WeightedBlock> SCULK_VEGETATION =
            ConfiguredFeatureManager.randomBlock(C.id("sculk_vegetation"));
    public static final ConfiguredFeatureKey<ForSimpleBlock> HOOK_MUSHROOM =
            ConfiguredFeatureManager.simple(C.id("patch_hook_mushroom"));
    public static final ConfiguredFeatureKey<ForSimpleBlock> MOSS_COVER =
            ConfiguredFeatureManager.simple(C.id("patch_moss_cover"));

    public static void setupBonemealFeatures() {
        NetherBlocks.NETHERRACK_MOSS.setVegetationFeature(BONEMEAL_NETHERRACK_MOSS);
        NetherBlocks.NETHER_MYCELIUM.setVegetationFeature(BONEMEAL_NETHER_MYCELIUM);
        NetherBlocks.JUNGLE_GRASS.setVegetationFeature(BONEMEAL_JUNGLE_GRASS);
        NetherBlocks.MUSHROOM_GRASS.setVegetationFeature(BONEMEAL_MUSHROOM_GRASS);
        NetherBlocks.SEPIA_MUSHROOM_GRASS.setVegetationFeature(BONEMEAL_SEPIA_MUSHROOM_GRASS);
        NetherBlocks.SWAMPLAND_GRASS.setVegetationFeature(BONEMEAL_SWAMPLAND_GRASS);
        NetherBlocks.CEILING_MUSHROOMS.setVegetationFeature(BONEMEAL_CEILING_MUSHROOMS);

        BonemealAPI.INSTANCE.addSpreadableFeatures(Blocks.SOUL_SOIL, () -> BONEMEAL_SOUL_SOIL.getHolder(WorldState.registryAccess()));
        BonemealAPI.INSTANCE.addSpreadableFeatures(Blocks.SOUL_SAND, () -> BONEMEAL_SOUL_SOIL.getHolder(WorldState.registryAccess()));
    }
}
