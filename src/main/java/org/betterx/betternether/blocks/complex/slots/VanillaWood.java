package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class VanillaWood extends VanillaFallback<VanillaWood> {
    public VanillaWood(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
                WoodSlots.TABURET,
                WoodSlots.BAR_STOOL,
                WoodSlots.CHAIR
        );
    }

    public static VanillaWood create(String baseName, Block clothMaterial) {
        Block plank = getVanillaBlock(baseName, WoodSlots.PLANKS.suffix);
        return new VanillaWood(baseName, plank.defaultMapColor(), plank.defaultMapColor())
                .setFurnitureCloth(clothMaterial)
                .init();
    }
}

