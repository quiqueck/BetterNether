package org.betterx.betternether.blocks;

import org.betterx.bclib.interfaces.behaviours.BehaviourCompostable;
import org.betterx.bclib.interfaces.tools.AddMineableHoe;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.level.material.MapColor;

public class BlockBlackAppleSeed extends BlockCommonSapling implements AddMineableHoe, BehaviourCompostable {
    public BlockBlackAppleSeed() {
        super(NetherBlocks.BLACK_APPLE, MapColor.COLOR_ORANGE);
    }
}