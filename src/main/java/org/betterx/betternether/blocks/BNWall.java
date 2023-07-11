package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class BNWall extends WallBlock implements DropSelfLootProvider<BNWall> {
    public static class Stone extends BNWall implements BehaviourStone {
        public Stone(Block block) {
            super(block);
        }
    }

    public static class Metal extends BNWall implements BehaviourMetal {
        public Metal(Block block) {
            super(block);
        }
    }

    public BNWall(Block block) {
        super(FabricBlockSettings.copyOf(block).noOcclusion());
    }

    public static BNWall from(Block source) {
        if (source instanceof BehaviourStone)
            return new BNWall.Stone(source);
        if (source instanceof BehaviourMetal)
            return new BNWall.Metal(source);


        return new BNWall(source);
    }
}
