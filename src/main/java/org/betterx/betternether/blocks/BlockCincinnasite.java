package org.betterx.betternether.blocks;

import org.betterx.bclib.complexmaterials.BehaviourBuilders;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase {
    public BlockCincinnasite() {
        super(BehaviourBuilders.createMetal(MapColor.COLOR_YELLOW)
                               .strength(3, 10)
                               .requiresCorrectToolForDrops()
        );
    }
}
