package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.BehaviourHelper;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.behaviours.interfaces.BehaviourWood;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collections;
import java.util.List;

public class BlockBase extends Block {
    public static class Stone extends BlockBase implements BehaviourStone {
        public Stone(Block source) {
            super(BlockBehaviour.Properties.ofFullCopy(source));
        }

        public Stone(Properties settings) {
            super(settings);
        }
    }

    public static class Metal extends BlockBase implements BehaviourMetal {
        public Metal(Block source) {
            super(BlockBehaviour.Properties.ofFullCopy(source));
        }

        public Metal(Properties settings) {
            super(settings);
        }
    }

    public static class Wood extends BlockBase implements BehaviourWood {
        public Wood(Block source) {
            super(BlockBehaviour.Properties.ofFullCopy(source));
        }

        public Wood(Properties settings) {
            super(settings);
        }
    }

    private boolean dropItself = true;

    public BlockBase(Properties settings) {
        super(settings);
    }



    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (dropItself)
            return Collections.singletonList(new ItemStack(this.asItem()));
        else
            return super.getDrops(state, builder);
    }

    public void setDropItself(boolean drop) {
        this.dropItself = drop;
    }

    public static BlockBase from(Block source) {
        return BehaviourHelper.from(source,
                Wood::new, Stone::new, Metal::new
        );
    }

    public static BlockBase from(Block source, Properties settings) {
        return BehaviourHelper.from(
                source,
                b -> new Wood(settings),
                b -> new Stone(settings),
                b -> new Metal(settings)
        );
    }
}
