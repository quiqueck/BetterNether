package org.betterx.betternether.blocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.advancements.BNCriterion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Iterator;

class BNObsidianBase extends Block {
    final Block transformsTo;

    public BNObsidianBase() {
        this(null);
    }

    public BNObsidianBase(Block transformsTo) {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN), transformsTo);
    }

    protected BNObsidianBase(Properties settings, Block transformsTo) {
        super(settings);
        this.transformsTo = transformsTo;

    }

    @Override
    public void neighborChanged(
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Block block,
            Orientation orientation,
            boolean bl
    ) {
        // 1.21.7: neighborChanged no longer supplies the source position; scan the
        // adjacent blocks for a powered lightning rod instead.
        if (transformsTo != null) {
            for (Direction direction : Direction.values()) {
                final BlockPos rodPos = blockPos.relative(direction);
                final BlockState updaterState = level.getBlockState(rodPos);
                if (updaterState.is(Blocks.LIGHTNING_ROD) && updaterState.getValue(LightningRodBlock.POWERED)) {
                    BNObsidian.onLightningUpdate(level, blockPos, transformsTo);
                    return;
                }
            }
        }
    }
}

public class BNObsidian extends BNObsidianBase {
    public BNObsidian() {
        super(null);
    }

    public BNObsidian(Block transformsTo) {
        super(transformsTo);
    }

    public BNObsidian(Properties settings, Block transformsTo) {
        super(settings, transformsTo);
    }

    public static void onLightningUpdate(Level level, BlockPos blockPos, Block transformsTo) {
        BlocksHelper.setWithoutUpdate(level, blockPos, transformsTo.defaultBlockState());

        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();
        Iterator nearbyPlayer = level.getEntitiesOfClass(
                                             ServerPlayer.class,
                                             (new AABB(x, y, z, x, y - 4, z)).inflate(10.0D, 5.0D, 10.0D)
                                     )
                                     .iterator();

        while (nearbyPlayer.hasNext()) {
            final ServerPlayer serverPlayer = (ServerPlayer) nearbyPlayer.next();
            BNCriterion.CONVERT_BY_LIGHTNING.trigger(serverPlayer, transformsTo.asItem());
        }
    }
}
