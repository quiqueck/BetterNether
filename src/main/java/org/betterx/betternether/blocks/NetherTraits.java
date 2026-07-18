package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Combinators for the {@code List<BlockTrait<?, ?>>} that the {@code NetherBlocks.register*} helpers take,
 * so a block can be handed several unrelated rules (a {@link NetherSurvival} ground rule, a
 * {@link NetherRender} layer, a {@link ModelTraitLibrary} model) through the single list parameter.
 * <p>
 * The client-only trait builders ({@link ModelTraitLibrary}, {@link ClientBlockTraits#RENDER_LAYER}) return
 * {@code null} outside the client/datagen, so these lists must tolerate a null element - {@link List#of}
 * would throw on one. Nulls are dropped here, so the returned lists are always null-free.
 */
public class NetherTraits {
    /** A null-free list of the given traits; any {@code null} (client-only, off-client) is dropped. */
    @SafeVarargs
    public static List<BlockTrait<?, ?>> of(BlockTrait<?, ?>... traits) {
        return and(List.of(), traits);
    }

    /**
     * {@code base} plus {@link CompostableBlockTrait} at bclib's default 0.1 chance.
     * <p>
     * Unlike the old {@code BehaviourCompostable} marker - which only ever produced the
     * {@code c:compostable} item tag at datagen, never a composter entry (see its javadoc) - the trait
     * both tags the item AND calls {@code ComposterAPI.allowCompost}, so the block actually composts.
     */
    public static List<BlockTrait<?, ?>> compostable(List<BlockTrait<?, ?>> base) {
        return and(base, CompostableBlockTrait.withDefault());
    }

    /** {@link #compostable(List)} with no other traits. */
    public static List<BlockTrait<?, ?>> compostable() {
        return compostable(List.of());
    }

    /**
     * {@code base} plus everything the retired {@code BehaviourPlant} marker used to contribute, reproduced
     * as traits: the {@link PlantLikeBlockTrait} nature-tab marker, {@code mineable/hoe}
     * ({@link BlockTraits#MINEABLE_WITH}), {@link CompostableBlockTrait} at 0.1, and the
     * {@code c:plant} block tag ({@link VegetationTagTrait#plant()}).
     * <p>
     * {@code BehaviourPlant} extended {@code AddMineableHoe + BehaviourCompostable + BehaviourPlantLike}; the
     * PLANT block tag came from the {@code BehaviourPlant} scan in {@code BCLAutoBlockTagProvider}. This is
     * NOT {@code PlantLikeBlockTrait.plant()} because that bundles its own {@code CompostableBlockTrait} - we
     * fold {@link #compostable(List)} in here instead so the compostable trait is added exactly once.
     */
    public static List<BlockTrait<?, ?>> plant(List<BlockTrait<?, ?>> base) {
        return and(
                compostable(base),
                PlantLikeBlockTrait.withDefault(),
                BlockTraits.MINEABLE_WITH.needsHoe(),
                VegetationTagTrait.plant()
        );
    }

    /** {@link #plant(List)} with no other traits. */
    public static List<BlockTrait<?, ?>> plant() {
        return plant(List.of());
    }

    /**
     * {@code base} plus everything the retired {@code BehaviourSapling} marker contributed: the nature-tab
     * marker, {@code mineable/hoe}, {@link CompostableBlockTrait} at 0.1, and the sapling block/item tags
     * ({@link VegetationTagTrait#sapling()} = {@code minecraft/c:saplings}). {@code BehaviourSapling} extended
     * {@code AddMineableHoe + BehaviourCompostable + BehaviourPlantLike + BehaviourSaplingLike}; unlike
     * {@code BehaviourPlant} it never carried the PLANT tag.
     */
    public static List<BlockTrait<?, ?>> sapling(List<BlockTrait<?, ?>> base) {
        return and(
                compostable(base),
                PlantLikeBlockTrait.withDefault(),
                BlockTraits.MINEABLE_WITH.needsHoe(),
                VegetationTagTrait.sapling()
        );
    }

    /** {@link #sapling(List)} with no other traits. */
    public static List<BlockTrait<?, ?>> sapling() {
        return sapling(List.of());
    }

    /**
     * {@code base} plus everything the retired {@code BehaviourSeed} marker contributed: the nature-tab
     * marker, {@code mineable/hoe}, {@link CompostableBlockTrait} at 0.1, and the seed block/item tags
     * ({@link VegetationTagTrait#seed()} = {@code c:seeds}). {@code BehaviourSeed} extended
     * {@code AddMineableHoe + BehaviourCompostable + BehaviourPlantLike + BehaviourSeedLike}; like
     * {@code BehaviourSapling} it never carried the PLANT tag.
     */
    public static List<BlockTrait<?, ?>> seed(List<BlockTrait<?, ?>> base) {
        return and(
                compostable(base),
                PlantLikeBlockTrait.withDefault(),
                BlockTraits.MINEABLE_WITH.needsHoe(),
                VegetationTagTrait.seed()
        );
    }

    /** {@link #seed(List)} with no other traits. */
    public static List<BlockTrait<?, ?>> seed() {
        return seed(List.of());
    }

    /**
     * {@code base} plus everything the retired {@code BehaviourVine}/{@code BehaviourClimableVine} markers
     * contributed: {@code mineable/hoe}, {@code mineable/shears}, {@link CompostableBlockTrait} at 0.1, the
     * {@link BlockTraits#CLIMBABLE} tag, and the vine block tag ({@link VegetationTagTrait#vine()}).
     * {@code BehaviourVine} extended {@code AddMineableShears + AddMineableHoe + BehaviourCompostable +
     * BehaviourClimable}; {@code BehaviourClimableVine} was just {@code BehaviourClimable + BehaviourVine}
     * combined, so both retire to the same set of traits here.
     * <p>
     * This is NOT {@code bclib}'s full {@code VineBlockTrait} - that also forces a map color/light-level
     * {@code PlantBlockTrait} and a datagen cube model, which would fight the hand-authored
     * {@code WeightedCrossModelTrait}/external models these blocks already carry and double up the
     * {@link CompostableBlockTrait} already folded in via {@link #compostable(List)} above.
     */
    public static List<BlockTrait<?, ?>> vine(List<BlockTrait<?, ?>> base) {
        return and(
                compostable(base),
                BlockTraits.MINEABLE_WITH.needsHoe(),
                BlockTraits.MINEABLE_WITH.needsShears(),
                BlockTraits.CLIMBABLE.withDefault(),
                VegetationTagTrait.vine()
        );
    }

    /** {@link #vine(List)} with no other traits. */
    public static List<BlockTrait<?, ?>> vine() {
        return vine(List.of());
    }

    /** {@code a} followed by {@code b}, dropping any {@code null}. */
    public static List<BlockTrait<?, ?>> concat(List<BlockTrait<?, ?>> a, List<BlockTrait<?, ?>> b) {
        final List<BlockTrait<?, ?>> combined = new ArrayList<>(a.size() + b.size());
        a.stream().filter(t -> t != null).forEach(combined::add);
        b.stream().filter(t -> t != null).forEach(combined::add);
        return combined;
    }

    /** {@code base} plus the given extra traits, dropping any {@code null}. */
    @SafeVarargs
    public static List<BlockTrait<?, ?>> and(List<BlockTrait<?, ?>> base, BlockTrait<?, ?>... extra) {
        final List<BlockTrait<?, ?>> combined = new ArrayList<>(base.size() + extra.length);
        combined.addAll(base);
        Arrays.stream(extra).filter(t -> t != null).forEach(combined::add);
        return combined;
    }
}
