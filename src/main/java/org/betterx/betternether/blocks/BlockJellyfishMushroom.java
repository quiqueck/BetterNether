package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherSaplingBlocks;

import org.betterx.betternether.blocks.BNBlockProperties.JellyShape;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
// overrides are gone. The class carried its own getDrops() override, so BlockBase's dropItself mechanism was
// never in play; that override has since been replaced by a NetherLoot.jellyfishMushroom() trait on the registration.
public class BlockJellyfishMushroom extends Block {
    private static final VoxelShape TOP_SHAPE = box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape MIDDLE_SHAPE = box(5, 0, 5, 11, 16, 11);
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    public static final EnumProperty<JellyShape> VISUAL = BNBlockProperties.JELLY_MUSHROOM_VISUAL;

    public BlockJellyfishMushroom(Properties settings) {
        // 26.2 made bouncing data-driven: Block#updateEntityMovementAfterFallOn is gone and Entity
        // now derives the rebound from Block#getBounceRestitution() (scaled by 0.8 for non-living
        // entities) unless the entity is suppressing the bounce or the block is in
        // BlockTags.SUPPRESSES_BOUNCE. Vanilla's slime block moved to bounceRestitution(1.0F) for
        // exactly the "-y * (living ? 1.0 : 0.8)" this block used to apply by hand, so the old
        // updateEntityMovementAfterFallOn/bounce pair is replaced by this property.
        super(settings.bounceRestitution(1.0F));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE, VISUAL);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.TOP ? TOP_SHAPE : MIDDLE_SHAPE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherSaplingBlocks.JELLYFISH_MUSHROOM_SAPLING);
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
        switch (state.getValue(SHAPE)) {
            case BOTTOM:
                return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP)
                        ? state
                        : Blocks.AIR.defaultBlockState();
            case MIDDLE:
                return world.getBlockState(pos.above()).getBlock() == this && world.getBlockState(pos.below())
                                                                                   .isFaceSturdy(
                                                                                           world,
                                                                                           pos.below(),
                                                                                           Direction.UP
                                                                                   )
                        ? state
                        : Blocks.AIR.defaultBlockState();
            case TOP:
            default:
                return world.getBlockState(pos.below()).getBlock() == this ? state : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (world.getBlockState(pos).getValue(SHAPE) != TripleShape.TOP)
            return;
        if (entity.isSuppressingBounce())
            super.fallOn(world, state, pos, entity, fallDistance);
        else
            entity.causeFallDamage(fallDistance, 0.0F, world.damageSources().fall());
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (world.getBlockState(pos).getValue(SHAPE) != TripleShape.TOP) {
            super.stepOn(world, pos, state, entity);
            return;
        }

        double d = Math.abs(entity.getDeltaMovement().y);
        if (d < 0.1D && !entity.isSteppingCarefully()) {
            double e = 0.4D + d * 0.2D;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(e, 1.0D, e));
        }
        super.stepOn(world, pos, state, entity);
    }

}