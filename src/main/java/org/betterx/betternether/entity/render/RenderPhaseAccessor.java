package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BindGroupLayouts;
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
 * Modelled directly on vanilla's {@code RenderPipelines.EYES}/{@code ENERGY_SWIRL}: both are built from
 * the public {@code RenderPipelines.MATRICES_FOG_SNIPPET} + the {@code core/entity} shader, not from the
 * private {@code ENTITY_SNIPPET} that the pre-1.21.5 firefly type used, so a full custom pipeline is
 * reachable with public API only - {@code RenderSetup.builder}/{@code RenderType.create(String, RenderSetup)}
 * are public too. {@code EMISSIVE} + {@code NO_OVERLAY} + {@code NO_CARDINAL_LIGHTING} give the same
 * full-bright "glow" shading as those two.
 * <p>
 * 26.2 reworked the pipeline builder: {@code withSampler(String)} became
 * {@code withBindGroupLayout(BindGroupLayout)} and {@code withVertexFormat(VertexFormat, Mode)} split into
 * {@code withVertexBinding(int, VertexFormat)} + {@code withPrimitiveTopology(PrimitiveTopology)}. The
 * replacements used here - {@code BindGroupLayouts.SAMPLER0}, binding 0 of {@code DefaultVertexFormat
 * .ENTITY}, {@code PrimitiveTopology.QUADS} - are exactly what 26.2's own {@code RenderPipelines.EYES}
 * passes, verified against {@code javap -c RenderPipelines}.
 * <p>
 * The depth test is {@link CompareOp#GREATER_THAN_OR_EQUAL}, not the {@code LESS_THAN_OR_EQUAL} the 26.1
 * version used: 26.2 switched to a reversed-Z depth buffer, flipping both
 * {@code DepthStencilState.DEFAULT} (now {@code (GREATER_THAN_OR_EQUAL, true)}) and {@code EYES}. Like
 * vanilla {@code EYES} we keep the same test but do not write depth.
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
            .builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withLocation(BetterNether.C.mk("pipeline/firefly"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
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
