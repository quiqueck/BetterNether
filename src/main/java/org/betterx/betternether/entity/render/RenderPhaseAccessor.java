package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

/**
 * Client-only holder for the firefly glow {@link RenderType}.
 * <p>
 * Modelled directly on vanilla's {@code RenderPipelines.EYES}, which in 26.3 is
 * {@code ENTITY_NO_LIGHTMAP_SNIPPET + EYES_SNIPPET} plus a colour-target and depth-stencil state. Both
 * snippets are public in 26.3, so the hand-rolled snippet chain the 26.1 version needed
 * ({@code MATRICES_FOG_SNIPPET} + {@code core/entity} + {@code Sampler0} + {@code ENTITY}/{@code QUADS} +
 * {@code EMISSIVE}/{@code NO_OVERLAY}/{@code NO_CARDINAL_LIGHTING}) collapses into exactly those two -
 * verified field-for-field against {@code javap -c RenderPipelines}. {@code RenderSetup.builder} /
 * {@code RenderType.create(String, RenderSetup)} are still public.
 * <p>
 * The depth test is {@link CompareOp#GREATER_THAN_OR_EQUAL}, not the {@code LESS_THAN_OR_EQUAL} the 26.1
 * version used: 26.3 switched to a reversed-Z depth buffer, and {@code LESS_THAN_OR_EQUAL} no longer
 * appears anywhere in vanilla's {@code RenderPipelines}. {@code DepthStencilState.DEFAULT} is
 * {@code (GREATER_THAN_OR_EQUAL, true)}; like vanilla {@code EYES} we keep the same test but do not write
 * depth.
 * <p>
 * The blend is {@link BlendFunction#LIGHTNING} ({@code SRC_ALPHA, ONE}), not {@code ADDITIVE}
 * ({@code ONE, ONE}): {@code ADDITIVE} ignores the fragment's alpha entirely, so the soft circular falloff
 * baked into the firefly texture's alpha channel got flattened into a hard-edged bright disc.
 * {@code LIGHTNING} still adds light on top of the scene (no darkening, stacks additively with itself) but
 * scales by alpha first, so the glow fades out the way the texture intends.
 * <p>
 * Must never be touched from common code — it references client-only rendering classes.
 */
@Environment(EnvType.CLIENT)
public abstract class RenderPhaseAccessor {
    private static final RenderPipeline FIREFLY_PIPELINE = RenderPipeline
            .builder(RenderPipelines.ENTITY_NO_LIGHTMAP_SNIPPET, RenderPipelines.EYES_SNIPPET)
            .withLocation(BetterNether.C.mk("pipeline/firefly"))
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .build();

    private static final Function<Identifier, RenderType> FIREFLY_RENDER_TYPE = Util.memoize(RenderPhaseAccessor::createFirefly);

    private static RenderType createFirefly(Identifier texture) {
        RenderSetup setup = RenderSetup
                .builder(FIREFLY_PIPELINE)
                .withTexture("Sampler0", texture)
                .sortOnUpload()
                .setOutline(OutlineProperty.NONE)
                .createRenderSetup();
        return RenderType.create("firefly", setup);
    }

    public static RenderType getFirefly(Identifier texture) {
        return FIREFLY_RENDER_TYPE.apply(texture);
    }
}
