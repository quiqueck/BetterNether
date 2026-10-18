package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.interfaces.SurvivesOnNetherrack;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.features.configured.NetherTrees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The anchor tree sapling, which grows a different tree depending on which way up it was planted.
 * <p>
 * Hanging from a ceiling it grows the small downward branch, exactly as it always has. Standing on the
 * floor it grows nothing on its own - only a 2x2 does anything there (and only with
 * {@link NetherGameRules#GROW_LARGE_ANCHOR_TREES} on), and what it grows is the giant anchor tree, built
 * upward from the floor until it meets the ceiling above.
 * <p>
 * The orientation lives in {@link #HANGING} so a floor-planted sapling can be rendered flipped.
 */
public class BlockAnchorTreeSapling extends FeatureSaplingBlock implements BonemealableBlock, SurvivesOnNetherrack, BehaviourSapling {
    /** {@code true} when hanging from the block above, {@code false} when standing on the block below. */
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    private static final VoxelShape STANDING_SHAPE = Block.box(4, 0, 4, 12, 14, 12);
    private static final VoxelShape HANGING_SHAPE = Block.box(4, 2, 4, 12, 16, 12);

    public BlockAnchorTreeSapling() {
        super((BlockState state) -> NetherTrees.ANCHOR_TREE_BRANCH);
        registerDefaultState(getStateDefinition().any().setValue(HANGING, true).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return isSurvivable(blockState);
    }

    private boolean supports(BlockGetter level, BlockPos at) {
        return mayPlaceOn(level.getBlockState(at), level, at);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockPos pos = ctx.getClickedPos();
        final boolean canHang = supports(ctx.getLevel(), pos.above());
        final boolean canStand = supports(ctx.getLevel(), pos.below());

        // Clicking the underside of a block is an unambiguous request to hang it there; otherwise a floor
        // wins, and hanging is the fallback so the original ceiling behaviour is never lost.
        final boolean hanging;
        if (ctx.getClickedFace() == Direction.DOWN && canHang) hanging = true;
        else if (canStand) hanging = false;
        else hanging = canHang;

        return defaultBlockState().setValue(HANGING, hanging);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(HANGING) ? supports(level, pos.above()) : supports(level, pos.below());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
    }

    /**
     * A hanging sapling grows the small branch, however many of them are placed side by side. A standing
     * one waits for a 2x2 rather than growing the small branch, a structure that hangs downward and would
     * build into the ground it was planted on.
     */
    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0 || state.getValue(HANGING)) {
            super.advanceTree(level, pos, state, random);
            return;
        }
        if (level.getGameRules().getBoolean(NetherGameRules.GROW_LARGE_ANCHOR_TREES)) {
            GiantSaplingGrowth.tryGrow(
                    level, pos, state, NetherTrees.ANCHOR_TREE,
                    s -> s.is(this) && !s.getValue(HANGING),
                    random
            );
        }
    }
}
