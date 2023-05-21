package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.blocks.BaseBookshelfBlock;
import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.Bookshelf;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;

public class VanillaNetherWood extends VanillaFallback<VanillaNetherWood> {
    public VanillaNetherWood(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
                WoodSlots.CRAFTING_TABLE,
                new Bookshelf() {
                    @Override
                    protected @NotNull Block createBlock(
                            WoodenComplexMaterial parentMaterial,
                            BlockBehaviour.Properties settings
                    ) {
                        return new BaseBookshelfBlock.WithVanillaWood(parentMaterial.getBlock(WoodSlots.PLANKS));
                    }
                },
                WoodSlots.COMPOSTER,
                WoodSlots.CHEST,
                WoodSlots.BARREL,
                WoodSlots.LADDER,
                WoodSlots.BOAT,
                WoodSlots.CHEST_BOAT,
                NetherSlots.TABURET,
                NetherSlots.BAR_STOOL,
                NetherSlots.CHAIR
        );
    }
}
