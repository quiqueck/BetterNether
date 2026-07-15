package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityHydrogenJellyfish;
import org.betterx.betternether.entity.model.ModelEntityHydrogenJellyfish;
import org.betterx.betternether.registry.EntityRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RenderHydrogenJellyfish extends MobRenderer<EntityHydrogenJellyfish, HydrogenJellyfishRenderState, ModelEntityHydrogenJellyfish> {
    private static final ResourceLocation TEXTURE =
            BetterNether.C.mk("textures/entity/jellyfish.png");

    public RenderHydrogenJellyfish(EntityRendererProvider.Context ctx) {
        super(ctx, new ModelEntityHydrogenJellyfish(ctx.bakeLayer(EntityRenderRegistry.HYDROGEN_JELLYFISH_MODEL)), 1);
    }

    @Override
    public HydrogenJellyfishRenderState createRenderState() {
        return new HydrogenJellyfishRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(HydrogenJellyfishRenderState state) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(EntityHydrogenJellyfish entity, BlockPos pos) {
        return 15;
    }
}
