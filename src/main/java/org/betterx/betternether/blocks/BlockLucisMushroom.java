package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.blocks.BNBlockProperties.EnumLucisShape;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. The class carried its own getDrops() override, so BlockBase's dropItself mechanism was
// never in play; that override has since been replaced by a NetherLoot.lucisMushroom() trait on the registration.
public class BlockLucisMushroom extends Block {
    private static final VoxelShape V_SHAPE = box(0, 0, 0, 16, 9, 16);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<EnumLucisShape> SHAPE = BNBlockProperties.LUCIS_SHAPE;

    public BlockLucisMushroom(Properties settings) {
        super(settings);
        this.registerDefaultState(getStateDefinition().any()
                                                      .setValue(FACING, Direction.NORTH)
                                                      .setValue(SHAPE, EnumLucisShape.CORNER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, SHAPE);
    }

    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return V_SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return BlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.FRONT_BACK) {
            if (state.getValue(SHAPE) == EnumLucisShape.SIDE)
                state = state.setValue(FACING, state.getValue(FACING).getCounterClockWise());
            if (state.getValue(FACING) == Direction.NORTH) return state.setValue(FACING, Direction.WEST);
            if (state.getValue(FACING) == Direction.WEST) return state.setValue(FACING, Direction.NORTH);
            if (state.getValue(FACING) == Direction.SOUTH) return state.setValue(FACING, Direction.EAST);
            if (state.getValue(FACING) == Direction.EAST) return state.setValue(FACING, Direction.SOUTH);
        } else if (mirror == Mirror.LEFT_RIGHT) {
            if (state.getValue(SHAPE) == EnumLucisShape.SIDE)
                state = state.setValue(FACING, state.getValue(FACING).getCounterClockWise());
            if (state.getValue(FACING) == Direction.NORTH) return state.setValue(FACING, Direction.EAST);
            if (state.getValue(FACING) == Direction.EAST) return state.setValue(FACING, Direction.NORTH);
            if (state.getValue(FACING) == Direction.SOUTH) return state.setValue(FACING, Direction.WEST);
            if (state.getValue(FACING) == Direction.WEST) return state.setValue(FACING, Direction.SOUTH);
        }
        return state;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherMushroomBlocks.LUCIS_SPORE);
    }
}
