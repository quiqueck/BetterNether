package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherPlantBlocks;

import org.betterx.betternether.blocks.BNBlockProperties.PottedPlantShape;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import java.util.Collections;
import java.util.List;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone, and so is setDropItself(false). What this drops is decided by
// NetherLoot.pottedPlant() - one pool per PLANT value - rather than by a getDrops override.
public class BlockPottedPlant extends Block {
    public static final EnumProperty<PottedPlantShape> PLANT = BNBlockProperties.PLANT;

    public BlockPottedPlant(Properties settings) {
        super(settings);

        this.registerDefaultState(getStateDefinition().any().setValue(PLANT, PottedPlantShape.AGAVE));
    }

    /** Public so {@code NetherBlocks} can chain it as a {@code lightLevel(...)} registration-site setter. */
    public static int getLuminance(BlockState blockState) {
        if (!blockState.hasProperty(PLANT)) return 0;

        if (blockState.getValue(PLANT) == PottedPlantShape.WILLOW)
            return 12;
        else if (blockState.getValue(PLANT) == PottedPlantShape.JELLYFISH_MUSHROOM)
            return 13;
        else
            return 0;

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Block block = state.getValue(PLANT).getBlock();
        Vec3 vec3d = block.defaultBlockState().getOffset(pos);
        return block.getShape(block.defaultBlockState(), view, pos, ePos).move(-vec3d.x, -0.5 - vec3d.y, -vec3d.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(PLANT);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).getBlock() instanceof BlockBNPot;
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


    public static BlockState getPlant(Item item) {
        for (PottedPlantShape shape : PottedPlantShape.values()) {
            if (shape.getItem().equals(item))
                return NetherPlantBlocks.POTTED_PLANT.defaultBlockState().setValue(PLANT, shape);
        }
        return null;
    }
}
