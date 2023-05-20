package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.AbstractSaplingSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockAnchorTreeSapling;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

public class AnchorTreeMaterial extends NetherWoodenMaterial<AnchorTreeMaterial> {

    public AnchorTreeMaterial() {
        super("anchor_tree", MaterialColor.COLOR_BLUE, MaterialColor.COLOR_GREEN);
    }


    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(AbstractSaplingSlot.create(BlockAnchorTreeSapling::new));
    }

    public Block getSapling() {
        return getBlock(WoodSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
