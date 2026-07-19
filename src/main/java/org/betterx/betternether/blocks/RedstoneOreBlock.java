package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.function.ToIntFunction;

public class RedstoneOreBlock extends RedStoneOreBlock {
    private final int minCount;
    private final int maxCount;

    public RedstoneOreBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(Materials.stone(settings, MapColor.COLOR_RED)
                               .strength(3, 5)
                               .requiresCorrectToolForDrops()
                               .sound(SoundType.NETHERRACK)
                               .randomTicks()
                               .lightLevel(litBlockEmission(9)));

        this.minCount = 1;
        this.maxCount = 3;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int i) {
        return (blockState) -> {
            return (Boolean) blockState.getValue(BlockStateProperties.LIT) ? i : 0;
        };
    }
}
