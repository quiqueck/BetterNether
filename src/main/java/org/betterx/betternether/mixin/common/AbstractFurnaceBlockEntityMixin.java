package org.betterx.betternether.mixin.common;

import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.blockentities.ChangebleCookTime;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    /**
     * How far from the furnace the crystal's burning is still credited to a player.
     * <p>
     * Nothing hands a player down to the point where fuel is consumed - a furnace burns on its own
     * clock - so the advancement goes to whoever is standing by it. Wide enough to survive stepping
     * back from the block, short enough not to credit someone walking past a hopper-fed array in the
     * next room.
     */
    @Unique
    private static final double BETTERNETHER$CRYSTAL_BURN_CREDIT_RANGE = 8.0;

    @Inject(method = "getTotalCookTime", at = @At("RETURN"), cancellable = true)
    private static void betternether$getTotalCookTime(
            ServerLevel level,
            AbstractFurnaceBlockEntity inventory,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (inventory instanceof ChangebleCookTime cct) {
            int val = cir.getReturnValue();
            cir.setReturnValue(cct.changeCookTime(val));
        }
    }

    /**
     * Credits burning a gloomsculk crystal.
     * <p>
     * Hung off getBurnDuration rather than off consumeFuel because the one call site sits directly in
     * front of the consume, guarded by the same {@code newLitTime > 0} that decides whether the stack
     * is actually spent - so a non-zero return here means this stack is about to burn. consumeFuel
     * itself is static and carries neither the level nor the position, which is what finding a player
     * needs.
     */
    @Inject(method = "getBurnDuration", at = @At("RETURN"))
    private void betternether$creditCrystalBurn(
            FuelValues fuelValues,
            ItemStack itemStack,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (cir.getReturnValueI() <= 0
                || !(itemStack.is(NetherTerrainBlocks.GLOOMSCULK_CRYSTAL.asItem())
                || itemStack.is(NetherTerrainBlocks.GLOOMSCULK_GEODE_CRYSTAL.asItem()))) {
            return;
        }

        final BlockEntity self = (BlockEntity) (Object) this;
        final Level level = self.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }

        final BlockPos pos = self.getBlockPos();
        final Player player = level.getNearestPlayer(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                BETTERNETHER$CRYSTAL_BURN_CREDIT_RANGE,
                false
        );
        if (player instanceof ServerPlayer serverPlayer) {
            BNCriterion.BURNED_GLOOMSCULK_CRYSTAL.trigger(serverPlayer);
        }
    }
}
