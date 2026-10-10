package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import de.ambertation.wover.test.api.gametest.GameTestAssertions;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * The worldgen-only trunks of the willow, the mushroom fir and the stalagnate are the wood of those trees, so
 * they are in {@code #minecraft:logs} (and the material's {@code #<name>_logs}) like the logs and barks. That is
 * what tree-felling mods (FallingTree) and leaf decay look for. Nether woods do not burn, so the trunks stay out
 * of {@code #minecraft:logs_that_burn}. The willow branch is the hanging, glowing vine under the willow leaves,
 * not wood, and deliberately stays out of {@code #minecraft:logs}.
 */
public class TrunkLogTagGameTest {
    /** 26.3 dropped the {@code BlockTags.LOGS_THAT_BURN} handle; vanilla still ships the tag. */
    private static final TagKey<Block> LOGS_THAT_BURN = TagKey.create(
            Registries.BLOCK,
            Identifier.withDefaultNamespace("logs_that_burn")
    );

    private static final BlockPos TRUNK = new BlockPos(1, 2, 1);
    private static final BlockPos TRUNK_LEAF = new BlockPos(2, 2, 1);
    private static final BlockPos CONTROL = new BlockPos(1, 2, 3);
    private static final BlockPos CONTROL_LEAF = new BlockPos(2, 2, 3);

    private static Map<Block, WoodenBlockSet<?>> trunks() {
        return Map.of(
                NetherWoodBlocks.MAT_WILLOW.getTrunk(), NetherWoodBlocks.MAT_WILLOW,
                NetherWoodBlocks.MAT_MUSHROOM_FIR.getTrunk(), NetherWoodBlocks.MAT_MUSHROOM_FIR,
                NetherWoodBlocks.MAT_STALAGNATE.getTrunk(), NetherWoodBlocks.MAT_STALAGNATE
        );
    }

    private static String id(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    @GameTest
    public void trunksAreLogs(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (var entry : trunks().entrySet()) {
            final BlockState trunk = entry.getKey().defaultBlockState();
            final String id = id(entry.getKey());
            if (!id.startsWith(BetterNether.C.namespace + ":")) failures.add(id + " is not a BetterNether block");
            if (!trunk.is(BlockTags.LOGS)) failures.add(id + " is not in #minecraft:logs");
            if (!trunk.is(entry.getValue().logsBlocksTag)) {
                failures.add(id + " is not in #" + entry.getValue().logsBlocksTag.location());
            }
            if (!trunk.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) {
                failures.add(id + " is not in #minecraft:prevents_nearby_leaf_decay");
            }
            if (!trunk.is(BlockTags.MINEABLE_WITH_AXE)) failures.add(id + " is not in #minecraft:mineable/axe");
            if (trunk.is(LOGS_THAT_BURN)) failures.add(id + " is in #minecraft:logs_that_burn, but Nether wood does not burn");
        }
        final Block branch = NetherWoodBlocks.MAT_WILLOW.getBranch();
        if (branch.defaultBlockState().is(BlockTags.LOGS)) {
            failures.add(id(branch) + " (a hanging vine, not wood) is in #minecraft:logs");
        }
        GameTestAssertions.failIfAny(helper, "Tree trunks vs #minecraft:logs", failures);
        helper.succeed();
    }

    /**
     * A non-persistent leaf next to a willow trunk relaxes to distance 1, like next to any log. The control leaf
     * next to stone keeps the decay distance.
     */
    @GameTest(maxTicks = 40)
    public void leavesNextToTrunkDoNotDecay(GameTestHelper helper) {
        final BlockState decaying = Blocks.OAK_LEAVES.defaultBlockState()
                                                     .setValue(LeavesBlock.PERSISTENT, false)
                                                     .setValue(LeavesBlock.DISTANCE, LeavesBlock.DECAY_DISTANCE);
        helper.setBlock(TRUNK_LEAF, decaying);
        helper.setBlock(CONTROL_LEAF, decaying);
        // Placing the neighbour afterwards runs the leaves' updateShape, which schedules the distance update.
        helper.setBlock(TRUNK, NetherWoodBlocks.MAT_WILLOW.getTrunk().defaultBlockState());
        helper.setBlock(CONTROL, Blocks.STONE.defaultBlockState());

        helper.succeedWhen(() -> {
            final BlockState leaf = helper.getBlockState(TRUNK_LEAF);
            if (!leaf.is(Blocks.OAK_LEAVES) || leaf.getValue(LeavesBlock.DISTANCE) != 1) {
                throw helper.assertionException(Component.literal("leaf next to the willow trunk: " + leaf));
            }
            final BlockState control = helper.getBlockState(CONTROL_LEAF);
            if (control.is(Blocks.OAK_LEAVES) && control.getValue(LeavesBlock.DISTANCE) != LeavesBlock.DECAY_DISTANCE) {
                throw helper.assertionException(Component.literal("control leaf next to stone: " + control));
            }
        });
    }
}
