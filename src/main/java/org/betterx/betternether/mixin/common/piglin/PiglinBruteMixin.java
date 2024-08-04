package org.betterx.betternether.mixin.common.piglin;

import org.betterx.betternether.config.Configs;
import org.betterx.betternether.items.complex.NetherSet;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.complex.api.equipment.ArmorSlot;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinBrute.class)
public class PiglinBruteMixin {
    @Inject(
            method = "populateDefaultEquipmentSlots",
            at = @At("HEAD"),
            cancellable = true
    )
    void bn_populateDefaultEquipmentSlots(
            RandomSource randomSource,
            DifficultyInstance difficultyInstance,
            CallbackInfo ci
    ) {
        if (Configs.GAME_RULES.piglinWearNetherArmor.get()) {
            //Piglin Brutes will now also consider to wear a BetterNether helmet
            int random = randomSource.nextInt(100);
            NetherSet set = null;

            if (random < 2) set = NetherItems.FLAMING_RUBY_SET;
            else if (random < 10) set = NetherItems.NETHER_RUBY_SET;
            else if (random < 30) set = NetherItems.CINCINNASITE_SET;

            if (set != null) {
                ((PiglinBrute) (Object) this).setItemSlot(EquipmentSlot.HEAD, new ItemStack((Item) set.get(ArmorSlot.HELMET_SLOT)));

                ci.cancel();
            }
        }
    }
}
