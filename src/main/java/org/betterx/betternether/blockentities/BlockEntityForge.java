package org.betterx.betternether.blockentities;

import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.registry.BlockEntitiesRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityForge extends AbstractFurnaceBlockEntity implements ChangebleCookTime {
    public static final int SPEEDUP = 2;

    public BlockEntityForge(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.CINCINNASITE_FORGE, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.forge", new Object[0]);
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }

    /**
     * 26.3 removed {@code FuelValues} (the server-wide fuel table) - burn time is now the
     * {@code minecraft:cooking_fuel} item component, resolved through a loot {@code NumberProvider},
     * which is why the hook takes a {@link ServerLevel} instead. Halving whatever vanilla resolves
     * keeps the forge's "burns fuel twice as fast" behaviour unchanged.
     */
    @Override
    protected int getBurnDuration(ServerLevel level, ItemStack fuel) {
        return super.getBurnDuration(level, fuel) / SPEEDUP;
    }

    @Override
    public int changeCookTime(int cookTime) {
        return cookTime / SPEEDUP;
    }

    @Override
    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer) {
        BNCriterion.USED_FORGE.trigger(serverPlayer);
        super.awardUsedRecipesAndPopExperience(serverPlayer);
    }
}
