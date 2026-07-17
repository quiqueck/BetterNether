package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockCincinnasite extends BlockBase {
    public BlockCincinnasite(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(Materials.metal(settings, MapColor.COLOR_YELLOW)
                               .strength(3, 10)
                               .requiresCorrectToolForDrops()
        );
    }
}
