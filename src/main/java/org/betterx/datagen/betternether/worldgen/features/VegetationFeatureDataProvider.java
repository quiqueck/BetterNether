package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.bclib.api.v3.levelgen.features.blockpredicates.BlockPredicates;
import org.betterx.bclib.api.v3.levelgen.features.config.PlaceFacingBlockConfig;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationFeatureDataProvider extends WoverFeatureProvider {
    public VegetationFeatureDataProvider(
            ModCore modCore
    ) {
        super(modCore, modCore.id("vegetation"));
    }

    @Override
    protected void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        final int GRAY_MOLD_ID = 42;
        final int MUSHROOM_ID = 23;

        NetherVegetation.VEGETATION_MUSHROOM_FORREST
                .bootstrap(ctx)
                .placement((placer, id) -> placer
                        .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                        .inRandomPatch()
                        .tries(id == GRAY_MOLD_ID ? 140 : 96)
                        .spreadXZ(id == GRAY_MOLD_ID ? 8 : 7)
                        .spreadY(id == MUSHROOM_ID ? 6 : 3)
                        .inlinePlace()
                        .directHolder()
                )
                .add(NetherBlocks.GRAY_MOLD, 200, GRAY_MOLD_ID)
                .add(NetherBlocks.RED_MOLD, 180)
                .addAllStatesFor(BlockCommonPlant.AGE, NetherBlocks.ORANGE_MUSHROOM, 100)
                .addAll(60, MUSHROOM_ID, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM)
                .add(Blocks.CRIMSON_FUNGUS, 80, MUSHROOM_ID)
                .add(Blocks.WARPED_FUNGUS, 80, MUSHROOM_ID)
                .addAll(
                        30,
                        MUSHROOM_ID,
                        NetherBlocks.SEPIA_BONE_GRASS,
                        NetherBlocks.BONE_GRASS,
                        NetherBlocks.JUNGLE_PLANT
                )
                .register();

        NetherVegetation.JELLYFISH_MUSHROOM.bootstrap(ctx).register();
        NetherVegetation.WALL_LUCIS.bootstrap(ctx).register();

        NetherVegetation.PATCH_JELLYFISH_MUSHROOM
                .bootstrap(ctx)
                .featureToPlace(NetherVegetationPlaced.JELLYFISH_MUSHROOM)
                .tries(6)
                .spreadXZ(6)
                .spreadY(4)
                .register();

        NetherVegetation.PATCH_BLACK_BUSH
                .bootstrap(ctx)
                .block(NetherBlocks.BLACK_BUSH)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();

        NetherVegetation.BONEMEAL_SOUL_SOIL
                .bootstrap(ctx)
                .add(NetherBlocks.SOUL_VEIN, 150)
                .add(NetherBlocks.SOUL_GRASS, 200)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        NetherVegetation.VEGETATION_MAGMA_LAND
                .bootstrap(ctx)
                .add(NetherBlocks.GEYSER, 40)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 120)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_GRASSLANDS
                .bootstrap(ctx)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockInkBush.AGE, NetherBlocks.INK_BUSH, 80)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 40)
                .add(NetherBlocks.NETHER_GRASS, 200)
                .addAllStatesFor(BlockBlackApple.AGE, NetherBlocks.BLACK_APPLE, 50)
                .add(NetherBlocks.MAT_WART.getSeed(), 60)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_GRAVEL_DESERT
                .bootstrap(ctx)
                .addAllStatesFor(BlockAgave.AGE, NetherBlocks.AGAVE, 80)
                .addAllStatesFor(BlockBarrelCactus.AGE, NetherBlocks.BARREL_CACTUS, 20)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_JUNGLE
                .bootstrap(ctx)
                .addAllStatesFor(BlockEggPlant.AGE, NetherBlocks.EGG_PLANT, 80)
                .add(NetherBlocks.JUNGLE_PLANT, 80)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherBlocks.FEATHER_FERN, 20)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_POOR_GRASSLANDS
                .bootstrap(ctx)
                .placement((placer, id) -> placer
                        .isEmptyAndOnNetherGround()
                        .inRandomPatch()
                        .inlinePlace()
                        .directHolder()
                )
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 40)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 120)
                .add(NetherBlocks.NETHER_GRASS, 200)
                .addAllStatesFor(BlockInkBush.AGE, NetherBlocks.INK_BUSH, 80)
                .addAllStatesFor(BlockBlackApple.AGE, NetherBlocks.BLACK_APPLE, 50)
                .add(NetherBlocks.MAT_WART.getSeed(), 80)
                .register();
        NetherVegetation.VEGETATION_SOUL_PLAIN
                .bootstrap(ctx)
                .add(NetherBlocks.SOUL_VEIN, 80)
                .add(NetherBlocks.SOUL_GRASS, 200)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_WART_FOREST
                .bootstrap(ctx)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 120)
                .add(NetherBlocks.MAT_WART.getSeed(), 60)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_WART_FOREST_EDGE
                .bootstrap(ctx)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 120)
                .add(NetherBlocks.MAT_WART.getSeed(), 60)
                .add(NetherBlocks.SOUL_GRASS, 200)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_SWAMPLAND
                .bootstrap(ctx)
                .add(NetherBlocks.SOUL_VEIN, 80)
                .add(NetherBlocks.SWAMP_GRASS, 200)
                .add(NetherBlocks.FEATHER_FERN, 80)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_OLD_SWAMPLAND
                .bootstrap(ctx)
                .add(NetherBlocks.SOUL_VEIN, 80)
                .add(NetherBlocks.SWAMP_GRASS, 100)
                .add(Blocks.SCULK_VEIN, 40)
                .add(NetherBlocks.FEATHER_FERN, 80)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_OLD_WARPED_WOODS
                .bootstrap(ctx)
                .add(Blocks.WARPED_FUNGUS, 50)
                .add(Blocks.WARPED_ROOTS, 200)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_NYLIUM)
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.NETHER_CACTUS
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .addTopShape(NetherBlocks.NETHER_CACTUS.defaultBlockState(), BiasedToBottomInt.of(1, 4))
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_GRAVEL_OR_SAND)
                .inRandomPatch()
                .tries(16)
                .register();
        NetherVegetation.WALL_MUSHROOM_RED_WITH_MOSS
                .bootstrap(ctx)
                .add(NetherBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherBlocks.WALL_MOSS, 20)
                .allHorizontal()
                .inlinePlace()
                .is(BlockPredicate.solid())
                .inRandomPatch()
                .tries(120)
                .spreadXZ(4)
                .spreadY(7)
                .register();
        NetherVegetation.WALL_MUSHROOMS_WITH_MOSS
                .bootstrap(ctx)
                .add(NetherBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherBlocks.WALL_MUSHROOM_BROWN, 35)
                .add(NetherBlocks.WALL_MOSS, 20)
                .allHorizontal()
                .inlinePlace()
                .is(BlockPredicate.solid())
                .inRandomPatch()
                .tries(120)
                .spreadXZ(4)
                .spreadY(7)
                .register();
        NetherVegetation.WALL_MUSHROOMS
                .bootstrap(ctx)
                .add(NetherBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherBlocks.WALL_MUSHROOM_BROWN, 35)
                .allHorizontal()
                .inlinePlace()
                .is(BlockPredicate.solid())
                .inRandomPatch()
                .tries(120)
                .spreadXZ(4)
                .spreadY(7)
                .register();
        NetherVegetation.WALL_JUNGLE
                .bootstrap(ctx)
                .add(NetherBlocks.WALL_MUSHROOM_RED, 20)
                .add(NetherBlocks.WALL_MUSHROOM_BROWN, 15)
                .add(NetherBlocks.JUNGLE_MOSS, 40)
                .add(NetherBlocks.WALL_MOSS, 40)
                .allHorizontal()
                .inlinePlace()
                .inRandomPatch()
                .tries(120)
                .spreadXZ(7)
                .spreadY(7)
                .register();
        NetherVegetation.WALL_UPSIDE_DOWN
                .bootstrap(ctx)
                .add(NetherBlocks.WALL_MUSHROOM_RED, 20)
                .add(NetherBlocks.WALL_MUSHROOM_BROWN, 15)
                .add(NetherBlocks.JUNGLE_MOSS, 90)
                .allHorizontal()
                .inlinePlace()
                .is(BlockPredicate.solid())
                .inRandomPatch()
                .tries(120)
                .spreadXZ(4)
                .spreadY(7)
                .register();
        NetherVegetation.NETHER_REED
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .addTopShape(NetherBlocks.MAT_REED.getStem().defaultBlockState(), BiasedToBottomInt.of(0, 3))
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .belowIsNextTo(BlockPredicates.ONLY_LAVA)
                .inRandomPatch()
                .register();
        NetherVegetation.WART_BUSH
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.WILLOW_BUSH
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.RUBEUS_BUSH
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.SAKURA_BUSH
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.SCULK_VEGETATION
                .bootstrap(ctx)
                .add(NetherBlocks.SWAMP_GRASS, 200)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherBlocks.MAGMA_FLOWER, 80)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .inRandomPatch()
                .register();
        NetherVegetation.HOOK_MUSHROOM
                .bootstrap(ctx)
                .block(NetherBlocks.HOOK_MUSHROOM)
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.MOSS_COVER
                .bootstrap(ctx)
                .block(NetherBlocks.MOSS_COVER)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(120)
                .spreadXZ(8)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstapContext<PlacedFeature> ctx) {
        NetherVegetationPlaced.VEGETATION_MUSHROOM_FORREST_EDGE
                .place(ctx)
                .vanillaNetherGround(8)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherVegetationPlaced.VEGETATION_MUSHROOM_FORREST
                .place(ctx)
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherVegetationPlaced.JELLYFISH_MUSHROOM
                .place(ctx)
                .findSolidFloor(4)
                .isEmptyAndOnNylium()
                .register();

        NetherVegetationPlaced.PATCH_JELLYFISH_MUSHROOM
                .place(ctx)
                .vanillaNetherGround(6)
                .onceEvery(4)
                .register();

        NetherVegetationPlaced.JELLYFISH_MUSHROOM_DENSE
                .place(ctx)
                .vanillaNetherGround(4)
                .onceEvery(2)
                .register();

        NetherVegetationPlaced.BLACK_BUSH
                .place(ctx)
                .vanillaNetherGround(6)
                .register();

        NetherVegetationPlaced.BLACK_BUSH_SPARSE
                .place(ctx)
                .vanillaNetherGround(3)
                .onceEvery(2)
                .register();

        NetherVegetationPlaced.WALL_LUCIS
                .place(ctx)
                .isEmpty()
                .inRandomPatch()
                .tries(120)
                .spreadXZ(12)
                .spreadY(10)
                .inlinePlace()
                .betterNetherOnWall(5)
                .onceEvery(2)
                .isEmpty()
                .register();

        NetherVegetationPlaced.VEGETATION_BONE_REEF
                .inlineConfiguration(ctx)
                .netherForrestVegetation()
                .add(NetherBlocks.BONE_GRASS, 180)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherBlocks.FEATHER_FERN, 20)
                .inlinePlace()
                .vanillaNetherGround(24)
                .register();

        NetherVegetationPlaced.VEGETATION_SULFURIC_BONE_REEF
                .inlineConfiguration(ctx)
                .netherForrestVegetation()
                .add(NetherBlocks.SEPIA_BONE_GRASS, 180)
                .inlinePlace()
                .vanillaNetherGround(8)
                .register();
        NetherVegetationPlaced.VEGETATION_MAGMA_LAND
                .place(ctx, NetherVegetation.VEGETATION_MAGMA_LAND)
                .betterNetherGround(8)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_CRIMSON_GLOWING_WOODS
                .inlineConfiguration(ctx)
                .netherForrestVegetation()
                .add(Blocks.CRIMSON_ROOTS, 120)
                .add(Blocks.CRIMSON_FUNGUS, 80)
                .add(NetherBlocks.MAT_WART.getSeed(), 80)
                .inlinePlace()
                .betterNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_GRASSLANDS
                .place(ctx, NetherVegetation.VEGETATION_GRASSLANDS)
                .betterNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_GRAVEL_DESERT
                .place(ctx, NetherVegetation.VEGETATION_GRAVEL_DESERT)
                .vanillaNetherGround(9)
                .onceEvery(5)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_JUNGLE
                .place(ctx, NetherVegetation.VEGETATION_JUNGLE)
                .vanillaNetherGround(18)
                .isEmptyAndOnNetherGround()
                .register();

        NetherVegetationPlaced.VEGETATION_POOR_GRASSLANDS
                .place(ctx, NetherVegetation.VEGETATION_POOR_GRASSLANDS)
                .vanillaNetherGround(8)
                .onceEvery(3)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_SOUL_PLAIN
                .place(ctx, NetherVegetation.VEGETATION_SOUL_PLAIN)
                .vanillaNetherGround(8)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();
        NetherVegetationPlaced.VEGETATION_WART_FOREST
                .place(ctx, NetherVegetation.VEGETATION_WART_FOREST)
                .vanillaNetherGround(10)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();
        NetherVegetationPlaced.VEGETATION_WART_FOREST_EDGE
                .place(ctx, NetherVegetation.VEGETATION_WART_FOREST_EDGE)
                .vanillaNetherGround(5)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();
        NetherVegetationPlaced.VEGETATION_SWAMPLAND
                .place(ctx, NetherVegetation.VEGETATION_SWAMPLAND)
                .vanillaNetherGround(20)
                .isEmptyAndOnNetherGround()
                .register();
        NetherVegetationPlaced.VEGETATION_OLD_SWAMPLAND
                .place(ctx, NetherVegetation.VEGETATION_OLD_SWAMPLAND)
                .vanillaNetherGround(22)
                .isEmptyAndOnNetherGround()
                .register();

        NetherVegetationPlaced.VEGETATION_OLD_WARPED_WOODS
                .place(ctx, NetherVegetation.VEGETATION_OLD_WARPED_WOODS)
                .vanillaNetherGround(8)
                .isEmptyAndOn(BlockPredicates.ONLY_NYLIUM)
                .register();
        NetherVegetationPlaced.NETHER_CACTUS
                .place(ctx, NetherVegetation.NETHER_CACTUS)
                .betterNetherGround(4)
                .onceEvery(5)
                .register();
        NetherVegetationPlaced.WALL_MUSHROOM_RED_WITH_MOSS
                .place(ctx, NetherVegetation.WALL_MUSHROOM_RED_WITH_MOSS)
                .betterNetherInWall(25)
                .register();
        NetherVegetationPlaced.WALL_MUSHROOMS_WITH_MOSS
                .place(ctx, NetherVegetation.WALL_MUSHROOMS_WITH_MOSS)
                .betterNetherInWall(25)
                .register();
        NetherVegetationPlaced.WALL_MUSHROOMS
                .place(ctx, NetherVegetation.WALL_MUSHROOMS)
                .betterNetherInWall(25)
                .register();
        NetherVegetationPlaced.WALL_JUNGLE
                .place(ctx, NetherVegetation.WALL_JUNGLE)
                .betterNetherInWall(50)
                .register();
        NetherVegetationPlaced.WALL_UPSIDE_DOWN
                .place(ctx, NetherVegetation.WALL_UPSIDE_DOWN)
                .betterNetherInWall(50)
                .register();

        NetherVegetationPlaced.NETHER_REED
                .place(ctx, NetherVegetation.NETHER_REED)
                .vanillaNetherGround(6)
                .register();

        NetherVegetationPlaced.WART_BUSH
                .place(ctx, NetherVegetation.WART_BUSH)
                .vanillaNetherGround(4)
                .onceEvery(3)
                .register();
        NetherVegetationPlaced.WILLOW_BUSH
                .place(ctx, NetherVegetation.WILLOW_BUSH)
                .vanillaNetherGround(4)
                .onceEvery(3)
                .register();
        NetherVegetationPlaced.RUBEUS_BUSH
                .place(ctx, NetherVegetation.RUBEUS_BUSH)
                .vanillaNetherGround(4)
                .onceEvery(2)
                .register();
        NetherVegetationPlaced.SAKURA_BUSH
                .place(ctx, NetherVegetation.SAKURA_BUSH)
                .vanillaNetherGround(4)
                .onceEvery(6)
                .register();
        NetherVegetationPlaced.WART_CAP
                .inlineConfiguration(ctx)
                .configuration(NetherFeatures.WART_CAP)
                .inlinePlace()
                .count(32)
                .squarePlacement()
                .randomHeight10FromFloorCeil()
                .onlyInBiome()
                .findSolidSurface(PlaceFacingBlockConfig.HORIZONTAL, 12, false)
                .register();
        NetherVegetationPlaced.SCULK_VEGETATION
                .place(ctx, NetherVegetation.SCULK_VEGETATION)
                .vanillaNetherGround(12)
                .onceEvery(2)
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .register();
        NetherVegetationPlaced.HOOK_MUSHROOM
                .place(ctx, NetherVegetation.HOOK_MUSHROOM)
                .betterNetherCeiling(8)
                .onceEvery(2)
                .isEmptyAndUnderNetherGround()
                .register();
        NetherVegetationPlaced.MOSS_COVER
                .place(ctx, NetherVegetation.MOSS_COVER)
                .vanillaNetherGround(8)
                .isEmptyAndOnNetherGround()
                .register();
    }
}
