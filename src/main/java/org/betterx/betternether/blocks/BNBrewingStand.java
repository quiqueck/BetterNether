package org.betterx.betternether.blocks;

import org.betterx.betternether.blockentities.BNBrewingStandBlockEntity;
import org.betterx.betternether.registry.BlockEntitiesRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BNBrewingStand extends BrewingStandBlock {
    public BNBrewingStand(BlockBehaviour.Properties settings) {
        super(settings
                .strength(0.5F, 0.5F)
                .lightLevel(state -> 1)
                .noOcclusion());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClientSide()
                ? null
                : createTickerHelper(type, BlockEntitiesRegistry.NETHER_BREWING_STAND, BNBrewingStandBlockEntity::tick);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BNBrewingStandBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BNBrewingStandBlockEntity) {
                player.openMenu((BNBrewingStandBlockEntity) blockEntity);
                player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BNBrewingStandBlockEntity bn) {
            Containers.dropContents(world, pos, bn);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, notify);
    }

}
