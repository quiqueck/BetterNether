package org.betterx.datagen.betternether.recipes;

import org.betterx.betternether.BN;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.Recipe;

/**
 * BetterNether's two extra brews, as ordinary {@code minecraft:brewing} recipes.
 * <p>
 * Up to 26.1 these lived in {@code BrewingRegistry}, a Java-side table that {@code BNBrewingStandBlockEntity}
 * consulted before the recipe manager, because {@code PotionBrewing} was a hard-coded registry with no room
 * for a mod's own entries other than a mixin. 26.3 made brewing a datapack recipe type, so the table has no
 * reason to exist any more and the two brews are generated here instead.
 * <p>
 * {@link net.minecraft.data.recipes.BrewingRecipeBuilder} is deliberately not used: it derives the recipe id
 * from the input item's namespace (which would put these under {@code minecraft:}) and its {@code defaultId}
 * requires the input to pin an exact potion, which the glass-bottle recipe below does not. The ids still
 * follow vanilla's {@code brewing/<input item>_<input potion>_<reagent>} shape.
 */
public class NetherBrewingRecipes extends WoverRecipeProvider {
    public NetherBrewingRecipes(ModCore modCore) {
        super(modCore, "BetterNether - Brewing Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        // Barrel cactus stores water, so brewing it onto an empty bottle fills the bottle. The input has no
        // potion predicate because a glass bottle carries no potion contents at all.
        brewingRecipe(
                context,
                BN.id("brewing/glass_bottle_barrel_cactus"),
                PotionIngredient.of(Items.GLASS_BOTTLE),
                NetherPlantBlocks.BARREL_CACTUS.asItem(),
                potionOutput(Potions.WATER)
        );

        brewingRecipe(
                context,
                BN.id("brewing/potion_awkward_hook_mushroom"),
                PotionIngredient.of(Items.POTION, PotionsPredicate.ofPotion(Potions.AWKWARD)),
                NetherMushroomBlocks.HOOK_MUSHROOM.asItem(),
                potionOutput(Potions.HEALING)
        );
    }

    private static void brewingRecipe(
            RecipeBuilder.Context context,
            Identifier id,
            PotionIngredient input,
            Item reagent,
            ItemStackTemplate output
    ) {
        context.recipeOutput().accept(
                ResourceKey.<Recipe<?>>create(Registries.RECIPE, id),
                new BrewingRecipe(input, PotionIngredient.of(reagent), output),
                // Brewing recipes are never unlocked by an advancement; vanilla's own builder throws if you
                // try to attach one.
                null
        );
    }

    private static ItemStackTemplate potionOutput(Holder<Potion> potion) {
        return new ItemStackTemplate(
                Items.POTION,
                DataComponentPatch
                        .builder()
                        .set(DataComponents.POTION_CONTENTS, new PotionContents(potion))
                        .build()
        );
    }
}
