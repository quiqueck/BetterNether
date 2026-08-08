package org.betterx.betternether.mixin.common;

import org.betterx.betternether.registry.BrewingRegistry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Teaches vanilla's brewing table about {@link BrewingRegistry}'s brews, so barrel cactus and hook
 * mushroom work in a <em>plain</em> {@code minecraft:brewing_stand} and not only in BetterNether's own
 * {@code nether_brewing_stand}.
 * <p>
 * Patching {@code isIngredient} alone (which is all this mixin used to do) was actively harmful:
 * {@code BrewingStandBlockEntity.canPlaceItem} and {@code BrewingStandMenu$IngredientsSlot.mayPlace} call
 * exactly that method, so a vanilla stand <em>accepted</em> both items into its reagent slot while
 * {@code isBrewable} - which additionally requires {@code hasMix} - kept returning false. The player got a
 * stand that swallowed the reagent and then sat there forever with no feedback.
 * <p>
 * The three methods injected here are the complete surface a brewing stand consults; verified against the
 * 26.2 bytecode of {@code BrewingStandBlockEntity}:
 * <ul>
 *     <li>{@code isBrewable} -> {@code isIngredient(reagent)} and {@code hasMix(bottle, reagent)}</li>
 *     <li>{@code doBrew} -> {@code mix(reagent, bottle)}</li>
 * </ul>
 * Covering all three is what closes the trap, and it lands these branches on the same player-facing
 * behaviour as 26.3, where both brews are native {@code minecraft:brewing} recipes.
 * <p>
 * <b>Why not {@code PotionBrewing.Builder} / Fabric's {@code FabricPotionBrewingBuilder}?</b> The builder
 * cannot express the barrel-cactus brew at all. {@code addContainer} and {@code addContainerRecipe} both run
 * {@code expectPotion}, which throws unless the item is a {@code PotionItem} - {@code GLASS_BOTTLE} is a
 * {@code BottleItem}. Even if a mix could be registered, {@code hasMix} short-circuits on
 * {@code isContainer(bottle)} and both {@code hasPotionMix} and {@code mix} bail out when the bottle carries
 * no {@code potion_contents} component, which an empty glass bottle never does. Vanilla's mix table is
 * structurally potion-to-potion; a glass-bottle-to-water brew only became expressible on 26.3, where
 * {@code minecraft:brewing} recipes replaced it.
 */
@Mixin(PotionBrewing.class)
public class BrewingRecipeRegistryMixin {
    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void bn_isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (BrewingRegistry.isValidIngridient(stack)) {
            info.setReturnValue(true);
        }
    }

    /**
     * Argument order is vanilla's: {@code hasMix(bottle, reagent)}, the reverse of {@code mix}.
     */
    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void bn_hasMix(ItemStack bottle, ItemStack reagent, CallbackInfoReturnable<Boolean> info) {
        if (BrewingRegistry.getResult(reagent, bottle) != null) {
            info.setReturnValue(true);
        }
    }

    /**
     * Argument order is vanilla's: {@code mix(reagent, bottle)}, the reverse of {@code hasMix}.
     * <p>
     * {@code doBrew} runs this over all three bottle slots including the empty ones;
     * {@code BrewingRegistry.getResult} returns null for an empty bottle, so those pass through to vanilla.
     */
    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void bn_mix(ItemStack reagent, ItemStack bottle, CallbackInfoReturnable<ItemStack> info) {
        final ItemStack result = BrewingRegistry.getResult(reagent, bottle);
        if (result != null) {
            info.setReturnValue(result.copy());
        }
    }
}
