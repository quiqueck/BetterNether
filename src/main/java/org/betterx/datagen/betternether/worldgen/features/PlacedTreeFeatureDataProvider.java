package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.BN;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.world.features.configs.GloomwoodTreeConfiguration;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import de.ambertation.wover.block.api.predicate.BlockPredicates;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedTreeFeatureDataProvider extends WoverFeatureProvider {
    /**
     * The grove field for the gloomwood: {@link net.minecraft.world.level.levelgen.Noises#GRAVEL} sampled
     * at {@code pos * scale}.
     * <p>
     * GRAVEL's first octave is {@code -8}, so at scale 1 its base wavelength is 256 blocks. Halved here,
     * which puts a grove and the clearing beside it at roughly 60-70 blocks each - large enough to walk
     * out of and see the edge of, small enough that a single biome holds several. The Y scale is left
     * coarse on purpose: the nether stacks several cave layers in a biome, and a field that ignored Y
     * entirely would print the identical grove layout onto every one of them.
     */
    private static final float GROVE_SCALE_XZ = 2.0F;
    private static final float GROVE_SCALE_Y = 1.0F;

    /**
     * Where the field is cut into the three densities.
     * <p>
     * Measured rather than guessed, because a normal noise's value range is nothing like the [-1,1] the
     * shape of the function suggests. Sampled over a generated gloomwood floor at this scale, 29% of it
     * came out above 0.00, 24% above 0.05, 18% above 0.10 and 15% above 0.15. The first attempt put the
     * grove core at 0.15 and produced closed stands over a twentieth of the biome, which reads as the
     * odd copse rather than as forest.
     * <p>
     * The numbers below leave a grove core over roughly a tenth of the floor and its edge over another
     * tenth, so four fifths of the biome is open ground carrying nothing but the occasional lone tree -
     * the lone tree is what the gloomwood normally looks like, and a grove is the exception you walk
     * into. The estimate above comes from a spatially clustered sample spanning only a few periods of a
     * 128-block field, so treat these as the right order of magnitude rather than as exact quantiles.
     */
    private static final double GROVE_CORE = 0.25;
    private static final double GROVE_EDGE = 0.05;

    /**
     * How often a solitary gloomwood comes out bleached - aimed at one bleached tree per biome.
     * <p>
     * Measured rather than guessed, and not from counting bleached trees: with the chance forced to 1.0
     * in a test datapack every solitary tree buries a debris pocket, and those pockets are compact and
     * one per tree, so counting them counts the solitary trees exactly. That came to 12 across two
     * gloomwood patches, or six per biome. A third of six is two, which is where this is aimed: one per
     * biome was the original target, but at that rate a player crossing several gloomwoods was finding
     * none at all - the average is over biomes, and half of them round down to zero. Two is the smallest
     * number that makes the pale tree something you actually meet. (Counting bleached crowns instead of
     * pockets does not work: neighbouring canopies merge into one blob and undercount badly.)
     */
    private static final float BLEACHED_GLOOMWOOD_CHANCE = 0.33F;

    public PlacedTreeFeatureDataProvider(ModCore modCore) {
        super(modCore, modCore.id("trees"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherTrees.RUBEUS_TREE
                .bootstrap(ctx)
                .configuration(NaturalTreeConfiguration.natural())
                .register();

        NetherTrees.GLOOMWOOD_TREE
                .bootstrap(ctx)
                .configuration(GloomwoodTreeConfiguration.natural())
                .register();

        NetherTrees.GLOOMWOOD_TREE_SOLITARY
                .bootstrap(ctx)
                .configuration(GloomwoodTreeConfiguration.bleachedSometimes(BLEACHED_GLOOMWOOD_CHANCE))
                .register();

        NetherTrees.MUSHROOM_FIR.bootstrap(ctx).register();

        NetherTrees.STALAGNATE
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.TRIPLE_SHAPE_FILL)
                .direction(Direction.UP)
                .blockState(NetherWoodBlocks.MAT_STALAGNATE.getTrunk())
                .minHeight(3)
                .maxHeight(64)
                .register();

        NetherTrees.STALAGNATE_DOWN
                .bootstrap(ctx)
                .transformer(PillarFeatureConfig.KnownTransformers.TRIPLE_SHAPE_FILL)
                .direction(Direction.DOWN)
                .blockState(NetherWoodBlocks.MAT_STALAGNATE.getTrunk())
                .minHeight(3)
                .maxHeight(64)
                .register();

        NetherTrees.GIANT_MOLD
                .bootstrap(ctx)
                .direction(Direction.UP)
                .addTripleShape(NetherMushroomBlocks.GIANT_MOLD.defaultBlockState(), ClampedNormalInt.of(5, 1.3f, 3, 8))
                .register();

        NetherTrees.PATCH_BIG_RED_MUSHROOM
                .bootstrap(ctx)
                .direction(Direction.UP)
                .prioritizeTip()
                .addTripleShape(NetherMushroomBlocks.RED_LARGE_MUSHROOM.defaultBlockState(), ClampedNormalInt.of(6, 2.1f, 3, 9))
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .register();

        NetherTrees.PATCH_BIG_BROWN_MUSHROOM
                .bootstrap(ctx)
                .inlinePlace()
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .likeDefaultNetherVegetation()
                .tries(30)
                .spreadXZ(7)
                .register();

        NetherTrees.WART_TREE
                .bootstrap(ctx)
                .configuration(NaturalTreeConfiguration.natural())
                .register();

        NetherTrees.WILLOW_TREE.bootstrap(ctx).register();

        NetherTrees.OLD_WILLOW_TREE
                .bootstrap(ctx)
                .configuration(NaturalTreeConfiguration.naturalLarge())
                .register();

        NetherTrees.ANCHOR_TREE_BRANCH.bootstrap(ctx).register();
        NetherTrees.ANCHOR_TREE.bootstrap(ctx).register();
        NetherTrees.ANCHOR_TREE_ROOT.bootstrap(ctx).register();
        NetherTrees.SAKURA_TREE.bootstrap(ctx).register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
        NetherTreesPlaced.CRIMSON_GLOWING_TREE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/crimson_glow_tree_01"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_02"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_03"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_04"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.CRIMSON_PINE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/crimson_pine_01"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_02"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_03"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_04"), 0, 1.0f)
                .add(BN.id("trees/crimson_pine_05"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.RUBEUS_TREE
                .place(ctx)
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.MUSHROOM_FIR
                .place(ctx)
                .vanillaNetherGround(14)
                .isEmptyAndOnNetherGround()
                .register();

        // Gloomwood grows on the sculk floor rather than on nether ground, so the usual
        // isEmptyAndOnNetherGround() filter would reject every position in its own biome.
        //
        // Three placements over one noise field instead of one even stand. Density alone would not have
        // done it: the feature keeps its trees 7 blocks apart, so a single high count simply saturates
        // and covers the biome wall to wall, which is what it used to do. What makes a grove is the field
        // deciding *where* the count is allowed to apply, and the three slices below are disjoint - a
        // given column feeds exactly one of them.
        // The count no longer saturates the spacing on purpose. GloomwoodTreeConfiguration.SPACING keeps
        // the trees 9 blocks apart now, and a count high enough to fill that everywhere gave a closed
        // canopy - so this is deliberately short of it, and a grove comes out as a stand with gaps in it
        // rather than as a roof.
        NetherTreesPlaced.GLOOMWOOD_TREE
                .place(ctx)
                .vanillaNetherGround(8)
                .noiseAbove(GROVE_CORE, GROVE_SCALE_XZ, GROVE_SCALE_Y)
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        // The rim of a grove: the same trees, thinned to about a third, so the stand fades out over some
        // distance rather than ending at the line where the noise crosses its threshold.
        NetherTreesPlaced.GLOOMWOOD_TREE_EDGE
                .place(ctx)
                .vanillaNetherGround(3)
                .noiseIn(GROVE_EDGE, GROVE_CORE, GROVE_SCALE_XZ, GROVE_SCALE_Y)
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        // The open ground between the groves, and now four fifths of the biome - this is the gloomwood's
        // default rather than its exception. One candidate per chunk layer and a further one-in-three, so
        // the trees that do stand here have nothing near them. These are also the only gloomwoods that
        // can come out bleached, and the only ones with debris under them.
        NetherTreesPlaced.GLOOMWOOD_TREE_SOLITARY
                .place(ctx)
                .vanillaNetherGround(1)
                .noiseBelow(GROVE_EDGE, GROVE_SCALE_XZ, GROVE_SCALE_Y)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicate.matchesTag(CommonBlockTags.SCULK_LIKE))
                .register();

        NetherTreesPlaced.STALAGNATE
                .place(ctx, NetherTrees.STALAGNATE)
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(7)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.GIANT_MOLD
                .place(ctx, NetherTrees.GIANT_MOLD)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(5)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.BIG_RED_MUSHROOM
                .place(ctx)
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.BIG_BROWN_MUSHROOM
                .place(ctx)
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.OLD_RED_MUSHROOM
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/red_mushroom_01"), -1, 1.0f)
                .add(BN.id("trees/red_mushroom_02"), -0, 1.0f)
                .add(BN.id("trees/red_mushroom_03"), -0, 1.0f)
                .add(BN.id("trees/red_mushroom_04"), -3, 1.0f)
                .add(BN.id("trees/red_mushroom_05"), -3, 1.0f)
                .add(BN.id("trees/red_mushroom_06"), -1, 1.0f)
                .add(BN.id("trees/red_mushroom_07"), -4, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(5)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .register();

        NetherTreesPlaced.OLD_BROWN_MUSHROOM
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/brown_mushroom_02"), -3, 1.0f)
                .add(BN.id("trees/brown_mushroom_03"), -2, 1.0f)
                .add(BN.id("trees/brown_mushroom_01"), -2, 1.0f)
                .add(BN.id("trees/brown_mushroom_04"), -1, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(4)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .register();

        NetherTreesPlaced.SOUL_LILY
                .inlineConfiguration(ctx)
                .withFeature(NetherFeatures.SOUL_LILY)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .spreadXZ(3)
                .spreadY(3)
                .inlinePlace()
                .vanillaNetherGround(6)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        NetherTreesPlaced.WART_TREE
                .place(ctx)
                .vanillaNetherGround(14)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        NetherTreesPlaced.WILLOW_TREE
                .place(ctx)
                .vanillaNetherGround(14)
                .onceEvery(3)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.OLD_WILLOW_TREE
                .place(ctx)
                .vanillaNetherGround(14)
                .onceEvery(16)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.SAKURA_TREE
                .place(ctx)
                .betterNetherCeiling(5)
                .onceEvery(6)
                .isEmptyAndUnderNetherGround()
                .register();

        NetherTreesPlaced.ANCHOR_TREE
                .place(ctx)
                .betterNetherCeiling(5)
                .isEmptyAndUnderNetherGround()
                .onceEvery(15)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_SPARSE
                .place(ctx)
                .onlyInBiome()
                .count(1)
                .randomHeight4FromFloorCeil()
                .findSolidCeil(5)
                .isEmptyAndUnderNetherGround()
                .onceEvery(7)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_BRANCH
                .place(ctx)
                .betterNetherCeiling(3)
                .isEmptyAndUnderNetherGround()
                .onceEvery(11)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_ROOT
                .place(ctx)
                .betterNetherCeiling(2)
                .onceEvery(5)
                .isEmptyAndUnderNetherGround()
                .register();

        NetherTreesPlaced.BIG_WARPED_TREE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/warped_tree_01"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_02"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_03"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_04"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_05"), -3, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

    }
}
