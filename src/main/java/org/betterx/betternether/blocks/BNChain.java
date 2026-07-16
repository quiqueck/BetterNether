package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BNChain extends ChainBlock implements BehaviourMetal, DropSelfLootProvider<BNChain> {
    public BNChain(BlockBehaviour.Properties settings) {
        super(settings);
    }

}
