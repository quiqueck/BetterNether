package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;

import java.util.List;

/**
 * Nether-specific trait bundles for the {@code List<BlockTrait<?, ?>>} that the
 * {@code NetherBlocks.register*} helpers take. The generic list combinators (of/and/concat) now live in
 * {@link TraitLists}; this class keeps only the mod's reusable behaviour bundles (compostable/plant/
 * sapling/seed/vine) that reproduce the retired {@code Behaviour*} markers as traits.
 */
public class NetherTraits {
    /**
     * {@code base} plus {@link CompostableBlockTrait} at bclib's default 0.1 chance.
     * <p>
     * Unlike the old {@code BehaviourCompostable} marker - which only ever produced the
     * {@code c:compostable} item tag at datagen, never a composter entry (see its javadoc) - the trait both
     * tags the item AND carries the composting chance as a runtime trait that {@code ComposterBlockMixin}
     * reads when the composter is filled, so the block actually composts.
     */
    public static List<BlockTrait<?, ?>> compostable(List<BlockTrait<?, ?>> base) {
        return TraitLists.and(base, CompostableBlockTrait.withDefault());
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
        return TraitLists.and(
                compostable(base),
                PlantLikeBlockTrait.withDefault(),
                // Grass/moss/fern/crop-style plants get NO mineable tag, matching vanilla
                // short_grass/fern/flowers/crops (and mirroring bclib PlantBlockTrait.compostableWithColor).
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
        return TraitLists.and(
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
        return TraitLists.and(
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
        return TraitLists.and(
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
}
