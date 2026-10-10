package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.blocks.BlockAnchorTreeSapling;
import org.betterx.betternether.registry.NetherGameRules;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * The giant willow and the giant anchor tree grow from a 2x2 of their saplings (GitHub #294).
 * <p>
 * Both giants are separate features from the tree a single sapling grows, and they are told apart by
 * what they are built from: the small willow is made of willow <em>trunk</em> blocks while the giant
 * ({@code OldWillowTree}) is made of willow log and bark, and only the giant anchor tree places anchor
 * tree log and bark at all - a lone ceiling-hung sapling grows the leafy branch. Each test also runs the
 * case that must <em>not</em> grow a giant, so a discriminator that miscounts cannot make it pass.
 * <p>
 * Both growth gamerules are flipped and restored inside one synchronous test body. Gamerules are
 * server-wide and other tests (notably {@link GameRuleGameTest}) read the defaults concurrently, so the
 * change must never be visible across a tick boundary.
 * <p>
 * Runs in the 32-cube arena: the giant anchor tree refuses to grow with less than 30 blocks between floor
 * and ceiling, and the arena's barrier ceiling at y=32 leaves exactly 30 over a sapling at y=1.
 */
public class GiantSaplingGameTest {
    private static final String HUGE_ARENA = "betternether-testmod:huge_empty";
    private static final int SIZE = 32;
    private static final BlockPos CORNER = new BlockPos(SIZE / 2 - 1, 1, SIZE / 2 - 1);

    /** A giant is several hundred blocks of wood; a stray block or two from anything else is not one. */
    private static final int MIN_GIANT_WOOD = 50;

    @GameTest(structure = HUGE_ARENA, maxTicks = 100)
    public void fourWillowSaplingsGrowAGiantWillow(GameTestHelper helper) {
        final Block sapling = NetherWoodBlocks.MAT_WILLOW.getSapling();
        final Block log = NetherWoodBlocks.MAT_WILLOW.getLog();
        final Block bark = NetherWoodBlocks.MAT_WILLOW.getBark();
        final Block trunk = NetherWoodBlocks.MAT_WILLOW.getTrunk();
        final List<String> failures = new ArrayList<>();

        // Control: a lone sapling grows the ordinary willow, which has no log or bark in it. The small
        // willow declines about one roll in three (WillowTreeFeature's headroom check can never pass for
        // its shortest height), leaving the sapling for the next tick, so give it a few rolls.
        clearArena(helper);
        helper.setBlock(CORNER, sapling);
        boolean grown = false;
        for (long seed = 1; seed <= 20 && !grown; seed++) {
            grown = grow(helper, CORNER, NetherGameRules.GROW_LARGE_WILLOWS, true, seed);
        }
        if (!grown) {
            failures.add("a single willow sapling did not grow at all - check the arena");
        } else if (count(helper, trunk) == 0 || count(helper, log) + count(helper, bark) > 0) {
            failures.add("a single willow sapling did not grow the small willow (trunk " + count(helper, trunk)
                    + ", log+bark " + (count(helper, log) + count(helper, bark)) + ") - the discriminator is broken");
        }

        // The 2x2, triggered from a sapling that is not the quad's own corner.
        clearArena(helper);
        plantQuad(helper, sapling.defaultBlockState());
        if (!grow(helper, CORNER.offset(1, 0, 1), NetherGameRules.GROW_LARGE_WILLOWS, true, 2L)) {
            failures.add("a 2x2 of willow saplings did not grow");
        } else {
            final int wood = count(helper, log) + count(helper, bark);
            if (wood < MIN_GIANT_WOOD) {
                failures.add("a 2x2 of willow saplings grew only " + wood + " willow log/bark blocks - that is not"
                        + " the giant willow");
            }
            if (count(helper, sapling) > 0) {
                failures.add(count(helper, sapling) + " of the four willow saplings are still standing");
            }
        }

        // With the rule off the same 2x2 grows an ordinary willow from the sapling that was ticked.
        clearArena(helper);
        plantQuad(helper, sapling.defaultBlockState());
        grow(helper, CORNER, NetherGameRules.GROW_LARGE_WILLOWS, false, 3L);
        if (count(helper, log) + count(helper, bark) > 0) {
            failures.add("grow_large_willows=false still grew the giant willow from a 2x2");
        }

        failIfAny(helper, "Giant willow growth regression (#294)", failures);
        helper.succeed();
    }

    @GameTest(structure = HUGE_ARENA, maxTicks = 100)
    public void fourFloorAnchorTreeSaplingsGrowAGiantAnchorTree(GameTestHelper helper) {
        final Block sapling = NetherWoodBlocks.MAT_ANCHOR_TREE.getSapling();
        final Block log = NetherWoodBlocks.MAT_ANCHOR_TREE.getLog();
        final Block bark = NetherWoodBlocks.MAT_ANCHOR_TREE.getBark();
        final BlockState standing = sapling.defaultBlockState().setValue(BlockAnchorTreeSapling.HANGING, false);
        final List<String> failures = new ArrayList<>();

        // Rule off (the default): a floor 2x2 waits rather than growing anything.
        clearArena(helper);
        plantQuad(helper, standing);
        if (grow(helper, CORNER, NetherGameRules.GROW_LARGE_ANCHOR_TREES, false, 4L)) {
            failures.add("with grow_large_anchor_trees=false a floor 2x2 of anchor tree saplings grew something");
        }
        if (count(helper, log) + count(helper, bark) > 0) {
            failures.add("with grow_large_anchor_trees=false a floor 2x2 grew anchor tree wood");
        }

        // Rule on: the 2x2 grows the giant, from floor to ceiling.
        clearArena(helper);
        plantQuad(helper, standing);
        if (!grow(helper, CORNER.offset(1, 0, 0), NetherGameRules.GROW_LARGE_ANCHOR_TREES, true, 5L)) {
            failures.add("with grow_large_anchor_trees=true a floor 2x2 of anchor tree saplings did not grow"
                    + " - it needs 30 blocks to the ceiling, check the arena height");
        } else {
            final int wood = count(helper, log) + count(helper, bark);
            if (wood < MIN_GIANT_WOOD) {
                failures.add("the 2x2 grew only " + wood + " anchor tree log/bark blocks - that is not the giant");
            }
            if (count(helper, sapling) > 0) {
                failures.add(count(helper, sapling) + " of the four anchor tree saplings are still standing");
            }
        }

        // A lone floor sapling grows nothing even with the rule on: the small tree hangs from a ceiling.
        clearArena(helper);
        helper.setBlock(CORNER, standing);
        if (grow(helper, CORNER, NetherGameRules.GROW_LARGE_ANCHOR_TREES, true, 6L)) {
            failures.add("a lone floor-planted anchor tree sapling grew something");
        }

        failIfAny(helper, "Giant anchor tree growth regression (#294)", failures);
        helper.succeed();
    }

    private static void plantQuad(GameTestHelper helper, BlockState state) {
        for (BlockPos rel : BlockPos.betweenClosed(CORNER, CORNER.offset(1, 0, 1))) {
            helper.setBlock(rel, state);
        }
    }

    /**
     * Grows the sapling at {@code rel} with {@code rule} set to {@code value}, restoring the rule before
     * returning so no other test ever sees it changed.
     */
    private static boolean grow(
            GameTestHelper helper,
            BlockPos rel,
            GameRule<Boolean> rule,
            boolean value,
            long seed
    ) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(rel);
        final BlockState state = level.getBlockState(abs);
        if (!(state.getBlock() instanceof FeatureSaplingBlock sapling)) {
            throw helper.assertionException(Component.literal(
                    "expected a FeatureSaplingBlock at " + rel + " but found " + state));
        }
        final var rules = level.getGameRules();
        final boolean original = rules.get(rule);
        rules.set(rule, value, level.getServer());
        try {
            return sapling.growFeatureNow(level, abs, state, RandomSource.create(seed));
        } finally {
            rules.set(rule, original, level.getServer());
        }
    }

    private static void clearArena(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        for (BlockPos rel : arena()) {
            final BlockState target = rel.getY() == 0
                    ? Blocks.NETHERRACK.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
            level.setBlock(helper.absolutePos(rel), target, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    private static int count(GameTestHelper helper, Block block) {
        int n = 0;
        for (BlockPos rel : arena()) {
            if (helper.getLevel().getBlockState(helper.absolutePos(rel)).is(block)) n++;
        }
        return n;
    }

    private static Iterable<BlockPos> arena() {
        return BlockPos.betweenClosed(0, 0, 0, SIZE - 1, SIZE - 1, SIZE - 1);
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
