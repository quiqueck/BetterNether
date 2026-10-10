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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import java.util.Collections;
import java.util.List;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - the class's own getDrops() override below is
// unaffected either way.
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

    // The plant lives in the cell ABOVE its 8px pot, rendered half a block down, so a filled pot is two cells
    // tall and the plant cell's top is the only surface left to build on (#85). It behaves like leaves there:
    // a full-cube outline and collision, so blocks placed against it sit flush on the cell's top and mobs
    // stand on it, but an empty support shape, so - exactly like leaves - nothing needing a sturdy face
    // (torches, buttons, pressure plates) attaches. The outline keeps the plant's own (lowered) shape on top of
    // the cube so the part of the plant drawn inside the pot's cell stays clickable.
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Block block = state.getValue(PLANT).getBlock();
        Vec3 vec3d = block.defaultBlockState().getOffset(pos);
        return Shapes.or(
                Shapes.block(),
                block.getShape(block.defaultBlockState(), view, pos, ePos).move(-vec3d.x, -0.5 - vec3d.y, -vec3d.z)
        );
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter view, BlockPos pos) {
        return Shapes.empty();
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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Block block = state.getValue(PLANT).getBlock();
        return Collections.singletonList(new ItemStack(block.asItem()));
    }

    public static BlockState getPlant(Item item) {
        for (PottedPlantShape shape : PottedPlantShape.values()) {
            if (shape.getItem().equals(item))
                return NetherPlantBlocks.POTTED_PLANT.defaultBlockState().setValue(PLANT, shape);
        }
        return null;
    }
}
