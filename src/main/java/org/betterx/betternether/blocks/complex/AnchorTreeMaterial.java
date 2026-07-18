package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.BlockAnchorTreeSapling;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.block.WeightedBark;
import org.betterx.bclib.trait.block.WeightedLog;
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
        // Restore the randomized-log look: generate the weighted anchor_tree log/bark blockstates (base + _2 + _3,
        // for both the normal and stripped slots) instead of hand-authoring them. anchor_tree uses the standard
        // _side/_top texture layout, so no texture overrides are needed.
        return super.createDefaultDefinitions()
                    .replace(new WeightedLog(true, new int[]{1, 1, 1}))
                    .replace(new WeightedBark(true, new int[]{1, 1, 1}))
                    .replace(new WeightedLog(false, new int[]{1, 1, 1}))
                    .replace(new WeightedBark(false, new int[]{1, 1, 1}))
                    .add(Sapling.create(BlockAnchorTreeSapling::new, NetherSurvival.netherrack()));
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }
}
