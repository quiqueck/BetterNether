package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.betternether.client.IRenderTypeable;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class BNChain extends ChainBlock implements IRenderTypeable, BehaviourMetal, DropSelfLootProvider<BNChain> {
    public BNChain() {
        super(FabricBlockSettings.copyOf(Blocks.CHAIN));
    }

    @Override
    public BNRenderLayer getRenderLayer() {
        return BNRenderLayer.CUTOUT;
    }
}
