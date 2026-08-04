package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.item.api.ItemStackHelper;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the enchantments Nether Ruby and Fireruby gear is supposed to come with out of the box.
 * <p>
 * Two independent paths put them there, and both have to keep working:
 * <ul>
 *   <li>{@code ItemWithCustomStack#setupItemStack}, driven by
 *       {@code ItemStackHelper#callItemStackSetupIfPossible} - this is what the creative tab and
 *       {@code /give} use;</li>
 *   <li>{@code SmithingTransformRecipe#assemble}, via Wover's mixin - Fireruby gear is smithing-only,
 *       so a broken mixin there means the gear is simply unobtainable with its enchantments in
 *       survival.</li>
 * </ul>
 */
public class DefaultEnchantmentGameTest {
    private record Expectation(String name, Item item, ResourceKey<Enchantment> enchantment, int level) {}

    private static List<Expectation> expectations() {
        return List.of(
                new Expectation(
                        "nether_ruby_pickaxe",
                        NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT),
                        NetherEnchantments.OBSIDIAN_BREAKER.key(), 1
                ),
                new Expectation(
                        "flaming_ruby_pickaxe",
                        NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT),
                        NetherEnchantments.OBSIDIAN_BREAKER.key(), 3
                ),
                new Expectation(
                        "flaming_ruby_pickaxe",
                        NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT),
                        NetherEnchantments.RUBY_FIRE.key(), 1
                ),
                new Expectation(
                        "flaming_ruby_pickaxe",
                        NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT),
                        Enchantments.MENDING, 1
                ),
                new Expectation(
                        "flaming_ruby_sword",
                        NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.SWORD_SLOT),
                        NetherEnchantments.RUBY_FIRE.key(), 1
                ),
                new Expectation(
                        "flaming_ruby_chestplate",
                        NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ArmorSlot.CHESTPLATE_SLOT),
                        NetherEnchantments.RUBY_FIRE.key(), 1
                )
        );
    }

    /** The creative-tab and {@code /give} path. */
    @GameTest
    public void customStackSetupAppliesDefaultEnchantments(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        for (Expectation expected : expectations()) {
            final ItemStack stack = new ItemStack(expected.item());
            ItemStackHelper.callItemStackSetupIfPossible(stack, helper.getLevel().registryAccess());

            final int level = EnchantmentUtils.getItemEnchantmentLevel(
                    helper.getLevel(), expected.enchantment(), stack
            );
            if (level != expected.level()) {
                failures.add(expected.name() + ": expected "
                        + expected.enchantment().location() + " " + expected.level()
                        + " but the stack had level " + level);
            }
        }

        failIfAny(helper, "Default enchantment regression (creative tab / give path)", failures);
        helper.succeed();
    }

    /**
     * The smithing path. Resolves the real recipe from the recipe manager and assembles it, so Wover's
     * {@code SmithingTransformRecipeMixin} has to fire for this to pass.
     */
    @GameTest
    public void smithingUpgradeAppliesDefaultEnchantments(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final Item base = NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT);
        final Item result = NetherEquipmentItems.FLAMING_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT);

        // Look the recipe up by id rather than by assembling every smithing recipe and comparing the
        // result: SmithingTransformRecipe#assemble derives its output from the base stack on this
        // branch, so assembling with an empty input yields an empty stack and matches nothing.
        final ResourceLocation recipeId = ResourceLocation.parse("betternether:flaming_ruby_pickaxe");
        final RecipeHolder<?> holder = helper.getLevel()
                                             .recipeAccess()
                                             .getRecipes()
                                             .stream()
                                             .filter(r -> r.id().location().equals(recipeId))
                                             .findFirst()
                                             .orElse(null);

        if (holder == null) {
            helper.fail(Component.literal(
                    recipeId + " is not registered - Fireruby gear cannot be crafted at all"
            ));
            return;
        }
        if (!(holder.value() instanceof SmithingTransformRecipe)) {
            helper.fail(Component.literal(
                    recipeId + " is no longer a smithing_transform recipe: " + holder.value().getClass()
            ));
            return;
        }

        final ItemStack assembled = ((SmithingRecipe) holder.value())
                .assemble(new SmithingRecipeInput(ItemStack.EMPTY, new ItemStack(base), ItemStack.EMPTY),
                        helper.getLevel().registryAccess());

        if (!assembled.is(result)) {
            failures.add("the smithing recipe produced " + assembled + " instead of " + result);
        } else {
            requireEnchantment(helper, assembled, NetherEnchantments.RUBY_FIRE.key(), 1, failures);
            requireEnchantment(helper, assembled, NetherEnchantments.OBSIDIAN_BREAKER.key(), 3, failures);
            requireEnchantment(helper, assembled, Enchantments.MENDING, 1, failures);
        }

        failIfAny(helper, "Default enchantment regression (smithing path)", failures);
        helper.succeed();
    }

    private static void requireEnchantment(
            GameTestHelper helper,
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int expected,
            List<String> failures
    ) {
        final int level = EnchantmentUtils.getItemEnchantmentLevel(helper.getLevel(), enchantment, stack);
        if (level != expected) {
            failures.add("smithing result: expected " + enchantment.location() + " " + expected
                    + " but had level " + level);
        }
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
