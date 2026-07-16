package org.betterx.betternether.mixin.common.piglin;

import org.betterx.betternether.config.Configs;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Makes piglins ignore entities wearing BetterNether armor (Cincinnasite / Nether Ruby / Flaming Ruby).
 * <p>
 * Up to 1.21.1 this was done by hooking {@code PiglinAi#isWearingGold}, which inspected the armor material.
 * Since 1.21.2 that method is gone and the decision is made by {@link PiglinAi#isWearingSafeArmor(LivingEntity)},
 * which only checks the {@code minecraft:piglin_safe_armor} item tag. The tag alone cannot express the
 * {@code piglinIgnoreNetherArmor} config option (a tag is always on), so the check is re-implemented here
 * instead of being moved into datagen.
 */
@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    /**
     * Lazily built so that referencing this mixin does not force {@link NetherItems} to initialize early.
     */
    @Unique
    private static Set<Item> bn_netherArmor = null;

    @Unique
    private static boolean bn_isNetherArmor(Item item) {
        Set<Item> armor = bn_netherArmor;
        if (armor == null) {
            armor = new HashSet<>();
            Collections.addAll(armor, NetherItems.CINCINNASITE_SET.getArmorPieces());
            Collections.addAll(armor, NetherItems.NETHER_RUBY_SET.getArmorPieces());
            Collections.addAll(armor, NetherItems.FLAMING_RUBY_SET.getArmorPieces());
            bn_netherArmor = armor;
        }
        return armor.contains(item);
    }

    @Inject(method = "isWearingSafeArmor", at = @At("HEAD"), cancellable = true)
    private static void bn_isWearingSafeArmor(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.GAME_RULES.piglinIgnoreNetherArmor.get()) return;

        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
            if (bn_isNetherArmor(livingEntity.getItemBySlot(slot).getItem())) {
                cir.setReturnValue(true);
                return;
            }
        }
        // fall through to vanilla so the piglin_safe_armor tag still applies
    }
}
