package org.betterx.betternether.integrations.jei;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.block.NetherFunctionalBlocks;

import net.minecraft.resources.Identifier;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class JeiPlugin implements IModPlugin {
    private static final Identifier UID = BetterNether.C.mk("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // The Cincinnasite Forge is just a faster vanilla smelting furnace (see BlockEntityForge) -
        // no custom recipe type, just an extra catalyst for JEI's built-in Smelting category.
        registration.addCraftingStation(RecipeTypes.SMELTING, NetherFunctionalBlocks.CINCINNASITE_FORGE);
    }
}
