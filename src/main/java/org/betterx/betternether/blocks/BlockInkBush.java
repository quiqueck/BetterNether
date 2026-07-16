package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockInkBush extends BlockCommonPlant implements BehaviourPlant {
    public BlockInkBush(Properties settings) {
        super(Materials.plant(settings, MapColor.COLOR_BLACK)
                               .sound(SoundType.CROP)
                               .noOcclusion()
        );
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState) {
        if (blockState.getValue(AGE) == 3) {
            //return an empty shape to prevent occlusion
            return Shapes.empty();
        }

        return super.getOcclusionShape(blockState);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherBlocks.INK_BUSH_SEED);
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()));
    }
}
