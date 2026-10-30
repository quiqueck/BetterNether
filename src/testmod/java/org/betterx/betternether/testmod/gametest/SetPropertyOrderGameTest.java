package org.betterx.betternether.testmod.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Regression for the wood-set {@code Stem} slot: it set {@code strength(0.5f)} in its slot hook, and the set's
 * {@code WOOD_BLOCK.netherWood()} (strength 2/3) ran after it and won, so stems took as long to break as logs.
 */
public class SetPropertyOrderGameTest {
    private static final String[] STEMS = {"stalagnate_stem", "mushroom_fir_stem", "nether_mushroom_stem"};

    @GameTest
    public void woodSetStemsKeepTheirOwnStrength(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (String path : STEMS) {
            final Block b = BuiltInRegistries.BLOCK.getValue(Identifier.parse("betternether:" + path));
            if (b == null || BuiltInRegistries.BLOCK.getKey(b).getPath().equals("air")) {
                failures.add(path + ": not registered");
                continue;
            }
            if (b.defaultDestroyTime() != 0.5F || b.getExplosionResistance() != 0.5F) {
                failures.add(path + ": strength " + b.defaultDestroyTime() + "/" + b.getExplosionResistance()
                        + ", expected 0.5/0.5");
            }
        }
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "stem strength:\n - " + String.join("\n - ", failures)));
        }
        helper.succeed();
    }
}
