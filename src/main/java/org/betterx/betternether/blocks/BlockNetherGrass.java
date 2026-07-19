package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.betternether.blocks.materials.Materials;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
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


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return SurvivesOnBlockTrait.survivesOn(this, level.getBlockState(pos.below()));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnBlockTrait.survivesOn(this, state);
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

    public static class BoneGrass extends BaseBlockNetherGrass.OnEverything {
        public BoneGrass(BlockBehaviour.Properties settings) {
            super(settings);
        }

    }

    public static class SepiaBoneGrass extends BaseBlockNetherGrass.OnEverything {
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

    public BaseBlockNetherGrass() {
        super(Materials.makeNetherGrass(MapColor.TERRACOTTA_GRAY).offsetType(Block.OffsetType.XZ));
    }

    public BaseBlockNetherGrass(BlockBehaviour.Properties settings) {
        super(Materials.makeNetherGrass(settings, MapColor.TERRACOTTA_GRAY));
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


    public static class OnEverything extends BaseBlockNetherGrass {
        public OnEverything() {
            super();
        }

        public OnEverything(BlockBehaviour.Properties settings) {
            super(settings);
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return SurvivesOnBlockTrait.survivesOn(this, level.getBlockState(pos.below()));
        }

        @Override
        public boolean isTerrain(BlockState state) {
            return SurvivesOnBlockTrait.survivesOn(this, state);
        }
    }
}
