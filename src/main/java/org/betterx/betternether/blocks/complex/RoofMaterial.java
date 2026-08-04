package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.complex.slots.Roof;
import org.betterx.betternether.blocks.complex.slots.RoofSlab;
import org.betterx.betternether.blocks.complex.slots.RoofStairs;
import de.ambertation.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.material.MapColor;

public class RoofMaterial<T extends RoofMaterial<T>> extends NetherWoodenMaterial<T> {
    public RoofMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Roof.SLOT)
                    .add(RoofStairs.SLOT)
                    .add(RoofSlab.SLOT);
    }
}
