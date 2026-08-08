package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.item.NetherResourceItems;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code agave_medicine}/{@code herbal_medicine}'s regeneration effect - power/amplifier is
 * passed straight through {@code new MobEffectInstance(effect, duration, amplifier)} with no
 * potion-style "level - 1" conversion, confirmed against vanilla's own constructor. "gave medicine" in the
 * original checklist, closest match to {@code NetherItems.registerMedicine("agave_medicine", 40, 2,
 * true)} (the leading "a" read as "gave"). Both are driven through
 * {@code Consumable#onConsume(Level, LivingEntity, ItemStack)} directly - the real vanilla effect-
 * application step a completed eat animation calls - rather than the plain {@code FoodProperties}
 * nutrition path, since the effect lives in the item's {@code DataComponents.CONSUMABLE} component,
 * not its food component.
 */
public class MedicineGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void agaveMedicineGrantsRegenerationFortyTicksAtPowerTwo(GameTestHelper helper) {
        assertRegeneration(helper, NetherResourceItems.AGAVE_MEDICINE, 40, 2);
    }

    @GameTest
    public void herbalMedicineGrantsRegenerationTenTicksAtPowerFive(GameTestHelper helper) {
        assertRegeneration(helper, NetherResourceItems.HERBAL_MEDICINE, 10, 5);
    }

    private static void assertRegeneration(
            GameTestHelper helper,
            net.minecraft.world.item.Item item,
            int expectedDuration,
            int expectedAmplifier
    ) {
        final ServerPlayer player = MockPlayers.survival(helper, POS);
        final ItemStack stack = new ItemStack(item);
        final Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        final List<String> failures = new ArrayList<>();
        if (consumable == null) {
            failures.add(item + " has no CONSUMABLE component at all");
        } else {
            consumable.onConsume(helper.getLevel(), player, stack);
            final MobEffectInstance effect = player.getEffect(MobEffects.REGENERATION);
            if (effect == null) {
                failures.add(item + " did not grant Regeneration");
            } else {
                if (effect.getAmplifier() != expectedAmplifier) {
                    failures.add(item + " granted amplifier " + effect.getAmplifier() + ", expected "
                            + expectedAmplifier);
                }
                if (effect.getDuration() > expectedDuration || effect.getDuration() <= 0) {
                    failures.add(item + " granted duration " + effect.getDuration() + ", expected up to "
                            + expectedDuration + " ticks");
                }
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Medicine regeneration regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
