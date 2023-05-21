package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockReedsBlock extends BNPillar {
    public BlockReedsBlock() {
        super(Materials.makeNetherWood(MapColor.COLOR_CYAN));
    }
}
