package org.betterx.betternether.items.materials;

import org.betterx.bclib.items.complex.SmithingSet;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherTemplates;
import org.betterx.wover.item.api.armor.CustomArmorMaterial;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.SmithingTemplateItem;
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

    //NetherTemplates.FLAMING_RUBY_TEMPLATE
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

public enum BNArmorMaterial implements SmithingSet {
    CINCINNASITE(BNArmorMaterials.CINCINNASITE),
    NETHER_RUBY(BNArmorMaterials.NETHER_RUBY),
    FLAMING_RUBY(
            BNArmorMaterials.FLAMING_RUBY,
            NetherTemplates.FLAMING_RUBY_TEMPLATE
    );
	/* Vanilla Settings
	LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(Items.LEATHER)),
    CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, () -> Ingredient.of(Items.IRON_INGOT)),
    IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.of(Items.IRON_INGOT)),
    GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0f, 0.0f, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, () -> Ingredient.of(Items.DIAMOND)),
    TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, () -> Ingredient.of(Items.SCUTE)),
    NETHERITE("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, () -> Ingredient.of(Items.NETHERITE_INGOT));
	 */

    public final Holder<ArmorMaterial> material;
    public final SmithingTemplateItem smithingTemplateItem;

    BNArmorMaterial(
            Holder<ArmorMaterial> material
    ) {
        this(material, null);
    }

    BNArmorMaterial(
            Holder<ArmorMaterial> material,
            SmithingTemplateItem smithingTemplateItem
    ) {
        this.material = material;
        this.smithingTemplateItem = smithingTemplateItem;
    }

    public boolean is(Holder<ArmorMaterial> mat) {
        return mat.unwrapKey().map(this.material::is).orElse(false);
    }

    public Holder<ArmorMaterial> getMaterial() {
        return material;
    }

    @Override
    public SmithingTemplateItem getSmithingTemplateItem() {
        return smithingTemplateItem;
    }
}
