package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockFireBowl;
import org.betterx.betternether.registry.block.NetherLightBlocks;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/** Covers all 6 fire bowl variants' light level per {@code FIRE} state. */
public class FireBowlGameTest {
    private static final Map<String, Block> VARIANTS = Map.of(
            "cincinnasite_fire_bowl", NetherLightBlocks.CINCINNASITE_FIRE_BOWL,
            "bricks_fire_bowl", NetherLightBlocks.BRICKS_FIRE_BOWL,
            "netherite_fire_bowl", NetherLightBlocks.NETHERITE_FIRE_BOWL,
            "cincinnasite_fire_bowl_soul", NetherLightBlocks.CINCINNASITE_FIRE_BOWL_SOUL,
            "bricks_fire_bowl_soul", NetherLightBlocks.BRICKS_FIRE_BOWL_SOUL,
            "netherite_fire_bowl_soul", NetherLightBlocks.NETHERITE_FIRE_BOWL_SOUL
    );

    @GameTest
    public void litBowlsGiveFullLightUnlitBowlsGiveNone(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        VARIANTS.forEach((name, block) -> {
            final int unlit = BlockFireBowl.getLuminance(block.defaultBlockState().setValue(BlockFireBowl.FIRE, false));
            final int lit = BlockFireBowl.getLuminance(block.defaultBlockState().setValue(BlockFireBowl.FIRE, true));
            if (unlit != 0) {
                failures.add(name + ": unlit luminance is " + unlit + ", expected 0");
            }
            if (lit != 15) {
                failures.add(name + ": lit luminance is " + lit + ", expected 15");
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Fire bowl luminance regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
