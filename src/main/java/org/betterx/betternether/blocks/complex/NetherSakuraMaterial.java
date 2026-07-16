package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockNetherSakuraSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class NetherSakuraMaterial extends NetherWoodenMaterial<NetherSakuraMaterial> {
    public NetherSakuraMaterial() {
        super("nether_sakura", MapColor.COLOR_PINK, MapColor.COLOR_BROWN);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(Sapling.create(BlockNetherSakuraSapling::new, NetherSurvival.netherrack()));
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
