package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.material.MapColor;

public class BlockBlackAppleSeed extends BlockCommonSapling {
    public BlockBlackAppleSeed(Properties settings) {
        super(NetherBlocks.BLACK_APPLE, org.betterx.betternether.blocks.materials.Materials.netherSapling(settings).mapColor(MapColor.COLOR_ORANGE).noLootTable());
    }
}