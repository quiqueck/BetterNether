package org.betterx.betternether.entity.render;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.entity.EntityJungleSkeleton;
import org.betterx.betternether.entity.model.ModelJungleSkeleton;
import org.betterx.betternether.registry.EntityRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class RenderJungleSkeleton extends AbstractSkeletonRenderer<EntityJungleSkeleton, JungleSkeletonRenderState> {
    private static final Identifier TEXTURE = BetterNether.C.mk(
            "textures/entity/jungle_skeleton.png"
    );

    public RenderJungleSkeleton(EntityRendererProvider.Context ctx) {
        super(
                ctx,
                ModelLayers.SKELETON_ARMOR,
                new ModelJungleSkeleton(ctx.bakeLayer(EntityRenderRegistry.JUNGLE_SKELETON_MODEL))
        );
    }

    @Override
    public JungleSkeletonRenderState createRenderState() {
        return new JungleSkeletonRenderState();
    }

    @Override
    public Identifier getTextureLocation(JungleSkeletonRenderState state) {
        return TEXTURE;
    }
}
