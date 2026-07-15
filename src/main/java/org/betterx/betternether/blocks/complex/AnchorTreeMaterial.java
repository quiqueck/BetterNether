package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockAnchorTreeSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class AnchorTreeMaterial extends NetherWoodenMaterial<AnchorTreeMaterial> {

    public AnchorTreeMaterial() {
        super("anchor_tree", MapColor.COLOR_BLUE, MapColor.COLOR_GREEN);
        this.setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Sapling.create(BlockAnchorTreeSapling::new));
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
