package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.AbstractSaplingSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockNetherSakuraSapling;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

public class NetherSakuraMaterial extends NetherWoodenMaterial<NetherSakuraMaterial> {
    public NetherSakuraMaterial() {
        super("nether_sakura", MaterialColor.COLOR_PINK, MaterialColor.COLOR_BROWN);
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(AbstractSaplingSlot.create(BlockNetherSakuraSapling::new));
    }

    public Block getSapling() {
        return getBlock(WoodSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
