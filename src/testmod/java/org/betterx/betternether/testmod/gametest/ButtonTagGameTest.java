package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Every BetterNether {@link ButtonBlock} is in {@code #minecraft:buttons} (block and item), like vanilla's
 * buttons. The set-built wood buttons got that from {@code BUTTON_BLOCK}; the standalone bone and cincinnasite
 * buttons did not, so other mods and vanilla tag consumers did not see them as buttons (and on 26.3, where
 * {@code #minecraft:buttons} is part of {@code #minecraft:washed_away_by_fluids}, fluids no longer broke them).
 */
public class ButtonTagGameTest {
    private static final TagKey<Item> ITEM_BUTTONS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("buttons"));

    @GameTest
    public void everyButtonIsInButtonsTag(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        int count = 0;
        for (Block button : BuiltInRegistries.BLOCK) {
            final String id = BuiltInRegistries.BLOCK.getKey(button).toString();
            if (!(button instanceof ButtonBlock) || !id.startsWith(BetterNether.C.namespace + ":")) continue;
            count++;
            if (!button.defaultBlockState().is(BlockTags.BUTTONS)) {
                failures.add(id + " is not in #minecraft:buttons");
            }
            if (!button.asItem().builtInRegistryHolder().is(ITEM_BUTTONS)) {
                failures.add(id + " (item) is not in #minecraft:buttons");
            }
        }
        if (count < 13) failures.add("found only " + count + " buttons");
        GameTestAssertions.failIfAny(helper, "Buttons vs #minecraft:buttons", failures);
        helper.succeed();
    }
}
