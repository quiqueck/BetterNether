package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Function;

public class BlockMold extends BaseBlockMold {
    public BlockMold(MapColor color) {
        super(color);
    }

    public BlockMold(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()));
    }
}

class BaseBlockMold extends BlockBaseNotFull {
    public BaseBlockMold(MapColor color) {
        this(color, p -> p);
    }

    public BaseBlockMold(MapColor color, Function<Properties, Properties> adaptProperties) {
        super(adaptProperties.apply(Materials.makeNetherGrass(color)
                                             .sound(SoundType.CROP)
                                             .randomTicks())
        );
        this.setDropItself(false);
    }

    public BaseBlockMold(Properties settings) {
        super(settings);
        this.setDropItself(false);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
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
        if (!canSurvive(state, world, pos))
            return Blocks.AIR.defaultBlockState();
        else
            return state;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (random.nextInt(16) == 0) {
            int c = 0;
            c = world.getBlockState(pos.north()).getBlock() == this ? c++ : c;
            c = world.getBlockState(pos.south()).getBlock() == this ? c++ : c;
            c = world.getBlockState(pos.east()).getBlock() == this ? c++ : c;
            c = world.getBlockState(pos.west()).getBlock() == this ? c++ : c;
            if (c < 2) {
                BlockPos npos = new BlockPos(pos);
                switch (random.nextInt(4)) {
                    case 0:
                        npos = npos.offset(-1, 0, 0);
                        break;
                    case 1:
                        npos = npos.offset(1, 0, 0);
                        break;
                    case 2:
                        npos = npos.offset(0, 0, -1);
                        break;
                    default:
                        npos = npos.offset(0, 0, 1);
                        break;
                }
                if (world.isEmptyBlock(npos) && canSurvive(state, world, npos)) {
                    BlocksHelper.setWithoutUpdate(world, npos, defaultBlockState());
                }
            }
        }
    }
}
