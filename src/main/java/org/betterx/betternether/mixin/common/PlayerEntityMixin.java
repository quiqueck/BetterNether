package org.betterx.betternether.mixin.common;

import org.betterx.betternether.blocks.BlockStatueRespawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

/**
 * Statue-respawner spawn-position redirect.
 * <p>
 * TODO(1.21.7): the old {@code ServerPlayer#getRespawnPosition()} / {@code respawnPosition} field this used to
 * {@code @Inject} into were removed — respawn is now driven by {@code ServerPlayer.RespawnConfig} and
 * {@code findRespawnAndUseSpawnBlock(ServerLevel, RespawnConfig, boolean)} (which returns a package-private
 * {@code RespawnPosAngle}). Re-hook the redirect onto that flow. The {@link BlockStatueRespawner} block itself
 * still functions; only the "spawn one block in front of the statue" offset is temporarily inactive.
 */
@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {
    @Unique
    private static Optional<Vec3> bn_findRespawnPosition(ServerLevel world, BlockPos pos, BlockState state) {
        if (state.getValue(BlockStatueRespawner.TOP))
            pos = pos.below();
        pos = pos.relative(state.getValue(BlockStatueRespawner.FACING));
        BlockState state2 = world.getBlockState(pos);
        if (!state2.blocksMotion() && state2.getCollisionShape(world, pos).isEmpty())
            return Optional.of(Vec3.atLowerCornerOf(pos).add(0.5, 0, 0.5));
        else
            return Optional.empty();
    }
}
