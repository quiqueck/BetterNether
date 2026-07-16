package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;


public class BlockGiantLucis extends HugeMushroomBlock {
    public BlockGiantLucis(Properties settings) {
        super(Materials
                .walkablePlant(settings, MapColor.COLOR_YELLOW)
                .requiresCorrectToolForDrops()
                .lightLevel((bs) -> 15)
                .sound(SoundType.WOOD)
                .strength(1F));
    }
}
