package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class BlockRubeusLeaves extends BNLeaves {
    public BlockRubeusLeaves(Block sapling) {
        super(sapling, MapColor.COLOR_LIGHT_BLUE);
    }

    public BlockRubeusLeaves(Block sapling, Properties settings) {
        super(sapling, Materials.staticLeaves(settings, MapColor.COLOR_LIGHT_BLUE, false).noOcclusion());
    }
}
