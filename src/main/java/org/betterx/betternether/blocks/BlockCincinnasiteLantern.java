package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockCincinnasiteLantern extends BlockBase {
    public BlockCincinnasiteLantern(BlockBehaviour.Properties settings) {
        super(settings.lightLevel(state -> 15));
    }
}
