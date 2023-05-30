package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSeed;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.material.MaterialColor;

public class BlockInkBushSeed extends BlockCommonSapling implements BehaviourSeed {
    public BlockInkBushSeed() {
        super(NetherBlocks.INK_BUSH, MaterialColor.COLOR_RED);
    }
}
