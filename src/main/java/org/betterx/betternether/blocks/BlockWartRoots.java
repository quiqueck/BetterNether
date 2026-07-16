package org.betterx.betternether.blocks;


import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockWartRoots extends BlockBase {
    public BlockWartRoots() {
        super(Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK));
        this.setDropItself(false);
    }

    public BlockWartRoots(BlockBehaviour.Properties properties) {
        super(properties);
        this.setDropItself(false);
    }
}
