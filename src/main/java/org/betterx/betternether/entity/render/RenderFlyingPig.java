package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityFlyingPig;
import org.betterx.betternether.entity.model.ModelEntityFlyingPig;
import org.betterx.betternether.registry.EntityRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class RenderFlyingPig extends MobRenderer<EntityFlyingPig, FlyingPigRenderState, ModelEntityFlyingPig> {
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/flying_pig.png"
    );
    private static final Identifier TEXTURE_WARTED = BetterNether.C.mk(
            "textures/entity/flying_pig_warted.png"
    );

    public RenderFlyingPig(EntityRendererProvider.Context ctx) {
        super(ctx, new ModelEntityFlyingPig(ctx.bakeLayer(EntityRenderRegistry.FLYING_PIG_MODEL)), 0.6F);
    }

    @Override
    public FlyingPigRenderState createRenderState() {
        return new FlyingPigRenderState();
    }

    @Override
    public void extractRenderState(EntityFlyingPig entity, FlyingPigRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isRoosting = entity.isRoosting();
        state.isWarted = entity.isWarted();
    }

    @Override
    public Identifier getTextureLocation(FlyingPigRenderState state) {
        return state.isWarted ? TEXTURE_WARTED : TEXTURE;
    }
}
