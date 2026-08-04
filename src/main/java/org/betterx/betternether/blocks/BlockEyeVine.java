package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherPlantBlocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockEyeVine extends BaseVineBlock {
    public BlockEyeVine(Properties settings) {
        super(
                settings,
                9,
                2
        );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherPlantBlocks.EYE_SEED);
    }
}
