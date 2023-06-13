package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.blocks.BNBlockProperties.WillowBranchShape;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.util.List;

public class BlockWillowBranch extends BlockBaseNotFull implements AddMineableAxe {
    private static final VoxelShape V_SHAPE = Block.box(4, 0, 4, 12, 16, 12);
    public static final EnumProperty<WillowBranchShape> SHAPE = BNBlockProperties.WILLOW_SHAPE;

    public BlockWillowBranch() {
        super(Materials.makeNetherWood(MapColor.TERRACOTTA_RED)
                       .noOcclusion()
                       .noCollission()
                       .lightLevel(BlockWillowBranch::getLuminance));
        this.setRenderLayer(BNRenderLayer.CUTOUT);
        this.setDropItself(false);
        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, WillowBranchShape.MIDDLE));
    }

    protected static int getLuminance(BlockState state) {
        return state.getOptionalValue(SHAPE).map(s -> s == WillowBranchShape.END ? 15 : 0).orElse(0);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return V_SHAPE;
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
        if (world.isEmptyBlock(pos.above()))
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(state.getValue(SHAPE) == WillowBranchShape.END
                ? NetherBlocks.MAT_WILLOW.getTorch()
                : NetherBlocks.WILLOW_LEAVES);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(SHAPE) == WillowBranchShape.END) {
            return Lists.newArrayList(new ItemStack(NetherBlocks.MAT_WILLOW.getTorch()));
        } else {
            return Lists.newArrayList();
        }
    }
}
