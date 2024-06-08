package org.betterx.betternether.mixin.common.obsidian_breaker;

import org.betterx.betternether.enchantments.ObsidianBreaker;
import org.betterx.betternether.registry.NetherEnchantments;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "createAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"))
    private static AttributeSupplier.Builder wover_test_createAttributes(Operation<AttributeSupplier.Builder> original) {
        return original.call().add(NetherEnchantments.OBSIDIAN_BLOCK_BREAK_SPEED);
    }

    @WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    float wover_test_getDestroySpeed(Inventory instance, BlockState blockState, Operation<Float> original) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        return ObsidianBreaker.modifyObsidianBreakerSpeed(blockState, original.call(instance, blockState), entity);
    }

}
