package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherLeavesBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.blocks.BNBlockProperties.WillowBranchShape;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Reparented off BlockBaseNotFull (WP6.12): its dead canSuffocate/isSimpleFullBlock/allowsSpawning
// overrides are gone. setDropItself(false) is gone too - it only ever made the block fall through to the
// vanilla loot-table-driven getDrops(), which is what happens by default once the class no longer extends
// BlockBase. The class's own getDrops() override is gone as well: the end-segment-drops-a-torch rule it
// held is now NetherLoot.willowBranch(), wired in at the registration site (complex/WillowMaterial.java).
public class BlockWillowBranch extends Block {
    private static final VoxelShape V_SHAPE = Block.box(4, 0, 4, 12, 16, 12);
    public static final EnumProperty<WillowBranchShape> SHAPE = BNBlockProperties.WILLOW_SHAPE;

    // The no-arg overload (building a fresh, id-less BlockBehaviour.Properties.of() via
    // Materials.makeNetherWood(...)) was dead code - nothing in src called it, only the (Properties)
    // overload below is ever used at registration (see complex/WillowMaterial.java). Removed rather than
    // ported (WP3.8).
    public BlockWillowBranch(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(SHAPE, WillowBranchShape.MIDDLE));
    }

    protected static int getLuminance(BlockState state) {
        return state.getOptionalValue(SHAPE).map(s -> s == WillowBranchShape.END ? 15 : 0).orElse(0);
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return V_SHAPE;
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
        // Decoration rule (superset of the old "anything above" for the worldgen anchors): a branch hangs
        // from another branch (self-chaining) or any solid/leaves ceiling. Willow worldgen anchors every
        // branch column under WILLOW_LEAVES (in minecraft:leaves), so generation is unaffected.
        if (above.is(this) || org.betterx.bclib.util.BlocksHelper.isDecorationSupport(
                world, abovePos, above, Direction.DOWN))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(state.getValue(SHAPE) == WillowBranchShape.END
                ? NetherWoodBlocks.MAT_WILLOW.getTorch()
                : NetherLeavesBlocks.WILLOW_LEAVES);
    }

}
