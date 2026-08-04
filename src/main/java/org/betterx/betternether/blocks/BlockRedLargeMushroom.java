package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase.
public class BlockRedLargeMushroom extends Block {
    private static final VoxelShape TOP_SHAPE = box(0, 0.1, 0, 16, 16, 16);
    private static final VoxelShape MIDDLE_SHAPE = box(5, 0, 5, 11, 16, 11);
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public BlockRedLargeMushroom(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.TOP ? TOP_SHAPE : MIDDLE_SHAPE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return state.getValue(SHAPE) == TripleShape.TOP
                ? new ItemStack(Items.RED_MUSHROOM)
                : new ItemStack(NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem());
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
        switch (state.getValue(SHAPE)) {
            case BOTTOM:
                return state;
            case MIDDLE:
            case TOP:
            default:
                return world.getBlockState(pos.below()).getBlock() == this ? state : Blocks.AIR.defaultBlockState();
        }
    }
}
