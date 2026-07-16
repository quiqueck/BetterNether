package org.betterx.betternether.mixin.client;


import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Retained for API compatibility only — currently unused.
//
// Before the 1.21.5 render-pipeline overhaul, RenderType.create was not public and took
// (VertexFormat, VertexFormat.Mode, bufferSize, affectsCrumbling, sortOnUpload, CompositeState);
// RenderPhaseAccessor's custom firefly RenderType reached it through this invoker. As of 1.21.5
// create is keyed by a RenderPipeline and is public, so RenderPhaseAccessor now calls it directly.
// The invoker is kept (pointing at the current overload, so the mixin still applies) because it is
// listed in betternether.mixins.client.json; drop both together if it stays unused.
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
