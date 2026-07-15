package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockMushroomFir;
import org.betterx.betternether.blocks.BlockMushroomFirSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.Stem;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class MushroomFirMaterial extends NetherWoodenMaterial<MushroomFirMaterial> {
    public MushroomFirMaterial() {
        super("mushroom_fir", MapColor.COLOR_BLUE, MapColor.COLOR_BLUE);
        this.setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(TrunkSlot.create(BlockMushroomFir::new))
                    .add(Sapling.create(BlockMushroomFirSapling::new))
                    .add(Stem.SLOT);
    }

    public Block getStem() {
        return getBlock(NetherSlots.STEM);
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }
}
