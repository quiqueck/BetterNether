package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourHelper;
import org.betterx.bclib.behaviours.interfaces.*;
import org.betterx.betternether.client.IRenderTypeable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import java.util.Collections;
import java.util.List;

public abstract class BNPane extends IronBarsBlock implements IRenderTypeable {
    private final boolean dropSelf;

    protected BNPane(Block block, boolean dropSelf) {
        super(FabricBlockSettings.copyOf(block).strength(0.3F, 0.3F).noOcclusion());
        this.dropSelf = dropSelf;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        if (dropSelf)
            return Collections.singletonList(new ItemStack(this.asItem()));
        else
            return super.getDrops(state, builder);
    }

    @Override
    public BNRenderLayer getRenderLayer() {
        return BNRenderLayer.TRANSLUCENT;
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

    public static class Wood extends BNPane implements BehaviourWood {
        public Wood(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }
    }

    public static class Stone extends BNPane implements BehaviourStone {
        public Stone(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }
    }

    public static class Metal extends BNPane implements BehaviourMetal {
        public Metal(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }
    }

    public static class Obsidian extends BNPane implements BehaviourObsidian {
        public Obsidian(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }
    }

    public static class Glass extends BNPane implements BehaviourGlass {
        public Glass(Block block, boolean dropSelf) {
            super(block, dropSelf);
        }
    }

    public static BNPane from(Block source, boolean dropSelf) {
        return BehaviourHelper.from(
                source,
                (s) -> new Wood(s, dropSelf),
                (s) -> new Stone(s, dropSelf),
                (s) -> new Metal(s, dropSelf),
                (s) -> new Obsidian(s, dropSelf),
                (s) -> new Glass(s, dropSelf)
        );
    }
}
