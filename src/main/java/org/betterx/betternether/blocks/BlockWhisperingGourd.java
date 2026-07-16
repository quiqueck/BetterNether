package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockWhisperingGourd extends BlockBase {
    public BlockWhisperingGourd(Properties settings) {
        super(org.betterx.betternether.blocks.materials.Materials.makeNetherWood(settings, MapColor.COLOR_BLUE).strength(0.5f));
    }
}
