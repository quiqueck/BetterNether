package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether ships {@code waystones:has_structure/blackstone_waystone} as compat data, so Waystones' wild Nether
 * waystones also generate in BetterNether biomes (Waystones itself only lists the vanilla Nether biomes). The test
 * suite runs without Waystones: the tag must still load (its entries are optional) and contain every BetterNether
 * biome.
 */
public class WaystonesBiomeTagGameTest {
    private static final TagKey<Biome> BLACKSTONE_WAYSTONE = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("waystones", "has_structure/blackstone_waystone")
    );

    @GameTest
    public void everyNetherBiomeHasWildWaystones(GameTestHelper helper) {
        final Registry<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final List<String> failures = new ArrayList<>();

        if (biomes.get(BLACKSTONE_WAYSTONE).isEmpty()) {
            failures.add("#" + BLACKSTONE_WAYSTONE.location() + " did not load");
        }

        int count = 0;
        for (ResourceKey<Biome> key : biomes.registryKeySet()) {
            if (!key.location().getNamespace().equals(BetterNether.C.namespace)) continue;
            count++;
            final Holder<Biome> holder = biomes.getOrThrow(key);
            if (!holder.is(BLACKSTONE_WAYSTONE)) {
                failures.add(key.location() + " is not in #" + BLACKSTONE_WAYSTONE.location());
            }
        }
        if (count < 22) failures.add("found only " + count + " BetterNether biomes");

        GameTestAssertions.failIfAny(helper, "Waystones compat biome tag", failures);
        helper.succeed();
    }
}
