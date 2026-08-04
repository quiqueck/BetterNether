package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockEyeBase extends Block {
    public BlockEyeBase(Properties settings) {
        super(settings);
    }

    public boolean allowsSpawning(BlockState state, BlockGetter view, BlockPos pos, EntityType<?> type) {
        return false;
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
        BlockPos blockPos = pos.above();
        BlockState above = world.getBlockState(blockPos);
        // Decoration rule (superset of the former eye-vine/netherrack whitelist): the block above must be an
        // eye vine (the plant's own stem) or any solid/leaves ceiling. NETHERRACK is a sturdy solid, so the
        // old worldgen anchor still passes.
        if (above.is(NetherVineBlocks.EYE_VINE) || org.betterx.bclib.util.BlocksHelper.isDecorationSupport(
                world, blockPos, above, Direction.DOWN))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherPlantBlocks.EYE_SEED);
    }
}
