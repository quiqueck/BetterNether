package org.betterx.betternether.mixin.client;

import org.betterx.betternether.config.Configs;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class PlayerArmorMixin<T extends Avatar & ClientAvatarEntity>
        extends LivingEntityRenderer<T, AvatarRenderState, PlayerModel> {
    public PlayerArmorMixin(
            EntityRendererProvider.Context context,
            PlayerModel entityModel,
            float f
    ) {
        super(context, entityModel, f);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "<init>*", at = @At(value = "RETURN"))
    private void bcl_onInit(EntityRendererProvider.Context context, boolean bl, CallbackInfo info) {
        if (Configs.CLIENT.thinArmor.get()) {
            for (RenderLayer<AvatarRenderState, PlayerModel> feature : this.layers) {
                if (feature instanceof HumanoidArmorLayer) {
                    this.layers.remove(feature);
                    break;
                }
            }
            this.layers.add(
                    0,
                    new HumanoidArmorLayer<>(
                            this,
                            ArmorModelSet.bake(
                                    ModelLayers.PLAYER_SLIM_ARMOR,
                                    context.getModelSet(),
                                    part -> new PlayerModel(part, true)
                            ),
                            context.getEquipmentRenderer()
                    )
            );
        }
    }
}