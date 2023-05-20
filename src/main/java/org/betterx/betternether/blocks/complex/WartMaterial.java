package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleBlockOnlyMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.betternether.blocks.BlockWartRoots;
import org.betterx.betternether.blocks.BlockWartSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

public class WartMaterial extends RoofMaterial<WartMaterial> {
    public WartMaterial(String name, MaterialColor woodColor, MaterialColor planksColor) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super
                .createMaterialSlots()
                .add(AbstractSeed.create(BlockWartSeed::new))
                .add(SimpleBlockOnlyMaterialSlot.createBlockOnly(NetherSlots.ROOTS, BlockWartRoots::new));
    }


    public Block getRoot() {
        return getBlock(NetherSlots.ROOTS);
    }

    public Block getSeed() {
        return getBlock(NetherSlots.SEED);
    }
}
