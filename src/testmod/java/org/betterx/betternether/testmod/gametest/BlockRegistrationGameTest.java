package org.betterx.betternether.testmod.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

/**
 * Minimal smoke test for the 1.20.1 testmod: BetterNether registered its blocks on a dedicated server, and one
 * of them can actually be placed and read back in a level. Mostly exists so the compat-test sets have a
 * GameTest run that proves the mod loaded next to the third-party jars.
 */
public class BlockRegistrationGameTest implements FabricGameTest {
    private static final ResourceLocation PROBE_BLOCK = new ResourceLocation("betternether", "nether_mycelium");

    @GameTest(template = EMPTY_STRUCTURE)
    public void blocksAreRegisteredAndPlaceable(GameTestHelper helper) {
        final long registered = BuiltInRegistries.BLOCK.keySet()
                                                       .stream()
                                                       .filter(id -> id.getNamespace().equals("betternether"))
                                                       .count();
        helper.assertTrue(registered > 100, "expected BetterNether to register more than 100 blocks, found " + registered);

        final Block probe = BuiltInRegistries.BLOCK.getOptional(PROBE_BLOCK).orElse(Blocks.AIR);
        helper.assertTrue(probe != Blocks.AIR, PROBE_BLOCK + " is not registered");

        final BlockPos pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, probe);
        helper.assertBlockPresent(probe, pos);
        helper.succeed();
    }
}
