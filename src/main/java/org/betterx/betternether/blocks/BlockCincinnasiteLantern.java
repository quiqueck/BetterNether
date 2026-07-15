package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockCincinnasiteLantern extends BlockBase implements BehaviourMetal {
    public BlockCincinnasiteLantern() {
        super(BlockBehaviour.Properties.ofFullCopy(NetherBlocks.CINCINNASITE_BLOCK).lightLevel(state -> 15));
    }
}
