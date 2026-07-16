package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockWillowBranch;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockWillowSapling;
import org.betterx.betternether.blocks.BlockWillowTorch;
import org.betterx.betternether.blocks.BlockWillowTrunk;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.sets.api.blocks.SlotMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class WillowMaterial extends RoofMaterial<WillowMaterial> {
    public WillowMaterial() {
        super("willow", MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_RED);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return super.createDefaultDefinitions()
                    .add(TrunkSlot.create(BlockWillowTrunk::new))
                    .add(Sapling.create(BlockWillowSapling::new, NetherSurvival.netherGround()))
                    .add(SimpleBlockSlot.blockOnly(
                            NetherSlots.BRANCH,
                            (set, props) -> new BlockWillowBranch(props),
                            NetherRender.cutout()
                    ))
                    .add(SimpleBlockSlot.withItem(
                            NetherSlots.TORCH,
                            (set, props) -> new BlockWillowTorch(props),
                            NetherRender.cutout()
                    ));
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }

    public Block getBranch() {
        return getBlock(NetherSlots.BRANCH);
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public Block getTorch() {
        return getBlock(NetherSlots.TORCH);
    }
}
