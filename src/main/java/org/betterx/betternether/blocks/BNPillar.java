package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.behaviours.interfaces.BehaviourWood;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.Collections;
import java.util.List;

public abstract class BNPillar extends RotatedPillarBlock {
    protected BNPillar(Properties settings) {
        super(settings);
    }

    protected BNPillar(Block block) {
        super(FabricBlockSettings.copyOf(block));
    }

    protected BNPillar(MapColor color) {
        super(Materials.makeNetherWood(color));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
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
