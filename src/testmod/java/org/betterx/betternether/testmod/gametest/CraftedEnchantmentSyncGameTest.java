package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.NetherEnchantments;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.util.HashOps;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression guard for BetterNether #262 / #275 / #277 (reported on 1.21.1): crafting a Nether Ruby
 * pickaxe disconnected the player with {@code Can't find id for Reference{... betternether:obsidian_breaker}
 * ... Failed to encode packet container_click}.
 * <p>
 * The default enchantments are added to a crafted stack by Wover's {@code CraftingRecipeMixin}
 * ({@code ItemWithCustomStack#setupItemStack}). If the {@link Holder} it puts on the stack does not come
 * from the registries the connection encodes with, {@code ByteBufCodecs.holderRegistry} cannot map the
 * enchantment value to a network id and the packet fails to encode. The tests here therefore run the
 * exact server crafting path ({@code CraftingMenu#slotChangedCraftingGrid}: recipe lookup, then
 * {@code assemble}) and push the result through both network forms an item takes:
 * <ul>
 *   <li>{@link ItemStack#STREAM_CODEC} on a {@link RegistryFriendlyByteBuf} bound to the level's
 *       registries ({@code container_set_slot}, and pre-1.21.5 {@code container_click});</li>
 *   <li>{@link HashedStack} built with the same hash generator a {@code ServerPlayer} uses, wrapped in a
 *       {@link ServerboundContainerClickPacket} and round-tripped through its codec (1.21.5+
 *       {@code container_click}).</li>
 * </ul>
 * {@link #harnessDetectsForeignEnchantmentHolder} is the control: an enchantment value that is not the
 * registry's own instance must make the very same harness fail, otherwise a green sweep proves nothing.
 */
public class CraftedEnchantmentSyncGameTest {
    @GameTest
    public void craftedDefaultEnchantmentsSurviveNetworkEncoding(GameTestHelper helper) {
        final var level = helper.getLevel();
        final RegistryAccess access = level.registryAccess();
        final Item rubyPickaxe = NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT);

        final List<String> failures = new ArrayList<>();
        final List<String> checked = new ArrayList<>();
        boolean sawRubyPickaxe = false;

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof ShapedRecipe shaped)) continue;
            final CraftingInput input = inputFor(shaped);
            if (input == null) continue;

            // Same lookup CraftingMenu#slotChangedCraftingGrid performs before assembling.
            final Optional<RecipeHolder<CraftingRecipe>> found = level.getServer()
                                                                     .getRecipeManager()
                                                                     .getRecipeFor(RecipeType.CRAFTING, input, level);
            if (found.isEmpty()) continue;
            final ItemStack result = found.get().value().assemble(input, access);
            if (!(result.getItem() instanceof ItemWithCustomStack)) continue;

            final String name = BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
            checked.add(name);
            if (result.is(rubyPickaxe)) {
                sawRubyPickaxe = true;
                final int obsidianBreaker = EnchantmentUtils.getItemEnchantmentLevel(
                        level, NetherEnchantments.OBSIDIAN_BREAKER.key(), result
                );
                if (obsidianBreaker != 1) {
                    failures.add(name + ": expected the crafted stack to carry obsidian_breaker 1 but had "
                            + obsidianBreaker + " - the sweep would not exercise the reported enchantment");
                }
            }

            final String problem = networkProblem(result, access);
            if (problem != null) failures.add(name + ": " + problem);
        }

        if (!sawRubyPickaxe) {
            failures.add("no shaped crafting recipe produced " + BuiltInRegistries.ITEM.getKey(rubyPickaxe)
                    + " (checked: " + checked + ")");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Crafted default enchantments do not survive network encoding:\n - "
                            + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void harnessDetectsForeignEnchantmentHolder(GameTestHelper helper) {
        final RegistryAccess access = helper.getLevel().registryAccess();
        final Holder<Enchantment> real = access.lookupOrThrow(Registries.ENCHANTMENT)
                                               .getOrThrow(NetherEnchantments.OBSIDIAN_BREAKER.key());
        // An equal-looking value that is not the registry's own instance - what a holder from another
        // RegistryAccess (e.g. the integrated server's, seen from the client) looks like to the encoder.
        final Enchantment e = real.value();
        final Holder<Enchantment> foreign = Holder.direct(
                new Enchantment(e.description(), e.definition(), e.exclusiveSet(), e.effects())
        );

        final ItemStack stack = new ItemStack(NetherEquipmentItems.NETHER_RUBY_SET.<Item>get(ToolSlot.PICKAXE_SLOT));
        final ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(foreign, 1);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

        if (networkProblem(stack, access) == null) {
            throw helper.assertionException(Component.literal(
                    "control failed: a stack with a foreign obsidian_breaker holder encoded fine, so the "
                            + "network sweep cannot detect the #277 failure mode"
            ));
        }
        helper.succeed();
    }

    /** First matching item of every ingredient, laid out in the recipe's own grid. */
    private static CraftingInput inputFor(ShapedRecipe recipe) {
        final List<ItemStack> items = new ArrayList<>();
        for (Optional<Ingredient> ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                items.add(ItemStack.EMPTY);
                continue;
            }
            final Optional<Holder<Item>> first = ingredient.get().items().findFirst();
            if (first.isEmpty()) return null;
            items.add(new ItemStack(first.get()));
        }
        return CraftingInput.of(recipe.getWidth(), recipe.getHeight(), items);
    }

    /** @return null if the stack round-trips through both network forms, otherwise what went wrong. */
    private static String networkProblem(ItemStack stack, RegistryAccess access) {
        try {
            final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
            ItemStack.STREAM_CODEC.encode(buf, stack);
            final ItemStack decoded = ItemStack.STREAM_CODEC.decode(buf);
            if (!ItemStack.matches(stack, decoded)) {
                return "ItemStack.STREAM_CODEC round trip changed the stack: " + stack + " -> " + decoded;
            }
        } catch (RuntimeException ex) {
            return "ItemStack.STREAM_CODEC failed: " + ex;
        }

        try {
            // The generator ServerPlayer installs on its container listener; the client builds the same
            // one from its own registries for container_click.
            final var hashOps = access.createSerializationContext(HashOps.CRC32C_INSTANCE);
            final HashedPatchMap.HashGenerator hasher = component -> component
                    .encodeValue(hashOps)
                    .getOrThrow(msg -> new IllegalArgumentException("Failed to hash " + component + ": " + msg))
                    .asInt();

            final HashedStack hashed = HashedStack.create(stack, hasher);
            final ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(
                    1, 1, (short) 0, (byte) 0, ClickType.PICKUP, Int2ObjectMaps.singleton(0, hashed), hashed
            );
            final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
            ServerboundContainerClickPacket.STREAM_CODEC.encode(buf, packet);
            final ServerboundContainerClickPacket decoded = ServerboundContainerClickPacket.STREAM_CODEC.decode(buf);
            if (!decoded.carriedItem().matches(stack, hasher)) {
                return "container_click round trip: the hashed carried item no longer matches the stack";
            }
        } catch (RuntimeException ex) {
            return "container_click encoding failed: " + ex;
        }
        return null;
    }
}
