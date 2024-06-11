package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSand;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockVeinedSand extends BlockBase implements BlockTagProvider, BehaviourSand {
    public BlockVeinedSand() {
        super(BlockBehaviour.Properties
                .ofFullCopy(Blocks.SAND)
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.SAND)
                .strength(0.5F, 0.5F)
        );
        this.setDropItself(false);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (world.getBlockState(pos.above()).getBlock() == NetherBlocks.SOUL_VEIN)
            return state;
        else
            return Blocks.SOUL_SAND.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.SOUL_SAND);
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.SOUL_GROUND);
    }
}
