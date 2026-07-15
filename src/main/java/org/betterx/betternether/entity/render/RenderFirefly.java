package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityFirefly;
import org.betterx.betternether.entity.model.ModelEntityFirefly;
import org.betterx.betternether.registry.EntityRenderRegistry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


@Environment(EnvType.CLIENT)
class FireflyGlowFeatureRenderer extends RenderLayer<FireflyRenderState, ModelEntityFirefly> {
    private static final int LIT = 15728880;
    private static final ResourceLocation TEXTURE = BetterNether.C.mk(
            "textures/entity/firefly.png"
    );

    public FireflyGlowFeatureRenderer(RenderLayerParent<FireflyRenderState, ModelEntityFirefly> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(
            PoseStack matrices,
            MultiBufferSource vertices,
            int light,
            FireflyRenderState state,
            float headYaw,
            float headPitch
    ) {
        ModelEntityFirefly model = this.getParentModel();

        RenderType renderLayer = RenderPhaseAccessor.getFirefly(TEXTURE);
        VertexConsumer vertexConsumer = vertices.getBuffer(renderLayer);

        int color = state.color;

        addViewAlignedGlow(matrices, vertexConsumer, color);

        // Temporarily make the glow part visible while this feature layer draws it;
        // it stays hidden in the main model pass.
        model.getGlowPart().visible = true;
        model.getGlowPart()
             .render(
                     matrices,
                     vertexConsumer,
                     light,
                     OverlayTexture.NO_OVERLAY,
                     color
             );
        model.getGlowPart()
             .render(
                     matrices,
                     vertexConsumer,
                     light,
                     OverlayTexture.NO_OVERLAY,
                     color
             );
        model.getGlowPart().visible = false;
    }

    private void addViewAlignedGlow(
            PoseStack matrices,
            VertexConsumer vertexConsumer,
            int color
    ) {
        matrices.pushPose();

			/* //Original transform
			matrixStack.translate(0, 0.125, 0);
			matrixStack.multiply(this.dispatcher.getRotation());
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));*/

        matrices.translate(0, 1.25, 0);

        //Get inverse rotation to make view-aligned
        Matrix3f normalMatrix = matrices.last().normal();
        normalMatrix.transpose();
//        Triple<Quaternion, Vector3f, Quaternion> trip = normalMatrix.svdDecompose();
//        matrices.mulPose(trip.getLeft());
        matrices.mulPose(normalMatrix.transpose(new Matrix3f()).getNormalizedRotation(new Quaternionf()));

        PoseStack.Pose entry = matrices.last();
        Matrix4f matrix4f = entry.pose();

        addVertex(matrix4f, entry, vertexConsumer, -1, -1, 0F, 0.5F, color);
        addVertex(matrix4f, entry, vertexConsumer, 1, -1, 1F, 0.5F, color);
        addVertex(matrix4f, entry, vertexConsumer, 1, 1, 1F, 1F, color);
        addVertex(matrix4f, entry, vertexConsumer, -1, 1, 0F, 1F, color);

        //emptyModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue,  1f);
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
    private static final ResourceLocation TEXTURE = BetterNether.C.mk(
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
    public ResourceLocation getTextureLocation(FireflyRenderState state) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(EntityFirefly entity, BlockPos blockPos) {
        return 15;
    }
}
