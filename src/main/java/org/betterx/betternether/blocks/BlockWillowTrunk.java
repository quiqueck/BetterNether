package org.betterx.betternether.blocks;

import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase.
public class BlockWillowTrunk extends Block {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final VoxelShape SHAPE_BOTTOM = box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_TOP = box(4, 0, 4, 12, 12, 12);

    // The no-arg overload (building a fresh, id-less BlockBehaviour.Properties.of() via
    // Materials.makeNetherWood(...)) was dead code - nothing in src called it, only the (Properties)
    // overload below is ever used at registration (see complex/WillowMaterial.java's
    // TrunkSlot.create(BlockWillowTrunk::new)). Removed rather than ported (WP3.8).
    public BlockWillowTrunk(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, TripleShape.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }
}
