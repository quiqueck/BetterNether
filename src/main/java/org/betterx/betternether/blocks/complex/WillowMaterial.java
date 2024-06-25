package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SimpleBlockOnlyMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SimpleMaterialSlot;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.AbstractSaplingSlot;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betternether.blocks.BlockWillowBranch;
import org.betterx.betternether.blocks.BlockWillowSapling;
import org.betterx.betternether.blocks.BlockWillowTorch;
import org.betterx.betternether.blocks.BlockWillowTrunk;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class WillowMaterial extends RoofMaterial<WillowMaterial> {
    public final static String BLOCK_TORCH = "torch";


    public WillowMaterial() {
        super("willow", MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_RED);
        setFurnitureCloth(NetherBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    public WillowMaterial init() {
        return (WillowMaterial) super.init();
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(TrunkSlot.create(BlockWillowTrunk::new))
                    .add(AbstractSaplingSlot.create(BlockWillowSapling::new))
                    .add(SimpleBlockOnlyMaterialSlot.createBlockOnly(NetherSlots.BRANCH, BlockWillowBranch::new))
                    .add(SimpleMaterialSlot.createBlockItem(BLOCK_TORCH, BlockWillowTorch::new));
    }

    public Block getTrunk() {
        return getBlock(NetherSlots.TRUNK);
    }

    public Block getBranch() {
        return getBlock(NetherSlots.BRANCH);
    }

    public Block getSapling() {
        return getBlock(WoodSlots.SAPLING);
    }

    public Block getTorch() {
        return getBlock(BLOCK_TORCH);
    }
}
