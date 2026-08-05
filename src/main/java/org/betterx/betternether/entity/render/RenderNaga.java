package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityNaga;
import org.betterx.betternether.entity.model.ModelNaga;
import org.betterx.betternether.registry.EntityRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class RenderNaga extends MobRenderer<EntityNaga, NagaRenderState, ModelNaga> {
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/naga.png"
    );

    public RenderNaga(EntityRendererProvider.Context ctx) {
        super(ctx, new ModelNaga(ctx.bakeLayer(EntityRenderRegistry.NAGA_MODEL)), 0.7F);
    }

    @Override
    public NagaRenderState createRenderState() {
        return new NagaRenderState();
    }

    @Override
    public void extractRenderState(EntityNaga entity, NagaRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.swimAmount = entity.getSwimAmount(partialTick);
        state.rollTooBig = entity.getFallFlyingTicks() > 4;
        state.isSwimming = entity.isVisuallySwimming();
        state.isMovingOnGround = entity.onGround()
                && (entity.getDeltaMovement().x != 0 || entity.getDeltaMovement().z != 0)
                && !entity.isPassenger();
    }

    @Override
    public Identifier getTextureLocation(NagaRenderState state) {
        return TEXTURE;
    }
}
