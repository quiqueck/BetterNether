package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseGlassBlock;

import net.minecraft.client.data.models.model.*;
import net.minecraft.world.level.block.Block;


public class BNGlass extends BaseGlassBlock {
    public BNGlass(Block block) {
        super(block, 0.3f);
    }

    public BNGlass(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(settings, 0.3f);
    }
}
