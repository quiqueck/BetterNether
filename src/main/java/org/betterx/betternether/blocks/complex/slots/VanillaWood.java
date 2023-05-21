package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

public class VanillaWood extends VanillaFallback<VanillaWood> {
    public VanillaWood(
            String name,
            MaterialColor woodColor,
            MaterialColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
                NetherSlots.TABURET,
                NetherSlots.BAR_STOOL,
                NetherSlots.CHAIR
        );
    }

    public static VanillaWood create(String baseName) {
        Block plank = getVanillaBlock(baseName, WoodSlots.PLANKS.suffix);
        return new VanillaWood(baseName, plank.defaultMaterialColor(), plank.defaultMaterialColor()).init();
    }
}

