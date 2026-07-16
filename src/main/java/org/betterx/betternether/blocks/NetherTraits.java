package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTrait;

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

    /** {@code base} plus the given extra traits, dropping any {@code null}. */
    @SafeVarargs
    public static List<BlockTrait<?, ?>> and(List<BlockTrait<?, ?>> base, BlockTrait<?, ?>... extra) {
        final List<BlockTrait<?, ?>> combined = new ArrayList<>(base.size() + extra.length);
        combined.addAll(base);
        Arrays.stream(extra).filter(t -> t != null).forEach(combined::add);
        return combined;
    }
}
