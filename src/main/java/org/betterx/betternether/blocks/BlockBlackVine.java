package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourClimableVine;
import org.betterx.bclib.blocks.BaseSimpleVineBlock;
import org.betterx.wover.loot.api.BlockLootProvider;

import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.material.MapColor;

public class BlockBlackVine extends BaseSimpleVineBlock implements BonemealableBlock, BehaviourClimableVine, BlockLootProvider {
    public BlockBlackVine() {
        super(BehaviourBuilders.createStaticVine(MapColor.COLOR_BLACK).instabreak(), 27, 1);
    }
}
