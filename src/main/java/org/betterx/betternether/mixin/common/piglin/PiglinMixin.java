package org.betterx.betternether.mixin.common.piglin;

import org.betterx.betternether.items.complex.NetherSet;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.complex.api.equipment.ArmorSlot;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
public abstract class PiglinMixin {
    @Shadow
    protected abstract void maybeWearArmor(
            EquipmentSlot equipmentSlot,
            ItemStack itemStack,
            RandomSource randomSource
    );

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
        //Piglins will now also consider to wear BetterNether armor
        if (((Piglin) (Object) this).isAdult()) {
            int random = randomSource.nextInt(100);
            NetherSet set = null;

            if (random < 2) set = NetherItems.FLAMING_RUBY_SET;
            else if (random < 10) set = NetherItems.NETHER_RUBY_SET;
            else if (random < 30) set = NetherItems.CINCINNASITE_SET;

            if (set != null) {
                this.maybeWearArmor(EquipmentSlot.HEAD, new ItemStack((Item) set.get(ArmorSlot.HELMET_SLOT)), randomSource);
                this.maybeWearArmor(EquipmentSlot.CHEST, new ItemStack((Item) set.get(ArmorSlot.CHESTPLATE_SLOT)), randomSource);
                this.maybeWearArmor(EquipmentSlot.LEGS, new ItemStack((Item) set.get(ArmorSlot.LEGGINGS_SLOT)), randomSource);
                this.maybeWearArmor(EquipmentSlot.FEET, new ItemStack((Item) set.get(ArmorSlot.BOOTS_SLOT)), randomSource);

                ci.cancel();
            }
        }
    }
}
