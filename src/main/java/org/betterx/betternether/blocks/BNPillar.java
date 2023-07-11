package org.betterx.betternether.blocks;

import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.behaviours.interfaces.BehaviourWood;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public abstract class BNPillar extends RotatedPillarBlock implements DropSelfLootProvider<BNPillar> {
    protected BNPillar(Properties settings) {
        super(settings);
    }

    protected BNPillar(Block block) {
        super(FabricBlockSettings.copyOf(block));
    }

    protected BNPillar(MapColor color) {
        super(Materials.makeNetherWood(color));
    }

    public static class Wood extends BNPillar implements BehaviourWood {
        public Wood(Properties settings) {
            super(settings);
        }

        public Wood(Block block) {
            super(block);
        }

        public Wood(MapColor color) {
            super(color);
        }
    }

    public static class Stone extends BNPillar implements BehaviourStone {
        public Stone(Properties settings) {
            super(settings);
        }

        public Stone(Block block) {
            super(block);
        }

        public Stone(MapColor color) {
            super(color);
        }
    }

    public static class Metal extends BNPillar implements BehaviourMetal {
        public Metal(Properties settings) {
            super(settings);
        }

        public Metal(Block block) {
            super(block);
        }

        public Metal(MapColor color) {
            super(color);
        }
    }
}
