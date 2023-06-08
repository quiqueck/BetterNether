package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.Planks;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Stem;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.Nullable;

public class NetherMushroomMaterial extends NetherWoodenMaterial<NetherMushroomMaterial> {
    public NetherMushroomMaterial() {
        super("nether_mushroom", MapColor.TERRACOTTA_WHITE, MapColor.COLOR_LIGHT_GRAY);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .remove(WoodSlots.LOG)
                    .remove(WoodSlots.BARK)
                    .remove(WoodSlots.STRIPPED_LOG)
                    .remove(WoodSlots.STRIPPED_BARK)
                    .add(new Stem() {
                        @Override
                        protected @Nullable void makeRecipe(ComplexMaterial material, ResourceLocation id) {
                        }
                    })
                    .replace(new Planks() {
                        @Override
                        protected @Nullable void makeRecipe(ComplexMaterial material, ResourceLocation id) {
                            BCLRecipeBuilder.crafting(id, material.getBlock(WoodSlots.PLANKS))
                                            .setOutputCount(4)
                                            .shapeless()
                                            .addMaterial('#', material.getBlock(NetherSlots.STEM))
                                            .setGroup("planks")
                                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                                            .build();
                        }
                    })
                ;
    }

    @Override
    public @Nullable <M extends ComplexMaterial> Block getBlock(MaterialSlot<M> key) {
        if (key.suffix.equals(WoodSlots.STRIPPED_LOG.suffix))
            return getStem();
        return super.getBlock(key);
    }

    public Block getStem() {
        return getBlock(NetherSlots.STEM);
    }
}
