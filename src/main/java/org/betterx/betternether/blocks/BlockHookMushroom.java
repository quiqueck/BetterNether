package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHookMushroom extends BaseBlockMold {
    public BlockHookMushroom(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.above()));
    }
}
