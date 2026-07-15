package org.betterx.betternether.integrations.emi;

// TODO(1.21.7): EMI API - integration disabled during the 1.21.6 migration.
//  See EMIPlugin.java in this package. This recipe wrapper relied on the removed
//  org.betterx.bclib.integration.emi.EMIPlugin helper and on recipe internals
//  (getIngredients()/getResultItem()/getCookingTime()/getExperience()) that moved off Recipe.
//  Re-implement against the new AbstractCookingRecipe API (input()/assemble()/cookingTime()/experience())
//  and a working EMI + bclib EMI API when re-enabling the integration.
public class EMIForgeRecipe {
}
