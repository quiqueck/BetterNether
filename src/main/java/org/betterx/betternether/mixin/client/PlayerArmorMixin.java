package org.betterx.betternether.mixin.client;

import org.betterx.betternether.config.Configs;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Makes players use the slim armor geometry when {@code thin_armor} is enabled.
 * <p>
 * We only swap the model layers vanilla bakes its armor from, instead of replacing the whole
 * {@link net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer}. That way the armor models are still
 * created by vanilla's own factory lambda, which other mods hook into: 3D Skin Layers wraps the
 * {@code new PlayerModel(...)} in that lambda to mark armor models as "ignored". A replacement layer built from
 * our own lambda escaped that marker, so 3D Skin Layers attached its voxel skin meshes to the armor (#313).
 */
@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class PlayerArmorMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ArmorModelSet;bake(Lnet/minecraft/client/renderer/entity/ArmorModelSet;Lnet/minecraft/client/model/geom/EntityModelSet;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/entity/ArmorModelSet;"
            ),
            index = 0
    )
    private ArmorModelSet<ModelLayerLocation> bn_useThinArmorLayers(ArmorModelSet<ModelLayerLocation> locations) {
        return Configs.CLIENT.thinArmor.get() ? ModelLayers.PLAYER_SLIM_ARMOR : locations;
    }
}
