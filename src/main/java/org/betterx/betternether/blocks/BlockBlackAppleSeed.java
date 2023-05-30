package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSeed;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.material.MaterialColor;

public class BlockBlackAppleSeed extends BlockCommonSapling implements BehaviourSeed {
    public BlockBlackAppleSeed() {
        super(NetherBlocks.BLACK_APPLE, MaterialColor.COLOR_ORANGE);
    }
}