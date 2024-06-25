package org.betterx.betternether.mixin.common;

import org.betterx.betternether.blocks.BlockStatueRespawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {

    @Shadow
    public abstract ServerLevel serverLevel();

    @Shadow
    @Nullable
    private BlockPos respawnPosition;

    @Inject(method = "getRespawnPosition", at = @At(value = "HEAD"), cancellable = true)
    private void bn_statueRespawn(
            CallbackInfoReturnable<BlockPos> info
    ) {
        final BlockPos pos = this.respawnPosition;
        if (pos == null) return;
        final BlockState blockState = this.serverLevel().getBlockState(pos);
        final Block block = blockState.getBlock();
        if (block instanceof BlockStatueRespawner) {
            final Optional<Vec3> op = bn_findRespawnPosition(this.serverLevel(), pos, blockState);
            if (op.isPresent()) {
                info.setReturnValue(new BlockPos((int) op.get().x, (int) op.get().y, (int) op.get().z));
                info.cancel();
            }

        }
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