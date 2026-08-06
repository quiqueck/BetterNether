package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.item.NetherFoodItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the bowl-returning eat mechanic {@code NetherItems.registerMedicine(..., bowl=true)} gives
 * agave/herbal medicine: finishing a single medicine item must turn it into an empty
 * {@code stalagnate_bowl}, not just vanish. Driven directly through
 * {@code Item#finishUsingItem(ItemStack, Level, LivingEntity)} - the same method vanilla's eating
 * animation completion calls - rather than the full multi-tick eat animation.
 */
public class BowlGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void finishingASingleMedicineReturnsAnEmptyBowl(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS);
        final ItemStack single = new ItemStack(NetherResourceItems.AGAVE_MEDICINE, 1);
        final Level level = helper.getLevel();

        final ItemStack result = single.getItem().finishUsingItem(single, level, player);

        if (!result.is(NetherFoodItems.STALAGNATE_BOWL)) {
            throw helper.assertionException(Component.literal(
                    "finishing a single agave_medicine produced " + result + " instead of an empty stalagnate_bowl"
            ));
        }
        helper.succeed();
    }
}
