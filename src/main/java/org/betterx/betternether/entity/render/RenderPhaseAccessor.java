package org.betterx.betternether.entity.render;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;


public abstract class RenderPhaseAccessor {
    // TODO(1.21.7): render pipeline overhaul.
    //  The original firefly glow used a hand-built RenderType.CompositeState with a
    //  custom additive TransparencyStateShard + RENDERTYPE_TRANSLUCENT_SHADER, created
    //  through RenderLayerMixin.callCreate. All of that (GlStateManager blend funcs,
    //  RenderStateShard.ShaderStateShard/TransparencyStateShard, ShaderInstance/Program)
    //  was removed in the 1.21.5 render-pipeline overhaul and replaced by RenderPipeline.
    //  Until the custom additive pipeline is ported, fall back to the stock emissive
    //  translucent entity RenderType, which is the closest clean equivalent (glowing,
    //  translucent, no-cull). This preserves the glow visually; the additive blend is lost.
    private static final java.util.function.Function<ResourceLocation, RenderType> FIREFLY_RENDER_LAYER =
            Util.memoize((ResourceLocation texture) -> RenderType.entityTranslucentEmissive(texture));

    public static RenderType getFirefly(ResourceLocation texture) {
        return FIREFLY_RENDER_LAYER.apply(texture);
    }
}
