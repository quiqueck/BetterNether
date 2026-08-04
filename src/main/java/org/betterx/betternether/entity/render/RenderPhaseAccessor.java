package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Client-only holder for the firefly glow {@link RenderType}.
 * <p>
 * Ported to the 1.21.5+ {@link RenderPipeline} API. Everything that used to live in a hand-assembled
 * {@code RenderType.CompositeState} (shader, blend func, cull, write mask) is now expressed on the
 * pipeline; only texture/lightmap/overlay remain in the composite state.
 * <p>
 * Must never be touched from common code — it references client-only rendering classes.
 */
@Environment(EnvType.CLIENT)
public abstract class RenderPhaseAccessor {
    /**
     * The old shard was <em>named</em> {@code ALPHA_ADD_TRANSPARENCY}, but historically it called
     * {@code blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO)} — a regular alpha blend, never
     * actually additive. The originally additive-ish look came from {@code RENDERTYPE_EYES_SHADER}, which
     * was swapped for {@code RENDERTYPE_TRANSLUCENT_SHADER} in 2022 to fix rendering under the
     * Iris/Complementary shader packs.
     * <p>
     * We deliberately use {@link BlendFunction#LIGHTNING} ({@code SRC_ALPHA, ONE}) instead: a genuinely
     * additive glow (unlike the historical blend above), but scaled by the fragment's alpha first (unlike
     * plain {@code ADDITIVE}, {@code ONE, ONE}, which ignores alpha and flattens the texture's soft
     * circular falloff into a hard-edged bright disc).
     */
    private static final BlendFunction FIREFLY_BLEND = BlendFunction.LIGHTNING;

    /**
     * Modelled on vanilla's {@code RenderPipelines.ENTITY_TRANSLUCENT}, which is the closest match to the
     * old composite state: the {@code ENTITY_SNIPPET} supplies the {@code core/entity} shader, the
     * {@code NEW_ENTITY} vertex format and {@code Sampler0}/{@code Sampler2} (lightmap); {@code Sampler1}
     * is added for the overlay. It differs from {@code ENTITY_TRANSLUCENT} only in the blend function
     * (above) and in disabling depth writes.
     * <p>
     * Deliberately <em>not</em> registered via {@code RenderPipelines.register}: the GL device compiles and
     * caches pipelines lazily on first use ({@code GlRenderPass.setPipeline} -> {@code getOrCompilePipeline}),
     * so registration would only add precompilation at shader-reload time — at the cost of mutating a vanilla
     * static map and turning any compile failure into a hard "Failed to load required shader programs" crash
     * on every resource reload.
     */
    private static final RenderPipeline FIREFLY_PIPELINE = RenderPipeline
            .builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(BetterNether.C.mk("pipeline/firefly"))
            // matches the old RENDERTYPE_TRANSLUCENT_SHADER's `if (color.a < 0.1) discard;`
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            // overlay sampler, required by the OVERLAY shard in the composite state below
            .withSampler("Sampler1")
            .withBlend(FIREFLY_BLEND)
            // old: NO_CULL
            .withCull(false)
            // old: COLOR_WRITE (colour + alpha, no depth)
            .withColorWrite(true)
            .withDepthWrite(false)
            .build();

    private static final Function<ResourceLocation, RenderType> FIREFLY_RENDER_LAYER =
            Util.memoize(RenderPhaseAccessor::createFirefly);

    private static RenderType createFirefly(ResourceLocation texture) {
        RenderType.CompositeState compositeState = RenderType.CompositeState
                .builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                // old: createCompositeState(false) -> does not affect the entity outline
                .createCompositeState(false);

        // old: callCreate("firefly", NEW_ENTITY, QUADS, 256, false, true, state).
        // Vertex format/mode now come from the pipeline; RenderType.create is public, so the
        // RenderLayerMixin @Invoker is no longer needed.
        return RenderType.create(
                "firefly",
                256,
                false, // affectsCrumbling
                true,  // sortOnUpload
                FIREFLY_PIPELINE,
                compositeState
        );
    }

    public static RenderType getFirefly(ResourceLocation texture) {
        return FIREFLY_RENDER_LAYER.apply(texture);
    }
}
