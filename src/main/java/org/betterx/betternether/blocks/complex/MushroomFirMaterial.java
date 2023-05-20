package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.AbstractSaplingSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockMushroomFir;
import org.betterx.betternether.blocks.BlockMushroomFirSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

public class MushroomFirMaterial extends NetherWoodenMaterial<MushroomFirMaterial> {
    public MushroomFirMaterial() {
        super("mushroom_fir", MaterialColor.COLOR_BLUE, MaterialColor.COLOR_BLUE);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(TrunkSlot.create(BlockMushroomFir::new))
                    .add(AbstractSaplingSlot.create(BlockMushroomFirSapling::new))
                    .add(NetherSlots.STEM);
    }

    public Block getStem() {
        return getBlock(NetherSlots.STEM);
    }

    public Block getSapling() {
        return getBlock(WoodSlots.SAPLING);
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }
}