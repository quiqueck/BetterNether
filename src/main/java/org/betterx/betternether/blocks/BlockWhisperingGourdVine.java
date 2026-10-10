package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherCropBlocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.util.LootUtil;
import org.betterx.betternether.BlocksHelper;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Unlike the other {@link BaseVineBlock}s, this vine's {@link #SHAPE} is not purely structural: below the tip
 * ({@code BOTTOM}), a {@code MIDDLE} segment is one carrying gourds and a {@code TOP} segment is a bare one -
 * the worldgen feature mixes the two at random along the column, and growth adds gourd-carrying segments.
 * Shearing a {@code MIDDLE} segment harvests it into a {@code TOP} one (#84).
 */
public class BlockWhisperingGourdVine extends BaseVineBlock.Growing {
    public BlockWhisperingGourdVine(Properties settings) {
        super(
                settings,
                6,
                1,
                16
        );
    }

    /**
     * Keeps the gourd/bare distinction across neighbour updates. The inherited rule re-derives every segment
     * from its neighbours, which turned every bare segment with a vine above it back into a gourd one on the
     * first update - undoing a harvest, and rewriting the worldgen mix.
     * <p>
     * A former tip that something now hangs below becomes bare: natural growth already set the right state
     * in {@code grow} before placing the new tip, so this only applies to a vine placed by hand, which must
     * not conjure gourds on the segment above it.
     */
    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction neighborDirection,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (level.getBlockState(pos.below()).getBlock() != this) {
            return makeBottomState(state);
        }
        if (state.getValue(SHAPE) == BlockProperties.TripleShape.BOTTOM) {
            return makeTopState(state);
        }
        return state;
    }

    @Override
    public InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!LootUtil.isShear(itemStack) || state.getValue(SHAPE) != BlockProperties.TripleShape.MIDDLE) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hit);
        }
        if (!level.isClientSide()) {
            BlocksHelper.setWithUpdate(level, pos, makeTopState(state));
            popResource(level, pos, new ItemStack(NetherCropBlocks.WHISPERING_GOURD, 1 + level.getRandom().nextInt(2)));
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            itemStack.hurtAndBreak(1, player, hand);
            level.gameEvent(player, GameEvent.SHEAR, pos);
        }
        // Played on both sides and excluding the player, like BlockGloomwispVine: the shearing client
        // predicts its own sound, everyone else hears the server's.
        level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}
