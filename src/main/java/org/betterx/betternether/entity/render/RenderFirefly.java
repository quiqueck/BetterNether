package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityFirefly;
import org.betterx.betternether.entity.model.ModelEntityFirefly;
import org.betterx.betternether.registry.EntityRenderRegistry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


@Environment(EnvType.CLIENT)
class FireflyGlowFeatureRenderer extends RenderLayer<FireflyRenderState, ModelEntityFirefly> {
    private static final int LIT = 15728880;
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/firefly.png"
    );

    public FireflyGlowFeatureRenderer(RenderLayerParent<FireflyRenderState, ModelEntityFirefly> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            FireflyRenderState state,
            float yRot,
            float xRot
    ) {
        ModelEntityFirefly model = this.getParentModel();

        RenderType renderLayer = RenderPhaseAccessor.getFirefly(TEXTURE);
        int color = state.color;

        // 1) view-aligned billboard glow halo
        submitViewAlignedGlow(poseStack, submitNodeCollector, renderLayer, color);

        // 2) the (slightly inflated) glow cube, drawn emissively. The part is hidden in the main
        //    model pass (glow.visible == false) and the visibility flag is only read at draw time
        //    in the new deferred submit pipeline, so we flip it *inside* the draw-time lambda: the
        //    main pass (a different render batch) never observes it as visible.
        final ModelPart glowPart = model.getGlowPart();
        submitNodeCollector.submitCustomGeometry(poseStack, renderLayer, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.mulPose(pose.pose());
            final boolean wasVisible = glowPart.visible;
            glowPart.visible = true;
            glowPart.render(local, buffer, lightCoords, OverlayTexture.NO_OVERLAY, color);
            glowPart.render(local, buffer, lightCoords, OverlayTexture.NO_OVERLAY, color);
            glowPart.visible = wasVisible;
        });
    }

    private void submitViewAlignedGlow(
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            RenderType renderType,
            int color
    ) {
        matrices.pushPose();

        matrices.translate(0, 1.25, 0);

        // The incoming pose already carries the firefly's own facing rotation (applied by
        // MobRenderer). Cancel it out first - for a pure rotation the normal matrix equals
        // the rotation itself, so its transpose is the inverse - then rotate by the camera's
        // orientation so the quad billboards to the viewer regardless of how the firefly is facing.
        Matrix3f entityRotation = new Matrix3f(matrices.last().normal());
        matrices.mulPose(entityRotation.transpose().getNormalizedRotation(new Quaternionf()));

        matrices.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().camera.rotation());
        matrices.mulPose(Axis.YP.rotationDegrees(180.0F));

        // The collector snapshots a copy of matrices.last(), so popping afterwards is safe.
        submitNodeCollector.submitCustomGeometry(matrices, renderType, (pose, buffer) -> {
            Matrix4f matrix4f = pose.pose();
            addVertex(matrix4f, pose, buffer, -1, -1, 0F, 0.5F, color);
            addVertex(matrix4f, pose, buffer, 1, -1, 1F, 0.5F, color);
            addVertex(matrix4f, pose, buffer, 1, 1, 1F, 1F, color);
            addVertex(matrix4f, pose, buffer, -1, 1, 0F, 1F, color);
        });

        matrices.popPose();
    }

    public static void addVertex(
            Matrix4f matrix4f,
            PoseStack.Pose matrix3f,
            VertexConsumer vertexConsumer,
            float posX,
            float posY,
            float u,
            float v,
            int color
    ) {
        vertexConsumer
                .addVertex(matrix4f, posX, posY, 0)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LIT)
                .setNormal(matrix3f, 0, 1, 0);
    }
}

@Environment(EnvType.CLIENT)
public class RenderFirefly extends MobRenderer<EntityFirefly, FireflyRenderState, ModelEntityFirefly> {
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/firefly.png"
    );

    public RenderFirefly(EntityRendererProvider.Context ctx) {
        super(ctx, new ModelEntityFirefly(ctx.bakeLayer(EntityRenderRegistry.FIREFLY_MODEL)), 0);

        this.addLayer(new FireflyGlowFeatureRenderer(this));
    }

    @Override
    public FireflyRenderState createRenderState() {
        return new FireflyRenderState();
    }

    @Override
    public void extractRenderState(EntityFirefly entity, FireflyRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.color = entity.getColor();
    }

    @Override
    public Identifier getTextureLocation(FireflyRenderState state) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(EntityFirefly entity, BlockPos blockPos) {
        return 15;
    }
}
