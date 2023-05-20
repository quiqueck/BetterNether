package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;

import net.minecraft.world.level.material.MaterialColor;

public class RoofMaterial<T extends RoofMaterial<T>> extends NetherWoodenMaterial<T> {
    public RoofMaterial(String name, MaterialColor woodColor, MaterialColor planksColor) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(NetherSlots.ROOF)
                    .add(NetherSlots.ROOF_STAIRS)
                    .add(NetherSlots.ROOF_SLAB);
    }
}
