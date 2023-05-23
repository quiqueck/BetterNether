package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase implements BehaviourMetal {
    public BlockCincinnasite() {
        super(BehaviourBuilders.createMetal(MapColor.COLOR_YELLOW)
                               .strength(3, 10)
                               .requiresCorrectToolForDrops()
        );
    }
}
