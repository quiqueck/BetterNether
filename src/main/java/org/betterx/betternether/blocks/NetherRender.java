package org.betterx.betternether.blocks;

import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTrait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The render layer a block is drawn with, as {@link ClientBlockTraits#RENDER_LAYER} traits to hand to a
 * block's definition at registration. Replaces the old {@code IRenderTypeable} interface plus the
 * {@code BetterNetherClient.registerRenderLayers()} registry walk: the layer now lives on the block
 * definition rather than in the class hierarchy, and wover applies it in {@code afterBlockRegistration}.
 * <p>
 * A block that is neither given one of these nor carries a {@code render_type} in its model renders as
 * SOLID, so every cutout/translucent block needs one.
 * <p>
 * These are methods rather than constants because {@link ClientBlockTraits#RENDER_LAYER} returns
 * {@code null} outside the client - the trait is client-only, and a constant would bake that null in at
 * class-init time. The lists returned here are already null-free, so they compose with
 * {@link NetherSurvival}'s lists and can be handed straight to the {@code register*} helpers.
 */
public class NetherRender {
    /** {@code RenderType.cutout()} - fully opaque or fully transparent pixels, no blending. */
    public static List<BlockTrait<?, ?>> cutout() {
        return only(ClientBlockTraits.RENDER_LAYER.cutout());
    }

    /** {@code RenderType.translucent()} - alpha-blended, partially transparent pixels. */
    public static List<BlockTrait<?, ?>> translucent() {
        return only(ClientBlockTraits.RENDER_LAYER.translucent());
    }

    /** {@link #cutout()} combined with further traits, e.g. a {@link NetherSurvival} rule. */
    public static List<BlockTrait<?, ?>> cutoutAnd(List<BlockTrait<?, ?>> more) {
        return combine(ClientBlockTraits.RENDER_LAYER.cutout(), more);
    }

    /** {@link #translucent()} combined with further traits, e.g. a {@link NetherSurvival} rule. */
    public static List<BlockTrait<?, ?>> translucentAnd(List<BlockTrait<?, ?>> more) {
        return combine(ClientBlockTraits.RENDER_LAYER.translucent(), more);
    }

    private static List<BlockTrait<?, ?>> only(BlockTrait<?, ?> trait) {
        // List.of would throw on the null a dedicated server gets back
        return trait == null ? List.of() : Collections.singletonList(trait);
    }

    private static List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> trait, List<BlockTrait<?, ?>> more) {
        if (trait == null) return more;
        final List<BlockTrait<?, ?>> combined = new ArrayList<>(more.size() + 1);
        combined.add(trait);
        combined.addAll(more);
        return combined;
    }
}
