package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockStalagnateBark extends BNPillar {
    public BlockStalagnateBark() {
        super(Materials.makeNetherWood(MapColor.TERRACOTTA_LIGHT_GREEN));
    }
}