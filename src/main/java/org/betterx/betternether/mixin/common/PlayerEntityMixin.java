package org.betterx.betternether.mixin.common;

import org.betterx.betternether.blocks.BlockStatueRespawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Statue-respawner spawn-position redirect: makes a player whose respawn point is a {@link BlockStatueRespawner}
 * spawn one block <em>in front of</em> the statue instead of inside it.
 * <p>
 * Hook point: {@link ServerPlayer#findRespawnPositionAndUseSpawnBlock(boolean, TeleportTransition.PostTeleportTransition)}.
 * The private static {@code findRespawnAndUseSpawnBlock(ServerLevel, RespawnConfig, boolean)} it delegates to only
 * understands beds and respawn anchors (anything else with {@code forced == false} yields {@code Optional.empty()}),
 * and its {@code RespawnPosAngle} return type is package-private. Injecting into the public wrapper instead lets us
 * build the resulting {@link TeleportTransition} — which is public — directly, with no access widening. The wrapper is
 * the sole caller of the private helper, so every respawn path (death via {@code PlayerList#respawn} and the End-portal
 * return in {@code EndPortalBlock}) is covered.
 */
@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void bn_statueRespawn(
            boolean useSpawnBlock,
            TeleportTransition.PostTeleportTransition postTeleportTransition,
            CallbackInfoReturnable<TeleportTransition> info
    ) {
        final ServerPlayer self = (ServerPlayer) (Object) this;
        final ServerPlayer.RespawnConfig respawnConfig = self.getRespawnConfig();
        if (respawnConfig == null) return;

        final MinecraftServer server = self.level().getServer();
        if (server == null) return;

        final ServerLevel level = server.getLevel(respawnConfig.respawnData().dimension());
        if (level == null) return;

        final BlockPos pos = respawnConfig.respawnData().pos();
        final BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof BlockStatueRespawner)) return;

        // When the spot in front of the statue is obstructed we fall through to vanilla, which reports a
        // missing/obstructed respawn block and sends the player to the world spawn.
        final Optional<Vec3> op = bn_findRespawnPosition(level, pos, blockState);
        if (op.isEmpty()) return;

        info.setReturnValue(new TeleportTransition(
                level,
                op.get(),
                Vec3.ZERO,
                respawnConfig.respawnData().yaw(),
                0.0F,
                postTeleportTransition
        ));
    }

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
