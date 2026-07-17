package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.*;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collections;
import java.util.List;

public abstract class BNPane extends IronBarsBlock {
    private final boolean dropSelf;

    protected BNPane(Block block, boolean dropSelf) {
        super(BlockBehaviour.Properties.ofFullCopy(block).strength(0.3F, 0.3F).noOcclusion());
        this.dropSelf = dropSelf;
    }

    protected BNPane(BlockBehaviour.Properties settings, boolean dropSelf) {
        super(settings.strength(0.3F, 0.3F).noOcclusion());
        this.dropSelf = dropSelf;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (dropSelf)
            return Collections.singletonList(new ItemStack(this.asItem()));
        else
            return super.getDrops(state, builder);
    }


    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState neighbor, Direction facing) {
        if (neighbor.getBlock() == this) {
            if (!facing.getAxis().isHorizontal()) {
                return false;
            }

            if (state.getValue(PROPERTY_BY_DIRECTION.get(facing)) && neighbor.getValue(PROPERTY_BY_DIRECTION.get(facing.getOpposite()))) {
                return true;
            }
        }

        return super.skipRendering(state, neighbor, facing);
    }

    public static class Wood extends BNPane {
        public Wood(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }

        public Wood(BlockBehaviour.Properties settings, boolean dropSelf) {
            super(settings, dropSelf);
        }
    }

    public static class Stone extends BNPane implements BehaviourStone {
        public Stone(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }

        public Stone(BlockBehaviour.Properties settings, boolean dropSelf) {
            super(settings, dropSelf);
        }
    }

    public static class Metal extends BNPane {
        public Metal(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }

        public Metal(BlockBehaviour.Properties settings, boolean dropSelf) {
            super(settings, dropSelf);
        }
    }

    public static class Obsidian extends BNPane implements BehaviourObsidian {
        public Obsidian(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }

        public Obsidian(BlockBehaviour.Properties settings, boolean dropSelf) {
            super(settings, dropSelf);
        }
    }

    public static class Glass extends BNPane {
        public Glass(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }

        public Glass(BlockBehaviour.Properties settings, boolean dropSelf) {
            super(settings, dropSelf);
        }
    }
}
