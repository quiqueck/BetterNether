package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.betternether.blocks.materials.Materials;
import org.betterx.betternether.interfaces.SurvivesOnNetherGround;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockFeatherFern extends BlockCommonPlant implements SurvivesOnNetherGround, BehaviourPlant {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);

    public BlockFeatherFern() {
        super(Materials.NETHER_PLANT
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .randomTicks()
                .offsetType(Block.OffsetType.XZ)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(view, pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canSurviveOnTop(world, pos);
    }
}
