package org.betterx.betternether.items.complex;

import org.betterx.bclib.items.complex.EquipmentDescription;
import org.betterx.bclib.items.complex.EquipmentSlot;
import org.betterx.bclib.recipes.CraftingRecipeBuilder;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

public class DiamondDescriptor<I extends Item> extends EquipmentDescription<I> {
    Item base;

    public DiamondDescriptor(EquipmentSlot slot, Item base, Function<Tier, I> creator) {
        super(slot, creator);
        this.base = base;
    }

    @Override
    protected boolean buildRecipe(Item tool, ItemLike stick, CraftingRecipeBuilder builder) {
        return super.buildRecipe(tool, stick, builder);
    }

    //    @Override
//    protected boolean buildRecipe(Item tool, ItemLike stick, CraftingRecipeBuilder builder) {
//        builder.addMaterial('P', base);
//        builder.addMaterial('#', Items.DIAMOND);
//        if (tool instanceof PickaxeItem) {
//            builder.setShape("#P#");
//        } else if (tool instanceof AxeItem) {
//            builder.setShape(" #", "#P");
//        } else if (tool instanceof HoeItem) {
//            builder.setShape(" #", "#P");
//        } else if (tool instanceof ShovelItem) {
//            builder.setShape("#", "P");
//        } else if (tool instanceof SwordItem) {
//            builder.setShape(" #", "#P");
//        } else return true;
//
//        return false;
//    }
}
