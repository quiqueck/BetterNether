package org.betterx.betternether.mixin.common.piglin;

import org.betterx.betternether.config.Configs;
import org.betterx.betternether.items.materials.BNArmorTiers;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ArmorMaterial;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @WrapOperation(
            method = "isWearingGold",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private static boolean bn_isWearingGold(
            Holder<ArmorMaterial> instance,
            Holder<ArmorMaterial> tHolder,
            Operation<Boolean> original
    ) {
        //Piglins will now also consider BetterNether armor materials as gold armor
        return original.call(instance, tHolder) ||
                (Configs.GAME_RULES.piglinIgnoreNetherArmor.get() && (instance.is(BNArmorTiers.CINCINNASITE.armorMaterial) || instance.is(BNArmorTiers.NETHER_RUBY.armorMaterial) || instance.is(BNArmorTiers.FLAMING_RUBY.armorMaterial)));
    }
}
