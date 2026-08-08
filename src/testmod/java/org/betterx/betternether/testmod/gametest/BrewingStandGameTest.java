package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blockentities.BNBrewingStandBlockEntity;
import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code nether_brewing_stand}'s custom brews in {@code BrewingRegistry}, driven through the real
 * {@link BNBrewingStandBlockEntity#tick} loop rather than by calling {@code BrewingRegistry.getResult}
 * directly, so the ingredient check ({@code canCraft}) and the slot rewrite ({@code craft}) are both
 * exercised. {@code tick} is a plain static method, so the whole 400-tick brew is driven synchronously
 * inside a single test tick instead of waiting on the world clock.
 * <p>
 * The load-bearing test here is {@link #hookMushroomLeavesNonAwkwardPotionsAlone}: every potion is the
 * same {@code minecraft:potion} item, so the old {@code ItemStack.isSameItem} bottle comparison ignored
 * the {@code potion_contents} component and happily converted water, swiftness - any potion at all - into
 * healing. The intended contract is 26.3's generated {@code minecraft:brewing} recipe, whose input is
 * pinned to {@code potion_contents: {potions: minecraft:awkward}}.
 * <p>
 * The {@code inAVanillaBrewingStand} tests cover the other half of the contract: both brews have to work in
 * a plain {@code minecraft:brewing_stand} too. {@code BrewingRecipeRegistryMixin} used to patch only
 * {@code PotionBrewing.isIngredient}, which is what {@code canPlaceItem} consults - so a vanilla stand
 * accepted the reagent but {@code hasMix} still said no and it never brewed. Those tests assert acceptance
 * <em>and</em> completion together, because acceptance on its own is exactly the dead end that was the bug.
 */
public class BrewingStandGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    /** 1 tick to consume fuel and arm the stand, 400 to brew, plus slack. */
    private static final int BREW_TICKS = 450;

    @GameTest
    public void hookMushroomTurnsAnAwkwardPotionIntoHealing(GameTestHelper helper) {
        final ItemStack result = brew(helper, potion(Potions.AWKWARD), NetherMushroomBlocks.HOOK_MUSHROOM);

        if (!isPotion(result, Potions.HEALING)) {
            throw helper.assertionException(Component.literal(
                    "awkward potion + hook_mushroom brewed " + describe(result) + " instead of a healing potion"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void hookMushroomLeavesNonAwkwardPotionsAlone(GameTestHelper helper) {
        assertNotBrewedToHealing(helper, potion(Potions.WATER), "water bottle");
        assertNotBrewedToHealing(helper, potion(Potions.SWIFTNESS), "swiftness potion");
        helper.succeed();
    }

    @GameTest
    public void barrelCactusTurnsAGlassBottleIntoWater(GameTestHelper helper) {
        final ItemStack result = brew(helper, new ItemStack(Items.GLASS_BOTTLE), NetherPlantBlocks.BARREL_CACTUS);

        if (!isPotion(result, Potions.WATER)) {
            throw helper.assertionException(Component.literal(
                    "glass bottle + barrel_cactus brewed " + describe(result) + " instead of a water bottle"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void hookMushroomTurnsAnAwkwardPotionIntoHealingInAVanillaBrewingStand(GameTestHelper helper) {
        final ItemStack result = brewInVanillaStand(
                helper,
                potion(Potions.AWKWARD),
                NetherMushroomBlocks.HOOK_MUSHROOM
        );

        if (!isPotion(result, Potions.HEALING)) {
            throw helper.assertionException(Component.literal(
                    "awkward potion + hook_mushroom brewed " + describe(result) + " instead of a healing"
                            + " potion in a vanilla brewing_stand - a vanilla stand takes the reagent"
                            + " (canPlaceItem asks PotionBrewing.isIngredient, which BetterNether patches)"
                            + " but never brews unless PotionBrewing.hasMix and PotionBrewing.mix know the"
                            + " brew as well"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void barrelCactusTurnsAGlassBottleIntoWaterInAVanillaBrewingStand(GameTestHelper helper) {
        final ItemStack result = brewInVanillaStand(
                helper,
                new ItemStack(Items.GLASS_BOTTLE),
                NetherPlantBlocks.BARREL_CACTUS
        );

        if (!isPotion(result, Potions.WATER)) {
            throw helper.assertionException(Component.literal(
                    "glass bottle + barrel_cactus brewed " + describe(result) + " instead of a water bottle"
                            + " in a vanilla brewing_stand - a vanilla stand takes the reagent"
                            + " (canPlaceItem asks PotionBrewing.isIngredient, which BetterNether patches)"
                            + " but never brews unless PotionBrewing.hasMix and PotionBrewing.mix know the"
                            + " brew as well"
            ));
        }
        helper.succeed();
    }

    private static void assertNotBrewedToHealing(GameTestHelper helper, ItemStack bottle, String what) {
        final ItemStack before = bottle.copy();
        final ItemStack result = brew(helper, bottle, NetherMushroomBlocks.HOOK_MUSHROOM);

        if (isPotion(result, Potions.HEALING)) {
            throw helper.assertionException(Component.literal(
                    "a " + what + " + hook_mushroom brewed a healing potion - the bottle comparison in"
                            + " BrewingRegistry.BrewingRecipe is ignoring the potion_contents component,"
                            + " so every potion matches the awkward -> healing brew"
            ));
        }
        if (!ItemStack.isSameItemSameComponents(before, result)) {
            throw helper.assertionException(Component.literal(
                    "a " + what + " + hook_mushroom changed into " + describe(result)
                            + ", expected it to be left untouched"
            ));
        }
    }

    /**
     * Places a nether brewing stand, loads bottle slot 0, ingredient slot 3 and blaze powder into the fuel
     * slot, then runs the block entity's tick loop to completion and returns whatever ended up in slot 0.
     */
    private static ItemStack brew(GameTestHelper helper, ItemStack bottle, Block ingredient) {
        helper.setBlock(POS, NetherFunctionalBlocks.NETHER_BREWING_STAND);

        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(POS);
        final BNBrewingStandBlockEntity stand = standAt(helper, level, abs);

        stand.setItem(0, bottle);
        stand.setItem(3, new ItemStack(ingredient));
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));

        for (int i = 0; i < BREW_TICKS; i++) {
            // re-read both the state and the block entity: tick() writes the HAS_BOTTLE properties back
            // through level.setBlock, exactly as the real ticker would
            final BNBrewingStandBlockEntity current = standAt(helper, level, abs);
            BNBrewingStandBlockEntity.tick(level, abs, level.getBlockState(abs), current);
        }

        return standAt(helper, level, abs).getItem(0);
    }

    /**
     * The vanilla counterpart of {@link #brew}: places a plain {@code minecraft:brewing_stand} and drives
     * {@link BrewingStandBlockEntity#serverTick} the same way.
     * <p>
     * Before ticking it also asserts the stand would actually let a player put the reagent in slot 3 - that
     * affordance is what {@code PotionBrewing.isIngredient} controls, and it is only honest if the brew then
     * completes.
     */
    private static ItemStack brewInVanillaStand(GameTestHelper helper, ItemStack bottle, Block ingredient) {
        helper.setBlock(POS, Blocks.BREWING_STAND);

        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(POS);
        final BrewingStandBlockEntity stand = vanillaStandAt(helper, level, abs);

        final ItemStack reagent = new ItemStack(ingredient);
        if (!stand.canPlaceItem(3, reagent)) {
            throw helper.assertionException(Component.literal(
                    "a vanilla brewing_stand rejected " + describe(reagent) + " from its reagent slot;"
                            + " BrewingRecipeRegistryMixin should be reporting it through"
                            + " PotionBrewing.isIngredient"
            ));
        }

        stand.setItem(0, bottle);
        stand.setItem(3, reagent);
        stand.setItem(4, new ItemStack(Items.BLAZE_POWDER));

        for (int i = 0; i < BREW_TICKS; i++) {
            final BrewingStandBlockEntity current = vanillaStandAt(helper, level, abs);
            BrewingStandBlockEntity.serverTick(level, abs, level.getBlockState(abs), current);
        }

        return vanillaStandAt(helper, level, abs).getItem(0);
    }

    private static BrewingStandBlockEntity vanillaStandAt(GameTestHelper helper, ServerLevel level, BlockPos abs) {
        final BlockEntity be = level.getBlockEntity(abs);
        if (!(be instanceof BrewingStandBlockEntity stand)) {
            throw helper.assertionException(Component.literal(
                    "minecraft:brewing_stand did not create a BrewingStandBlockEntity, got " + be
            ));
        }
        return stand;
    }

    private static BNBrewingStandBlockEntity standAt(GameTestHelper helper, ServerLevel level, BlockPos abs) {
        final BlockEntity be = level.getBlockEntity(abs);
        if (!(be instanceof BNBrewingStandBlockEntity stand)) {
            throw helper.assertionException(Component.literal(
                    "nether_brewing_stand did not create a BNBrewingStandBlockEntity, got " + be
            ));
        }
        return stand;
    }

    private static ItemStack potion(Holder<Potion> potion) {
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    private static boolean isPotion(ItemStack stack, Holder<Potion> potion) {
        final PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return false;
        final Optional<Holder<Potion>> actual = contents.potion();
        return actual.isPresent() && actual.get().is(potion);
    }

    private static String describe(ItemStack stack) {
        final PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return stack.toString();
        return stack + " " + contents.potion().map(Holder::getRegisteredName).orElse("<no potion>");
    }
}
