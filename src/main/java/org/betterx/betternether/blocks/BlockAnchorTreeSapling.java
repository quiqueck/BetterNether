package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

/**
 * The anchor tree sapling, which grows a different tree depending on which way up it was planted.
 * <p>
 * Hanging from a ceiling it grows the small downward branch, exactly as it always has. Standing on the
 * floor it grows nothing on its own - only a 2x2 does anything there, and what it grows is the giant
 * anchor tree, built upward from the floor until it meets the ceiling above.
 * <p>
 * The orientation lives in {@link #HANGING} rather than being read back out of the world. It has to: the
 * sapling's texture hangs downward, so a floor-planted one has to be rendered flipped, and a block model
 * can only vary per blockstate - the renderer cannot look at neighbouring blocks the way {@code canSurvive}
 * can. Keeping it in the state also takes the block tag back out of {@link #getShape}, which is called
 * during registration before tags are bound and threw {@code Tags not bound} when it consulted one.
 */
public class BlockAnchorTreeSapling extends FeatureSaplingBlock implements BonemealableBlock {
    /** {@code true} when hanging from the block above, {@code false} when standing on the block below. */
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    public BlockAnchorTreeSapling(BlockBehaviour.Properties properties) {
        super(properties,
                (level, pos, state, rnd) -> NetherTrees.ANCHOR_TREE_BRANCH
                        .placeInWorld(WorldState.registryAccess(), level, pos, rnd),
                (level, pos, state, rnd) -> NetherTrees.ANCHOR_TREE
                        .placeInWorld(WorldState.registryAccess(), level, pos, rnd),
                true
        );
        registerDefaultState(getStateDefinition().any().setValue(HANGING, true).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
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
        // Each orientation depends on its own support, so breaking the floor under a standing sapling
        // drops it even when there happens to be netherrack overhead.
        return state.getValue(HANGING) ? supports(level, pos.above()) : supports(level, pos.below());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(HANGING) ? HANGING_SHAPE : SHAPE;
    }

    /**
     * The giant grows from a 2x2 on the floor only. A ceiling-hung sapling keeps growing the small
     * branch however many of them are placed side by side.
     */
    @Override
    protected boolean megaFeatureEnabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return !state.getValue(HANGING)
                && level.getGameRules().getBoolean(NetherGameRules.GROW_LARGE_ANCHOR_TREES);
    }

    /**
     * A sapling standing on the floor waits for a 2x2 rather than growing the small branch, which is a
     * structure that hangs downward and would build into the ground it was planted on.
     */
    @Override
    protected boolean smallFeatureEnabled(@NotNull ServerLevel level, @NotNull BlockPos pos) {
        return level.getBlockState(pos).getValue(HANGING);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return SurvivesOnBlockTrait.survivesOn(this, blockState);
    }
}
