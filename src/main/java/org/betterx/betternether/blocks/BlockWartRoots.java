package org.betterx.betternether.blocks;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockWartRoots extends Block {
    public BlockWartRoots() {
        super(Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK));
    }

    public BlockWartRoots(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
