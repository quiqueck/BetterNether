package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockHookMushroom extends BaseBlockMold implements BehaviourPlant {
    public BlockHookMushroom(Properties settings) {
        super(org.betterx.betternether.blocks.materials.Materials.makeNetherGrass(settings, MapColor.COLOR_PINK)
                       .lightLevel(s -> 13)
                       .sound(SoundType.CROP)
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.above()));
    }
}
