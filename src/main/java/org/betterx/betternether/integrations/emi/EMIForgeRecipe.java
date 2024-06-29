package org.betterx.betternether.integrations.emi;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blockentities.BlockEntityForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

public class EMIForgeRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final RecipeHolder<SmeltingRecipe> recipe;
    private final int speedup;

    public EMIForgeRecipe(RecipeHolder<SmeltingRecipe> recipe, int speedup) {
        this.id = ResourceLocation.fromNamespaceAndPath(
                "emi",
                recipe.id().getNamespace() + "/" + recipe.id().getPath() + "/forge"
        );

        this.input = EmiIngredient.of(recipe.value().getIngredients().get(0));
        this.output = EmiStack.of(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()));
        this.recipe = recipe;
        this.speedup = speedup;
    }


    static void addAllRecipes(EmiRegistry emiRegistry, RecipeManager manager) {
        org.betterx.bclib.integration.emi.EMIPlugin.<SingleRecipeInput, SmeltingRecipe, EMIForgeRecipe>addAllRecipes(
                emiRegistry, manager, BetterNether.C.LOG,
                RecipeType.SMELTING, (recipe) -> new EMIForgeRecipe(recipe, BlockEntityForge.SPEEDUP)
        );
    }

    public EmiRecipeCategory getCategory() {
        return EMIPlugin.FORGE;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public List<EmiIngredient> getInputs() {
        return List.of(this.input);
    }

    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    public int getDisplayWidth() {
        return 82;
    }

    public int getDisplayHeight() {
        return 38;
    }

    public void addWidgets(WidgetHolder widgets) {
        widgets.addFillingArrow(24, 5, 50 * this.recipe.value().getCookingTime())
               .tooltip((mx, my) -> List.of(
                               ClientTooltipComponent.create(
                                       Component.translatable(
                                               "emi.cooking.time",
                                               (this.recipe.value().getCookingTime() / speedup) / 20.0F
                                       ).getVisualOrderText()
                               )
                       )
               );

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 24);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 24, 4000 / speedup, false, true, true);


        widgets.addText(
                Component
                        .translatable("emi.cooking.experience", this.recipe.value().getExperience())
                        .getVisualOrderText(),
                26, 28, -1, true
        );
        widgets.addSlot(this.input, 0, 4);
        widgets.addSlot(this.output, 56, 0).large(true).recipeContext(this);
    }
}
