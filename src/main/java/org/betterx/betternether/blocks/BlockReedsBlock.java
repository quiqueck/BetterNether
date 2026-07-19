package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class BlockReedsBlock extends BNPillar.Wood {
    public BlockReedsBlock() {
        super(Materials.makeNetherWood(MapColor.COLOR_CYAN).strength(1));
    }

    public BlockReedsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
