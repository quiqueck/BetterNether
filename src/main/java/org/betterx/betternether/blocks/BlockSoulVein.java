package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. The class already carries its own getDrops() override (falls through to
// super.getDrops() - now Block's, not BlockBase's - when the tool check fails), so BlockBase's dropItself
// mechanism was never really in play for the common case either.
public class BlockSoulVein extends Block implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

    public BlockSoulVein(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()));
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
        if (canSurvive(state, world, pos))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        popResource(world, pos, new ItemStack(this.asItem()));
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockState(pos.below()).getBlock() == Blocks.SOUL_SAND)
            world.setBlockAndUpdate(pos.below(), NetherTerrainBlocks.VEINED_SAND.defaultBlockState());
    }

}
