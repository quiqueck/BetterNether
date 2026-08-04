package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherVineBlocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockVeinedSand extends Block {
    public BlockVeinedSand(BlockBehaviour.Properties settings) {
        super(settings
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.SAND)
                .strength(0.5F, 0.5F)
        );
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (world.getBlockState(pos.above()).getBlock() == NetherVineBlocks.SOUL_VEIN)
            return state;
        else
            return Blocks.SOUL_SAND.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Blocks.SOUL_SAND);
    }
}
