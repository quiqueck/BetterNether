package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherPlantBlocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSoulLilySapling extends BaseBlockCommonSapling {
    public BlockSoulLilySapling(Properties settings) {
        super(NetherPlantBlocks.SOUL_LILY, settings);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()));
    }
}
