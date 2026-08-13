package org.betterx.datagen.betternether.worldgen.features;

import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.predicate.BlockPredicates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.block.*;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
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
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
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
                .add(NetherMushroomBlocks.GRAY_MOLD, 200, GRAY_MOLD_ID)
                .add(NetherMushroomBlocks.RED_MOLD, 180)
                .addAllStatesFor(BlockCommonPlant.AGE, NetherMushroomBlocks.ORANGE_MUSHROOM, 100)
                .addAll(60, MUSHROOM_ID, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM)
                .add(Blocks.CRIMSON_FUNGUS, 80, MUSHROOM_ID)
                .add(Blocks.WARPED_FUNGUS, 80, MUSHROOM_ID)
                .addAll(
                        30,
                        MUSHROOM_ID,
                        NetherPlantBlocks.SEPIA_BONE_GRASS,
                        NetherPlantBlocks.BONE_GRASS,
                        NetherPlantBlocks.JUNGLE_PLANT
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
                .block(NetherPlantBlocks.BLACK_BUSH)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();

        NetherVegetation.BONEMEAL_SOUL_SOIL
                .bootstrap(ctx)
                .add(NetherVineBlocks.SOUL_VEIN, 150)
                .add(NetherPlantBlocks.SOUL_GRASS, 200)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        // One gloomgrass per floor material rather than the two mixed through one patch: each tuft is
        // the same colour as the block it stands on, which is what keeps the floor reading as ground
        // with cover on it instead of as speckle. The ground predicates name the blocks instead of
        // SCULK_LIKE - that tag holds both of them (and the molten variant), so it cannot tell them
        // apart, which is the entire point here.
        NetherVegetation.VEGETATION_GLOOMWOOD_BLEACHED
                .bootstrap(ctx)
                .block(NetherPlantBlocks.PALE_GLOOMGRASS)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesBlocks(NetherTerrainBlocks.BLEACHED_GLOOMSCULK))
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_GLOOMWOOD_SCULK
                .bootstrap(ctx)
                .block(NetherPlantBlocks.GLOOMGRASS)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_MAGMA_LAND
                .bootstrap(ctx)
                .add(NetherDecorBlocks.GEYSER, 40)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 120)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_GRASSLANDS
                .bootstrap(ctx)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockInkBush.AGE, NetherPlantBlocks.INK_BUSH, 80)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 40)
                .add(NetherPlantBlocks.NETHER_GRASS, 200)
                .addAllStatesFor(BlockBlackApple.AGE, NetherCropBlocks.BLACK_APPLE, 50)
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 60)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_GRAVEL_DESERT
                .bootstrap(ctx)
                .addAllStatesFor(BlockAgave.AGE, NetherPlantBlocks.AGAVE, 80)
                .addAllStatesFor(BlockBarrelCactus.AGE, NetherPlantBlocks.BARREL_CACTUS, 20)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_JUNGLE
                .bootstrap(ctx)
                .addAllStatesFor(BlockEggPlant.AGE, NetherPlantBlocks.EGG_PLANT, 80)
                .add(NetherPlantBlocks.JUNGLE_PLANT, 80)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherPlantBlocks.FEATHER_FERN, 20)
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
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 120)
                .add(NetherPlantBlocks.NETHER_GRASS, 200)
                .addAllStatesFor(BlockInkBush.AGE, NetherPlantBlocks.INK_BUSH, 80)
                .addAllStatesFor(BlockBlackApple.AGE, NetherCropBlocks.BLACK_APPLE, 50)
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 80)
                .register();
        NetherVegetation.VEGETATION_SOUL_PLAIN
                .bootstrap(ctx)
                .add(NetherVineBlocks.SOUL_VEIN, 80)
                .add(NetherPlantBlocks.SOUL_GRASS, 200)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_WART_FOREST
                .bootstrap(ctx)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 120)
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 60)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_WART_FOREST_EDGE
                .bootstrap(ctx)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 120)
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 60)
                .add(NetherPlantBlocks.SOUL_GRASS, 200)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_SWAMPLAND
                .bootstrap(ctx)
                .add(NetherVineBlocks.SOUL_VEIN, 80)
                .add(NetherPlantBlocks.SWAMP_GRASS, 200)
                .add(NetherPlantBlocks.FEATHER_FERN, 80)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .register();
        NetherVegetation.VEGETATION_OLD_SWAMPLAND
                .bootstrap(ctx)
                .add(NetherVineBlocks.SOUL_VEIN, 80)
                .add(NetherPlantBlocks.SWAMP_GRASS, 100)
                .add(Blocks.SCULK_VEIN, 40)
                .add(NetherPlantBlocks.FEATHER_FERN, 80)
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
                .addTopShape(NetherPlantBlocks.NETHER_CACTUS.defaultBlockState(), BiasedToBottomInt.of(1, 4))
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_GRAVEL_OR_SAND)
                .inRandomPatch()
                .tries(16)
                .register();

        NetherVegetation.WALL_MUSHROOM_RED_WITH_MOSS
                .bootstrap(ctx)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherWallPlantBlocks.WALL_MOSS, 20)
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
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_BROWN, 35)
                .add(NetherWallPlantBlocks.WALL_MOSS, 20)
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
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_RED, 40)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_BROWN, 35)
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
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_RED, 20)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_BROWN, 15)
                .add(NetherPlantBlocks.JUNGLE_MOSS, 40)
                .add(NetherWallPlantBlocks.WALL_MOSS, 40)
                .allHorizontal()
                .inlinePlace()
                .inRandomPatch()
                .tries(120)
                .spreadXZ(7)
                .spreadY(7)
                .register();
        NetherVegetation.WALL_UPSIDE_DOWN
                .bootstrap(ctx)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_RED, 20)
                .add(NetherWallPlantBlocks.WALL_MUSHROOM_BROWN, 15)
                .add(NetherPlantBlocks.JUNGLE_MOSS, 90)
                .allHorizontal()
                .inlinePlace()
                .is(BlockPredicate.solid())
                .inRandomPatch()
                .tries(120)
                .spreadXZ(4)
                .spreadY(7)
                .register();
        // A wisp is always placed at its full three blocks: BOTTOM carries the dark half of the stalk
        // gradient and MIDDLE the bright half, so a short column would put the dark stalk directly under
        // the head and lose the gradient the two stalk textures exist for.
        NetherVegetation.GLOOMWISP_VINE
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                // Built explicitly rather than with addTripleShape(): that emits BOTTOM, MIDDLE x n, TOP,
                // and repeating MIDDLE would repeat the bright half of the stalk gradient. The block's own
                // shapeAt() says MIDDLE is only ever the one segment under the head, so the column is
                // BOTTOM x n, MIDDLE, TOP - which also lets the height vary without breaking the gradient.
                .add(
                        UniformInt.of(0, BlockGloomwispVine.MAX_HEIGHT - 2),
                        NetherVineBlocks.GLOOMWISP_VINE.defaultBlockState()
                                                       .setValue(
                                                               BlockGloomwispVine.SHAPE,
                                                               BlockProperties.TripleShape.BOTTOM
                                                       )
                )
                .add(
                        ConstantInt.of(1),
                        NetherVineBlocks.GLOOMWISP_VINE.defaultBlockState()
                                                       .setValue(
                                                               BlockGloomwispVine.SHAPE,
                                                               BlockProperties.TripleShape.MIDDLE
                                                       )
                )
                .add(
                        ConstantInt.of(1),
                        NetherVineBlocks.GLOOMWISP_VINE.defaultBlockState()
                                                       .setValue(
                                                               BlockGloomwispVine.SHAPE,
                                                               BlockProperties.TripleShape.TOP
                                                       )
                )
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                // a stand rather than a clump: 32 tries over an eight-block spread
                .inRandomPatch()
                .tries(32)
                .spreadXZ(8)
                .register();

        // The shortest wisp is head-only, and has to be its own column - see the key's doc-comment.
        NetherVegetation.GLOOMWISP_VINE_HEAD
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .add(
                        ConstantInt.of(1),
                        NetherVineBlocks.GLOOMWISP_VINE.defaultBlockState()
                                                       .setValue(
                                                               BlockGloomwispVine.SHAPE,
                                                               BlockProperties.TripleShape.TOP
                                                       )
                )
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                // inRandomPatch() is what gives the inline placement a key; without it registration
                // fails with "A ResourceKey for a Feature can not be null"
                .inRandomPatch()
                .tries(12)
                .spreadXZ(8)
                .register();

        NetherVegetation.NETHER_REED
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .addTopShape(NetherWoodBlocks.MAT_REED.getStem().defaultBlockState(), BiasedToBottomInt.of(0, 3))
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
                .add(NetherPlantBlocks.SWAMP_GRASS, 200)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 80)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .inRandomPatch()
                .register();
        NetherVegetation.HOOK_MUSHROOM
                .bootstrap(ctx)
                .block(NetherMushroomBlocks.HOOK_MUSHROOM)
                .inlinePlace()
                .isEmptyAndUnderNetherGround()
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .register();
        NetherVegetation.MOSS_COVER
                .bootstrap(ctx)
                .block(NetherPlantBlocks.MOSS_COVER)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(120)
                .spreadXZ(8)
                .register();

        NetherVegetation.BONEMEAL_NETHERRACK_MOSS
                .bootstrap(ctx)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockInkBush.AGE, NetherPlantBlocks.INK_BUSH, 80)
                .addAllStatesFor(NetherWartBlock.AGE, Blocks.NETHER_WART, 40)
                .add(NetherPlantBlocks.NETHER_GRASS, 200)
                .addAllStatesFor(BlockBlackApple.AGE, NetherCropBlocks.BLACK_APPLE, 50)
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 60)
                .register();
        NetherVegetation.BONEMEAL_NETHER_MYCELIUM
                .bootstrap(ctx)
                .add(NetherMushroomBlocks.GRAY_MOLD, 200)
                .add(NetherMushroomBlocks.RED_MOLD, 180)
                .addAllStatesFor(BlockCommonPlant.AGE, NetherMushroomBlocks.ORANGE_MUSHROOM, 40)
                .add(Blocks.RED_MUSHROOM, 60)
                .add(Blocks.BROWN_MUSHROOM, 60)
                .add(Blocks.CRIMSON_FUNGUS, 80)
                .add(Blocks.WARPED_FUNGUS, 80)
                .add(NetherPlantBlocks.SEPIA_BONE_GRASS, 30)
                .add(NetherPlantBlocks.BONE_GRASS, 30)
                .add(NetherPlantBlocks.JUNGLE_PLANT, 30)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .register();
        NetherVegetation.BONEMEAL_JUNGLE_GRASS
                .bootstrap(ctx)
                .addAllStatesFor(BlockEggPlant.AGE, NetherPlantBlocks.EGG_PLANT, 80)
                .add(NetherPlantBlocks.JUNGLE_PLANT, 80)
                .addAllStatesFor(BlockMagmaFlower.AGE, NetherPlantBlocks.MAGMA_FLOWER, 30)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherPlantBlocks.FEATHER_FERN, 20)
                .register();
        NetherVegetation.BONEMEAL_MUSHROOM_GRASS
                .bootstrap(ctx)
                .add(NetherPlantBlocks.BONE_GRASS, 180)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherPlantBlocks.FEATHER_FERN, 20)
                .register();
        NetherVegetation.BONEMEAL_SEPIA_MUSHROOM_GRASS
                .bootstrap(ctx)
                .add(NetherPlantBlocks.SEPIA_BONE_GRASS, 180)
                .register();
        NetherVegetation.BONEMEAL_SWAMPLAND_GRASS
                .bootstrap(ctx)
                .add(NetherVineBlocks.SOUL_VEIN, 80)
                .add(NetherPlantBlocks.SWAMP_GRASS, 200)
                .add(NetherPlantBlocks.FEATHER_FERN, 80)
                .register();
        NetherVegetation.BONEMEAL_CEILING_MUSHROOMS
                .bootstrap(ctx)
                .add(NetherPlantBlocks.NETHER_GRASS, 80)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
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
                .add(NetherPlantBlocks.BONE_GRASS, 180)
                .addAllStatesFor(BlockFeatherFern.AGE, NetherPlantBlocks.FEATHER_FERN, 20)
                .inlinePlace()
                .vanillaNetherGround(24)
                .register();

        // Both at the count the single mixed patch had: a patch that lands on the wrong material is
        // dropped by its filter rather than moved, so the two together put about as much grass on the
        // floor as the one did - each on its own share of it.
        NetherVegetationPlaced.VEGETATION_GLOOMWOOD_BLEACHED
                .place(ctx, NetherVegetation.VEGETATION_GLOOMWOOD_BLEACHED)
                .betterNetherGround(10)
                .isEmptyAndOn(BlockPredicate.matchesBlocks(NetherTerrainBlocks.BLEACHED_GLOOMSCULK))
                .register();

        NetherVegetationPlaced.VEGETATION_GLOOMWOOD_SCULK
                .place(ctx, NetherVegetation.VEGETATION_GLOOMWOOD_SCULK)
                .betterNetherGround(10)
                .isEmptyAndOn(BlockPredicate.matchesBlocks(Blocks.SCULK))
                .register();

        // Heights one to four, as two columns behind a weighted pick: a lone head, or a head over one
        // bright stalk and up to two dark ones. One in four is the bare head.
        // Heights one to four, as two features the biome runs side by side rather than one weighted
        // pick. A random-select would be the tidier shape, but it has to name a configured feature and
        // an inline one has no key to give it; two placed features cost nothing and are plainly readable.
        NetherVegetationPlaced.GLOOMWISP_VINE_HEAD
                .place(ctx, NetherVegetation.GLOOMWISP_VINE_HEAD)
                .vanillaNetherGround(1)
                .onceEvery(3)
                .register();

        NetherVegetationPlaced.GLOOMWISP_VINE
                .place(ctx, NetherVegetation.GLOOMWISP_VINE)
                .vanillaNetherGround(2)
                .onceEvery(2)
                .register();

        NetherVegetationPlaced.VEGETATION_SULFURIC_BONE_REEF
                .inlineConfiguration(ctx)
                .netherForrestVegetation()
                .add(NetherPlantBlocks.SEPIA_BONE_GRASS, 180)
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
                .add(NetherWoodBlocks.MAT_WART.getSeed(), 80)
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
                .withFeature(NetherFeatures.WART_CAP)
                .inlinePlace()
                .count(32)
                .squarePlacement()
                .randomHeight10FromFloorCeil()
                .onlyInBiome()
                .findSolidSurface(Direction.Plane.HORIZONTAL.stream().toList(), 12, false)
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
