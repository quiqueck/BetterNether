package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.betternether.interfaces.SurvivesOnSoulGroundOrFarmLand;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockSoulLilySapling extends BaseBlockCommonSapling implements SurvivesOnSoulGroundOrFarmLand, BehaviourSapling {
    public BlockSoulLilySapling(Properties settings) {
        super(NetherBlocks.SOUL_LILY, org.betterx.betternether.blocks.materials.Materials.netherSapling(settings).mapColor(MapColor.COLOR_ORANGE).noLootTable());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canSurviveOnTop(world, pos);
    }
}
