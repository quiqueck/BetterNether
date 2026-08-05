package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BasePlantBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


public abstract class BlockNetherGrass extends BaseBlockNetherGrass {
    public BlockNetherGrass(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public static class JunglePlant extends BlockNetherGrass {
        public JunglePlant(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }


    public static class SwampGrass extends BlockNetherGrass {
        public SwampGrass(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }

    public static class BoneGrass extends BaseBlockNetherGrass {
        public BoneGrass(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }

    public static class SepiaBoneGrass extends BaseBlockNetherGrass {
        public SepiaBoneGrass(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }

    public static class NetherGrass extends BlockNetherGrass {
        public NetherGrass(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }
}

abstract class BaseBlockNetherGrass extends BasePlantBlock {
    private static final VoxelShape SHAPE = box(4, 0, 4, 14, 12, 14);

    public BaseBlockNetherGrass(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
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
}
