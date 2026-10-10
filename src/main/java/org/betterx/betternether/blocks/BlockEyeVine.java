package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherPlantBlocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockEyeVine extends BaseVineBlock {
    public BlockEyeVine(Properties settings) {
        super(
                settings,
                9,
                2
        );
    }

    /**
     * The stalk only exists to carry an eyeball: once nothing (no stalk, no eyeball) hangs below it, it
     * comes down too, segment by segment, the way the pre-BaseVineBlock eye vine did. Without this,
     * cutting or pushing the eyeball off left a bare, itemless stalk behind that drops nothing and can
     * never grow a new eye.
     * <p>
     * Only a change <em>below</em> counts, and deliberately not in {@code canSurvive}: the stalk is placed
     * top-down (worldgen, bonemeal), so the newest segment briefly has air below it - and the segment above
     * re-shaping itself to MIDDLE/TOP sends it an update from above at exactly that moment.
     */
    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.DOWN && !neighborState.is(this) && !(neighborState.getBlock() instanceof BlockEyeBase)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(NetherPlantBlocks.EYE_SEED);
    }
}
