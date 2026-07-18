package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockWillowBranch;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.NetherTraits;
import org.betterx.betternether.blocks.BlockWillowSapling;
import org.betterx.betternether.blocks.BlockWillowTorch;
import org.betterx.betternether.blocks.BlockWillowTrunk;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.blocks.complex.slots.SimpleBlockSlot;
import org.betterx.betternether.blocks.complex.slots.TrunkSlot;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.bclib.trait.block.WeightedBark;
import org.betterx.bclib.trait.block.WeightedLog;
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
                    // Restore the randomized-log look: generate the weighted willow_log/willow_bark blockstates
                    // (base + _2) instead of hand-authoring them. willow's log uses the willow_bark texture on its
                    // sides (there is no willow_log_side), so the base model textures are supplied explicitly.
                    .replace(new WeightedLog(
                            true, new int[]{1, 1},
                            BetterNether.C.mk("block/willow_bark"),
                            BetterNether.C.mk("block/willow_log_top")
                    ))
                    .replace(new WeightedBark(
                            true, new int[]{1, 1},
                            BetterNether.C.mk("block/willow_bark"),
                            null
                    ))
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
                            // Replaces BlockWillowTorch's BehaviourCompostable marker, which only tagged the
                            // item and never registered a composter entry.
                            NetherTraits.compostable(NetherRender.cutout())
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
