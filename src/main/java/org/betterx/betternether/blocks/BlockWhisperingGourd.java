package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.material.MapColor;

public class BlockWhisperingGourd extends BlockBase implements AddMineableAxe {
    public BlockWhisperingGourd() {
        super(Materials.makeNetherWood(MapColor.COLOR_BLUE));
    }
}
