package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;

import net.minecraft.world.level.block.Blocks;

public class BlockWartRoots extends BlockBase implements AddMineableAxe {
    public BlockWartRoots() {
        super(Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK));
        this.setDropItself(false);
    }
}
