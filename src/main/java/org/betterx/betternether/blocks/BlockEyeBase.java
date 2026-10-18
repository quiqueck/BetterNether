package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableHoe;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Eyeball blocks need their own, fresh {@link Properties} (not the shared, mutable
 * {@code Materials.NETHER_PLANT}): blocks registered earlier chain {@code noLootTable()} onto that shared
 * instance, which left the eyeballs pointing at {@code minecraft:empty} - they dropped neither slime balls
 * nor eye seeds (quiqueck/BetterNether#153, #159, #179).
 */
public class BlockEyeBase extends BlockBase implements AddMineableHoe {
    public BlockEyeBase(Properties settings) {
        super(settings);
        setDropItself(false);
    }

    public boolean allowsSpawning(BlockState state, BlockGetter view, BlockPos pos, EntityType<?> type) {
        return false;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockPos blockPos = pos.above();
        Block up = world.getBlockState(blockPos).getBlock();
        if (up != NetherBlocks.EYE_VINE && up != Blocks.NETHERRACK)
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(NetherBlocks.EYE_SEED);
    }
}
