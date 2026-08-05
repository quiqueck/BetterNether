package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityNagaProjectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class RenderNagaProjectile extends EntityRenderer<EntityNagaProjectile, EntityRenderState> {
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/naga_projectile.png"
    );
    // 26.1: RenderType factories moved to RenderTypes; entityCutout is the no-cull variant
    // (its pipeline sets withCull(false)), replacing the removed entityCutoutNoCull.
    private static final RenderType LAYER = RenderTypes.entityCutout(TEXTURE);

    public RenderNagaProjectile(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLightLevel(EntityNagaProjectile entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(
            EntityRenderState state,
            PoseStack matrixStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        int frame = (int) (System.currentTimeMillis() / 150) & 3;
        float start = frame * 0.25F;
        float end = start + 0.25F;
        final int light = state.lightCoords;
        matrixStack.pushPose();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.mulPose(camera.orientation);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        submitNodeCollector.submitCustomGeometry(matrixStack, LAYER, (pose, buffer) -> {
            vertex(buffer, pose, light, 0.0F, 0, 0, end);
            vertex(buffer, pose, light, 1.0F, 0, 1, end);
            vertex(buffer, pose, light, 1.0F, 1, 1, start);
            vertex(buffer, pose, light, 0.0F, 1, 0, start);
        });
        matrixStack.popPose();
        super.submit(state, matrixStack, submitNodeCollector, camera);
    }

    private static void vertex(
            VertexConsumer vertexConsumer,
            PoseStack.Pose pose,
            int i,
            float f,
            int j,
            float u,
            float v
    ) {
        vertexConsumer.addVertex(pose, f - 0.5F, (float) j - 0.25F, 0.0F)
                      .setColor(255, 255, 255, 255)
                      .setUv(u, v)
                      .setOverlay(OverlayTexture.NO_OVERLAY)
                      .setLight(i)
                      .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
