package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.BlocksHelper;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.entity.InsideBlockEffectApplier;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. Always dropped itself unconditionally via BlockBase's inherited getDrops() override
// (no loot table json was generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion() on
// the registration.
public class BlockNetherCactus extends Block {
    private static final VoxelShape TOP_SHAPE = box(4, 0, 4, 12, 8, 12);
    private static final VoxelShape SIDE_SHAPE = box(5, 0, 5, 11, 16, 11);
    public static final BooleanProperty TOP = BlockProperties.TOP;

    public BlockNetherCactus(Properties settings) {
        super(settings);
        this.registerDefaultState(getStateDefinition().any().setValue(TOP, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(TOP).booleanValue() ? TOP_SHAPE : SIDE_SHAPE;
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
        if (canSurvive(state, world, pos)) {
            Block up = world.getBlockState(pos.above()).getBlock();
            if (up == this)
                return state.setValue(TOP, false);
            else
                return this.defaultBlockState();
        } else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.below());
        return SurvivesOnBlockTrait.survivesOn(this, down) || down.getBlock() == this;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
            return;
        }
        if (state.getValue(TOP).booleanValue() && random.nextInt(16) == 0) {
            BlockPos up = pos.above();
            boolean grow = world.getBlockState(up).getBlock() == Blocks.AIR;
            grow = grow && (BlocksHelper.getLengthDown(world, pos, this) < 3);
            if (grow) {
                BlocksHelper.setWithUpdate(world, up, defaultBlockState());
                BlocksHelper.setWithUpdate(world, pos, defaultBlockState().setValue(TOP, false));
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
        entity.hurt(world.damageSources().cactus(), 1.0F);
    }
}