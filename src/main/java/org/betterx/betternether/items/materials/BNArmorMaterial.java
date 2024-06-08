package org.betterx.betternether.items.materials;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.item.api.armor.CustomArmorMaterial;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

class BNArmorMaterials {
    public static final Holder<ArmorMaterial> CINCINNASITE = CustomArmorMaterial
            .start(BetterNether.C.mk("cincinnasite"))
            .defense(3, 5, 6, 3, 12)
            .enchantmentValue(20)
            .equipSound(SoundEvents.ARMOR_EQUIP_GOLD)
            .toughness(1.0f)
            .knockbackResistance(0.05f)
            .repairIngredientSupplier(() -> Ingredient.of(NetherItems.CINCINNASITE_INGOT))
            .buildAndRegister();

    public static final Holder<ArmorMaterial> NETHER_RUBY = CustomArmorMaterial
            .start(BetterNether.C.mk("nether_ruby"))
            .defense(3, 5, 7, 3, 18)
            .enchantmentValue(25)
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .toughness(1.4f)
            .knockbackResistance(0.2f)
            .repairIngredientSupplier(() -> Ingredient.of(NetherItems.NETHER_RUBY))
            .buildAndRegister();

    public static final Holder<ArmorMaterial> FLAMING_RUBY = CustomArmorMaterial
            .start(BetterNether.C.mk("flaming_ruby"))
            .defense(3, 6, 8, 3, 30)
            .enchantmentValue(30)
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .toughness(2.6f)
            .knockbackResistance(0.3f)
            .repairIngredientSupplier(() -> Ingredient.of(Blocks.SCULK_CATALYST))
            .buildAndRegister();
}