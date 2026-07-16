package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockWartRoots;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockWartSeed;
import org.betterx.betternether.blocks.complex.slots.AbstractSeed;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class WartMaterial extends RoofMaterial<WartMaterial> {
    public WartMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(name, woodColor, planksColor);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(AbstractSeed.create(BlockWartSeed::new, NetherSurvival.soulSand()))
                    .add(SimpleBlockSlot.blockOnly(NetherSlots.ROOTS, (set, props) -> new BlockWartRoots(props)));
    }

    public Block getRoot() {
        return getBlock(NetherSlots.ROOTS);
    }

    public Block getSeed() {
        return getBlock(NetherSlots.SEED);
    }
}
