package org.betterx.betternether.mixin.client;


import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// TODO(1.21.7): render pipeline overhaul.
//  RenderType.create no longer takes (VertexFormat, VertexFormat.Mode, bufferSize, affectsCrumbling,
//  sortOnUpload, CompositeState); as of the 1.21.5 render-pipeline overhaul it is keyed by a
//  RenderPipeline. The former caller (RenderPhaseAccessor's custom additive firefly RenderType) has
//  already been migrated to a stock RenderType, so this invoker is currently unused. The signature is
//  updated to the new create(String, int, RenderPipeline, CompositeState) overload so the mixin still
//  applies; revisit if a custom RenderPipeline is reintroduced.
@Mixin(RenderType.class)
public interface RenderLayerMixin {
    @Invoker("create")
    static RenderType.CompositeRenderType callCreate(
            String string,
            int i,
            RenderPipeline renderPipeline,
            RenderType.CompositeState compositeState
    ) {
        throw new AssertionError();
    }
}
