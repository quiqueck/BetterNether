package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;

import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class BlockWartRoots extends BlockBase implements AddMineableAxe {
    public BlockWartRoots() {
        super(FabricBlockSettings.copy(Blocks.NETHER_WART_BLOCK));
        this.setDropItself(false);
    }
}
