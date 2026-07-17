package org.betterx.betternether.blocks;

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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockVeinedSand extends BlockBase implements BlockTagProvider {
    public BlockVeinedSand(BlockBehaviour.Properties settings) {
        super(settings
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.SAND)
                .strength(0.5F, 0.5F)
        );
        this.setDropItself(false);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (world.getBlockState(pos.above()).getBlock() == NetherBlocks.SOUL_VEIN)
            return state;
        else
            return Blocks.SOUL_SAND.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Blocks.SOUL_SAND);
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.SOUL_GROUND);
    }
}
