package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherLeavesBlocks;

import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase.
public class BlockAnchorTreeVine extends Block {
    protected static final VoxelShape SHAPE_SELECTION = Block.box(4, 0, 4, 12, 16, 12);
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public BlockAnchorTreeVine(Properties settings) {
        super(settings);
    }

    public static int getLuminance(BlockState blockState) {
        return blockState.getOptionalValue(SHAPE).map(s -> s == TripleShape.BOTTOM ? 15 : 0).orElse(0);
    }

    public BlockBehaviour.OffsetType getOffsetType() {
        return BlockBehaviour.OffsetType.XZ;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        return SHAPE_SELECTION.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
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
        BlockPos abovePos = pos.above();
        BlockState above = world.getBlockState(abovePos);
        // Decoration rule (superset of the former self/anchor-leaves/netherrack whitelist): the block above
        // must be another anchor vine (self-chaining) or any solid/leaves ceiling. ANCHOR_TREE_LEAVES is in
        // minecraft:leaves and NETHERRACK is a sturdy solid, so every old worldgen anchor still passes.
        if (above.is(this) || org.betterx.bclib.util.BlocksHelper.isDecorationSupport(
                world, abovePos, above, Direction.DOWN))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherLeavesBlocks.ANCHOR_TREE_LEAVES);
    }
}