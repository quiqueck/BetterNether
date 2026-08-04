package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase.
public class BlockStalagnate extends Block {
    private static final VoxelShape SELECT_SHAPE = box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape COLLISION_SHAPE = box(5, 0, 5, 11, 16, 11);
    public static final EnumProperty<BlockProperties.TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    // The no-arg overload (building a fresh, id-less BlockBehaviour.Properties.of() via
    // Materials.makeNetherWood(...)) was dead code - nothing in src called it, only the (Properties)
    // overload below is ever used at registration (see complex/StalagnateMaterial.java's
    // TrunkSlot.createClimbable(BlockStalagnate::new, ...)). Removed rather than ported (WP3.8).
    public BlockStalagnate(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, BlockProperties.TripleShape.MIDDLE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SELECT_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return COLLISION_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherWoodBlocks.MAT_STALAGNATE.getStem());
    }
}
