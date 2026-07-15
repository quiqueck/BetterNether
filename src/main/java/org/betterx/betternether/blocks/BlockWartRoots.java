package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockWartRoots extends BlockBase implements AddMineableAxe {
    public BlockWartRoots() {
        super(Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK));
        this.setDropItself(false);
    }

    public BlockWartRoots(BlockBehaviour.Properties properties) {
        super(properties);
        this.setDropItself(false);
    }
}
