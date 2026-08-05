package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherCropBlocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.util.LootUtil;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;


public class BlockWhisperingGourdVine extends BaseVineBlock.Growing {
    public BlockWhisperingGourdVine(Properties settings) {
        super(
                settings,
                6,
                1,
                16
        );
    }

    @Override
    public InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack tool = player.getItemInHand(hand);
        if (LootUtil.isShear(tool) && state.getValue(SHAPE) == BlockProperties.TripleShape.MIDDLE) {
            if (!world.isClientSide()) {
                BlocksHelper.setWithUpdate(world, pos, state.setValue(SHAPE, BlockProperties.TripleShape.BOTTOM));
                world.addFreshEntity(new ItemEntity(
                        world,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        new ItemStack(
                                NetherCropBlocks.WHISPERING_GOURD)
                ));
                if (world.getRandom().nextBoolean()) {
                    world.addFreshEntity(new ItemEntity(
                            world,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            new ItemStack(NetherCropBlocks.WHISPERING_GOURD)
                    ));
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            return super.useItemOn(itemStack, state, world, pos, player, hand, hit);
        }
    }
}
