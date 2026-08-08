package org.betterx.betternether.mixin.common;

import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.blockentities.ChangebleCookTime;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

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

    /**
     * Credits burning a gloomsculk crystal.
     * <p>
     * Hung off getBurnDuration rather than off consumeFuel because the one call site sits directly in
     * front of the consume, guarded by the same {@code newLitTime > 0} that decides whether the stack
     * is actually spent - so a non-zero return here means this stack is about to burn. consumeFuel
     * itself is static and carries neither the level nor the position, which is what finding a player
     * needs.
     * <p>
     * 26.3 replaced the {@code FuelValues} first parameter with the {@code ServerLevel}, so the level
     * arrives directly rather than having to be read back off the block entity.
     */
    @Inject(method = "getBurnDuration", at = @At("RETURN"))
    private void betternether$creditCrystalBurn(
            ServerLevel level,
            ItemStack itemStack,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (cir.getReturnValueI() <= 0
                || !(itemStack.is(NetherTerrainBlocks.GLOOMSCULK_CRYSTAL.asItem())
                || itemStack.is(NetherTerrainBlocks.GLOOMSCULK_GEODE_CRYSTAL.asItem()))) {
            return;
        }

        final BlockPos pos = ((BlockEntity) (Object) this).getBlockPos();
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

    /**
     * 26.3 split {@code getTotalCookTime} into two static overloads (verified with
     * {@code javap -p net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity}):
     * <pre>
     * private static int getTotalCookTime(RecipeHolder&lt;? extends AbstractCookingRecipe&gt;, AbstractFurnaceBlockEntity)
     * private static int getTotalCookTime(ServerLevel, AbstractFurnaceBlockEntity)
     * </pre>
     * 26.1 only had the second one, so the descriptor is spelled out to keep the selection unambiguous.
     * <p>
     * The hook has to sit on the <b>{@code RecipeHolder} overload</b>, not the {@code ServerLevel} one.
     * Verified with {@code javap -p -c}: {@code serverTick} - the only path that stores
     * {@code cookingTotalTime} while the furnace is running - calls the {@code RecipeHolder} overload
     * directly (twice), while the {@code ServerLevel} overload is reached only from {@code setItem}, where it
     * merely wraps the {@code RecipeHolder} overload in an {@code Optional.map(...).orElse(200)}. Hooking the
     * {@code ServerLevel} overload had no effect on a ticking furnace: measured in-game, a Cincinnasite Forge
     * reported {@code cooking_total_time: 200}, identical to a vanilla furnace, while its halved
     * {@code getBurnDuration} override did apply ({@code lit_total_time: 800} vs. vanilla's {@code 1600}) -
     * i.e. the forge burnt fuel twice as fast without smelting any faster.
     * <p>
     * Since the {@code ServerLevel} overload delegates here, hooking only this overload still adjusts the
     * value exactly once on both paths.
     */
    @Inject(method = "getTotalCookTime(Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void betternether$getTotalCookTime(
            RecipeHolder<? extends AbstractCookingRecipe> recipe,
            AbstractFurnaceBlockEntity inventory,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (inventory instanceof ChangebleCookTime cct) {
            int val = cir.getReturnValue();
            cir.setReturnValue(cct.changeCookTime(val));
        }
    }
}
