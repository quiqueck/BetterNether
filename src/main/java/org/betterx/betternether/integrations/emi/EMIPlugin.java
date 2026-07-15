package org.betterx.betternether.integrations.emi;

// TODO(1.21.7): EMI API - integration disabled during the 1.21.6 migration.
//  The bclib helper package org.betterx.bclib.integration.emi (EMIPlugin.getSprite / addAllRecipes)
//  is no longer available in the dependency, and the recipe-internals it relied on moved.
//  BetterEnd (same author) disabled its EMI integration the same way; the "emi" entrypoint in
//  fabric.mod.json has been renamed to "emi_disabled" so this plugin is not loaded.
//  Re-enable and port EMIPlugin/EMIForgeRecipe once a working EMI + bclib EMI API is available.
public class EMIPlugin {
}
