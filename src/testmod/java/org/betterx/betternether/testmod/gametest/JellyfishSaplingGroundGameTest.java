package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * The jellyfish mushroom sapling belongs on nether nylium, not on End terrain (quiqueck/BetterNether#118).
 * <p>
 * BetterEnd puts its mosses ({@code end_moss}, {@code chorus_nylium}, ...) into {@code #minecraft:nylium} so
 * that vanilla's nylium mechanics apply to them, and a sapling whose ground rule was that vanilla tag
 * therefore grew on BetterEnd terrain. BetterEnd is not on the test classpath, so the testmod datapack
 * stands in for it: {@code data/minecraft/tags/block/nylium.json} adds {@code minecraft:end_stone} to the
 * vanilla tag exactly the way BetterEnd adds its mosses.
 */
public class JellyfishSaplingGroundGameTest {
    private static final BlockPos SOIL = new BlockPos(1, 1, 1);

    @GameTest
    public void jellyfishSaplingOnlySurvivesOnNetherNylium(GameTestHelper helper) {
        if (!Blocks.END_STONE.defaultBlockState().is(BlockTags.NYLIUM)) {
            throw helper.assertionException(Component.literal(
                    "Harness: the testmod datapack did not add end_stone to #minecraft:nylium"
            ));
        }

        final List<String> failures = new ArrayList<>();
        for (Block ground : List.of(
                NetherTerrainBlocks.MUSHROOM_GRASS,
                NetherTerrainBlocks.SEPIA_MUSHROOM_GRASS,
                NetherTerrainBlocks.JUNGLE_GRASS,
                NetherTerrainBlocks.SWAMPLAND_GRASS,
                Blocks.CRIMSON_NYLIUM,
                Blocks.WARPED_NYLIUM
        )) {
            if (!survivesOn(helper, ground)) failures.add("does not survive on nether nylium " + id(ground));
        }
        for (Block ground : List.of(Blocks.END_STONE, Blocks.NETHERRACK, Blocks.GRASS_BLOCK)) {
            if (survivesOn(helper, ground)) failures.add("survives on " + id(ground));
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Jellyfish mushroom sapling ground:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static boolean survivesOn(GameTestHelper helper, Block ground) {
        helper.setBlock(SOIL, ground);
        final BlockState sapling = NetherSaplingBlocks.JELLYFISH_MUSHROOM_SAPLING.defaultBlockState();
        return sapling.canSurvive(helper.getLevel(), helper.absolutePos(SOIL.above()));
    }

    private static String id(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }
}
