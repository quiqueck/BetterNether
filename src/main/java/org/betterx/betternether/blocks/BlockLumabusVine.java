package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.world.features.DeferedSeedBlock;
import org.betterx.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;

public class BlockLumabusVine extends BaseVineBlock implements DeferedSeedBlock {
    private static final VoxelShape MIDDLE_SHAPE = box(4, 0, 4, 12, 16, 12);
    static final VoxelShape BOTTOM_SHAPE = box(2, 4, 2, 14, 16, 14);
    private Block seed;

    public BlockLumabusVine(Properties settings, MapColor color) {
        super(
                Materials
                        .staticVine(settings, color)
                        .lightLevel(getLuminance()),
                9,
                1
        );
    }

    @Override
    public void setSeed(Block seed) {
        this.seed = seed;
    }

    /** The seed this vine drops; read back by {@link NetherLoot#lumabusVine()} at datagen time. */
    public Block getSeed() {
        return seed;
    }

    private static ToIntFunction<BlockState> getLuminance() {
        return (blockState) -> blockState.getOptionalValue(SHAPE).orElse(TripleShape.TOP) == TripleShape.BOTTOM
                ? 15
                : 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.BOTTOM ? BOTTOM_SHAPE : MIDDLE_SHAPE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @NotNull ItemStack getCloneItemStack(
            @NotNull LevelReader level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            boolean includeData
    ) {
        return new ItemStack(seed);
    }
}
