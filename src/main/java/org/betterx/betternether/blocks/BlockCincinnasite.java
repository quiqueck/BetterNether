package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase implements BehaviourMetal {
    public BlockCincinnasite(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(BehaviourBuilders.createMetal(settings, MapColor.COLOR_YELLOW)
                               .strength(3, 10)
                               .requiresCorrectToolForDrops()
        );
    }
}
