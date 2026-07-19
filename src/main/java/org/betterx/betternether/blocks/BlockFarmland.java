package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockFarmland extends BlockBase {
    public BlockFarmland(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(Materials.makeNetherWood(settings, MapColor.TERRACOTTA_LIGHT_GREEN));
    }
}
