package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourClimableVine;
import org.betterx.bclib.blocks.BlockProperties;
import org.betterx.betternether.BlocksHelper;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGoldenVine extends BlockBaseNotFull implements BonemealableBlock, BehaviourClimableVine, BlockLootProvider {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
    public static final BooleanProperty BOTTOM = BlockProperties.BOTTOM;

    public BlockGoldenVine() {
        super(BehaviourBuilders
                .createStaticVine(MapColor.COLOR_RED)
                .lightLevel((bs) -> 15)
        );
        this.setRenderLayer(BNRenderLayer.CUTOUT);
        this.setDropItself(false);
        this.registerDefaultState(getStateDefinition().any().setValue(BOTTOM, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(BOTTOM);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState upState = world.getBlockState(pos.above());
        return upState.getBlock() == this || upState.isFaceSturdy(world, pos, Direction.DOWN);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter view, BlockPos pos) {
        return true;
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
        if (canSurvive(state, world, pos))
            return world.getBlockState(pos.below()).getBlock() == this
                    ? state.setValue(BOTTOM, false)
                    : state.setValue(BOTTOM, true);
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        MutableBlockPos blockPos = new MutableBlockPos().set(pos);
        for (int y = pos.getY() - 1; y > 1; y--) {
            blockPos.setY(y);
            if (world.getBlockState(blockPos).getBlock() != this)
                return world.getBlockState(blockPos).getBlock() == Blocks.AIR;
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        MutableBlockPos blockPos = new MutableBlockPos().set(pos);
        for (int y = pos.getY(); y > 1; y--) {
            blockPos.setY(y);
            if (world.getBlockState(blockPos).getBlock() != this)
                break;
        }
        BlocksHelper.setWithUpdate(world, blockPos.above(), defaultBlockState().setValue(BOTTOM, false));
        BlocksHelper.setWithUpdate(world, blockPos, defaultBlockState().setValue(BOTTOM, true));
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this);
    }
}
