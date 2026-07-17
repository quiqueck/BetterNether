package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.material.MapColor;

public class BlockInkBushSeed extends BlockCommonSapling {
    public BlockInkBushSeed(Properties settings) {
        super(NetherBlocks.INK_BUSH, org.betterx.betternether.blocks.materials.Materials.netherSapling(settings).mapColor(MapColor.COLOR_RED).noLootTable());
    }
}
