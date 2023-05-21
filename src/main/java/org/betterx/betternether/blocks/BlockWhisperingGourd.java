package org.betterx.betternether.blocks;

import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockWhisperingGourd extends BlockBase {
    public BlockWhisperingGourd() {
        super(Materials.makeNetherWood(MapColor.COLOR_BLUE));
    }
}
