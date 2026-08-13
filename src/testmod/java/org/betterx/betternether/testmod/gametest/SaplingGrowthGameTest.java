package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import org.betterx.betternether.world.features.GloomwoodTreeFeature;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the two ways a sapling turns into a tree: on its own over time, and from bone meal.
 * <p>
 * Natural growth is here because it silently stopped working. A {@link FeatureSaplingBlock} does all of
 * its growing in {@code randomTick()}, and a block only ever receives a random tick if its
 * {@code BlockBehaviour.Properties} asked for one. BetterNether's wood-set sapling slot hand-assembles the
 * pieces of bclib's sapling bundle instead of taking it whole, and it never asked - so six saplings
 * (gloomwood, willow, rubeus, mushroom fir, nether sakura, anchor tree) and the stalagnate seed registered
 * as blocks that could never grow by themselves. Nothing about that is visible: they place, they render,
 * they survive, they still accept bone meal, and only a player who plants one and waits ever finds out.
 * That is exactly the shape of bug a regression test is for, which is why "does it grow" is asserted here
 * rather than checked by hand once.
 * <p>
 * {@link #everySaplingAsksForRandomTicks} is the cheap sweep that covers the whole family; the three
 * growth tests below drive one sapling end to end, so that "asks for random ticks" cannot pass while the
 * growing itself is broken.
 *
 * <h2>Why these tests need their own structure</h2>
 * The game test framework encloses each arena in barriers sized to its structure, and the default
 * {@code fabric-gametest-api-v1:empty} is 8 blocks tall - which leaves 5 free blocks over a sapling
 * standing on the floor. A gloomwood needs {@link GloomwoodTreeFeature#MIN_CLEARANCE}, so in the default
 * arena the feature correctly declines and every growth assertion below fails for a reason that has
 * nothing to do with saplings. {@link #TALL_ARENA} is that same empty box, 24 tall.
 */
public class SaplingGrowthGameTest {
    /**
     * A 12x24x12 empty arena. The height is what matters (see the class notes); the width keeps the two
     * trees in {@link #aSaplingGrowsFromRandomTicksAlone} from growing into each other.
     */
    private static final String TALL_ARENA = "betternether-testmod:tall_empty";

    private static final BlockPos SOIL = new BlockPos(3, 0, 3);
    private static final BlockPos PLANT = new BlockPos(3, 1, 3);
    private static final BlockPos OAK_SOIL = new BlockPos(9, 0, 9);
    private static final BlockPos OAK_PLANT = new BlockPos(9, 1, 9);
    private static final BlockPos LIGHT = new BlockPos(10, 1, 9);
    private static final BlockPos PLAYER = new BlockPos(6, 1, 0);

    /** Plenty for the ~32 random ticks a 1-in-16 grow chance needs twice over, and it costs no server ticks. */
    private static final int MAX_RANDOM_TICKS = 5000;
    /**
     * Bone meal only takes on an {@code isBonemealSuccess} roll, so a survival player has to keep clicking.
     * <p>
     * A gloomwood off farmland rolls 1-in-16 and needs two takes (the {@code STAGE} step, then the tree),
     * so the applications needed are negative-binomial with a mean of 32. At 500 the chance of not getting
     * there is about 3e-13 - small enough that a failure here is a real regression rather than a bad run.
     */
    private static final int MAX_BONE_MEAL_CLICKS = 500;
    private static final int BONE_MEAL_STACK = 64;

    private static final int NOT_RANDOMLY_TICKING = -1;
    private static final int NEVER_GREW = 0;

    /**
     * Every registered feature sapling asks for random ticks.
     * <p>
     * This is the regression itself, stated directly: a sapling whose properties never called
     * {@code randomTicks()} is handed no random tick by {@code ServerLevel#tickChunk}, so its
     * {@code randomTick()} - the only place it grows - is dead code. Asserting the property is what lets
     * one cheap test cover every sapling in the mod at once; the growth tests below prove the property is
     * sufficient as well as necessary.
     */
    @GameTest
    public void everySaplingAsksForRandomTicks(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        for (Block sapling : featureSaplings()) {
            if (!sapling.defaultBlockState().isRandomlyTicking()) {
                failures.add(BuiltInRegistries.BLOCK.getKey(sapling) + " is a FeatureSaplingBlock but its"
                        + " state is not randomly ticking - it can never grow on its own, only from bone meal");
            }
        }

        // Control: if vanilla's own sapling reads as not randomly ticking, the check above is measuring
        // something other than what it claims and every result here is meaningless.
        if (!Blocks.OAK_SAPLING.defaultBlockState().isRandomlyTicking()) {
            failures.add("the vanilla oak sapling reads as not randomly ticking - this check is broken,"
                    + " not the mod");
        }

        failIfAny(helper, "Sapling random-tick regression", failures);
        helper.succeed();
    }

    /**
     * A gloomwood sapling planted on netherrack becomes a tree from random ticks alone - no player, no bone
     * meal.
     * <p>
     * Driven through the same gate {@code ServerLevel#tickChunk} applies rather than by calling
     * {@code randomTick()} outright: the bug was the missing property, and a loop that ticked the block
     * unconditionally would have grown a tree just as happily with the bug still in place. Real server
     * ticks are deliberately not used either - random ticking picks blocks by chance out of a whole
     * section, so a tick-bounded test on it would be flaky - but everything past the gate is the real code.
     */
    @GameTest(structure = TALL_ARENA, maxTicks = 200)
    public void aSaplingGrowsFromRandomTicksAlone(GameTestHelper helper) {
        final Block sapling = gloomwoodSapling();
        helper.setBlock(SOIL, Blocks.NETHERRACK);
        helper.setBlock(PLANT, sapling);

        // Vanilla control through the identical loop. Oak gates on light as well, hence the glowstone.
        helper.setBlock(OAK_SOIL, Blocks.DIRT);
        helper.setBlock(LIGHT, Blocks.GLOWSTONE);
        helper.setBlock(OAK_PLANT, Blocks.OAK_SAPLING);

        final List<String> failures = new ArrayList<>();
        report(helper, failures, "the gloomwood sapling", PLANT, driveRandomTicks(helper, PLANT, sapling));
        report(helper, failures, "the vanilla oak sapling control", OAK_PLANT,
                driveRandomTicks(helper, OAK_PLANT, Blocks.OAK_SAPLING));

        failIfAny(helper, "Natural sapling growth regression", failures);
        helper.succeed();
    }

    /**
     * A survival player grows a gloomwood sapling with bone meal.
     * <p>
     * Survival rather than creative because the two are entirely separate code: a creative click is
     * intercepted by {@code BoneMealItemMixin} and grows the tree outright, while a survival click goes
     * down vanilla's {@code BoneMealItem.growCrop} path through {@code isValidBonemealTarget},
     * {@code isBonemealSuccess} and {@code advanceTree}. Only the survival path can show that the
     * {@code STAGE} step and the success roll still work.
     * <p>
     * Clicking in a loop rather than once: the roll is 1-in-16 and two applications are needed, so a single
     * click proves nothing either way.
     * <p>
     * The hand is refilled before every click, which is what a survival player with more bone meal in their
     * inventory does. It is not a convenience: {@code BoneMealItem.growCrop} shrinks the stack on every
     * valid application, <em>outside</em> the {@code isBonemealSuccess} branch, so a single stack buys 64
     * rolls of a 1-in-16 chance and not the {@link #MAX_BONE_MEAL_CLICKS} the loop appears to allow. Two
     * takes out of 64 rolls fails about 8.5% of the time, which is exactly what this test used to do - fail
     * roughly one run in ten, on a growth path that was working. Refilling makes the click count the real
     * bound, and the count of bone meal actually consumed is what the assertion below reads.
     */
    @GameTest(structure = TALL_ARENA, maxTicks = 200)
    public void aSurvivalPlayerGrowsASaplingWithBoneMeal(GameTestHelper helper) {
        final Block sapling = gloomwoodSapling();
        helper.setBlock(SOIL, Blocks.NETHERRACK);
        helper.setBlock(PLANT, sapling);

        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);

        int clicks = 0;
        int spent = 0;
        while (clicks < MAX_BONE_MEAL_CLICKS && helper.getBlockState(PLANT).is(sapling)) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BONE_MEAL, BONE_MEAL_STACK));
            helper.useBlock(PLANT, player);
            spent += BONE_MEAL_STACK - player.getItemInHand(InteractionHand.MAIN_HAND).getCount();
            clicks++;
        }

        final List<String> failures = new ArrayList<>();
        if (helper.getBlockState(PLANT).is(sapling)) {
            failures.add("a survival player applied bone meal " + clicks + " times, spending " + spent
                    + " of it, and the gloomwood sapling is still standing" + roomHint(helper, PLANT));
        }

        if (spent == 0) {
            failures.add("the sapling grew but no bone meal was spent - whatever grew it, it was not the"
                    + " bone meal, so this test is not measuring what it claims");
        }

        failIfAny(helper, "Bone meal sapling growth regression", failures);
        helper.succeed();
    }

    /**
     * A creative player grows a gloomwood sapling with a single bone meal click.
     * <p>
     * The single click is the assertion. {@code BoneMealItemMixin} routes a creative player to
     * {@code growFeatureNow} precisely so the {@code STAGE} step does not cost a second click; going
     * through {@code performBonemeal} instead would still grow a tree, just one click later, and only
     * asserting on the first click tells the two apart.
     */
    @GameTest(structure = TALL_ARENA, maxTicks = 200)
    public void aCreativePlayerGrowsASaplingWithOneBoneMealClick(GameTestHelper helper) {
        final Block sapling = gloomwoodSapling();
        helper.setBlock(SOIL, Blocks.NETHERRACK);
        helper.setBlock(PLANT, sapling);

        final ServerPlayer player = MockPlayers.inLevel(helper, PLAYER);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BONE_MEAL, BONE_MEAL_STACK));

        helper.useBlock(PLANT, player);

        if (helper.getBlockState(PLANT).is(sapling)) {
            throw helper.assertionException(Component.literal(
                    "one bone meal click from a creative player left the gloomwood sapling standing -"
                            + " creative skips both the success roll and the STAGE step, so it should have"
                            + " grown on this click" + roomHint(helper, PLANT)
            ));
        }
        helper.succeed();
    }

    /**
     * Runs vanilla's random-tick gate against the block at {@code plant} until it stops being
     * {@code sapling}.
     *
     * @return the number of random ticks it took, {@link #NOT_RANDOMLY_TICKING} if the block refused the
     *         gate, or {@link #NEVER_GREW} if it took every tick and never grew
     */
    private static int driveRandomTicks(GameTestHelper helper, BlockPos plant, Block sapling) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(plant);

        for (int tick = 1; tick <= MAX_RANDOM_TICKS; tick++) {
            final BlockState state = level.getBlockState(abs);
            if (!state.is(sapling)) return tick;
            // The gate itself, as ServerLevel#tickChunk applies it: a state that does not ask for random
            // ticks is never offered one. Checking it here rather than ticking unconditionally is what
            // makes this test able to fail on the actual bug.
            if (!state.isRandomlyTicking()) return NOT_RANDOMLY_TICKING;
            state.randomTick(level, abs, level.getRandom());
        }
        return NEVER_GREW;
    }

    private static void report(
            GameTestHelper helper,
            List<String> failures,
            String what,
            BlockPos plant,
            int result
    ) {
        if (result == NOT_RANDOMLY_TICKING) {
            failures.add(what + " does not ask for random ticks, so the server never offers it one -"
                    + " it can only ever be grown with bone meal");
        } else if (result == NEVER_GREW) {
            failures.add(what + " took " + MAX_RANDOM_TICKS + " random ticks without growing"
                    + roomHint(helper, plant));
        }
    }

    /**
     * Appended to every "it did not grow" message. A feature that declines for lack of headroom and one
     * that is never asked to grow at all look identical from the outside, and the arena's own barrier
     * ceiling is the likeliest cause of the former - so the message says which of the two happened
     * instead of leaving the next reader to rediscover it.
     */
    private static String roomHint(GameTestHelper helper, BlockPos plant) {
        final BlockPos abs = helper.absolutePos(plant);
        if (GloomwoodTreeFeature.hasRoomToGrow(helper.getLevel(), abs)) return "";
        return " (there is less than the " + GloomwoodTreeFeature.MIN_CLEARANCE + " blocks of headroom a"
                + " gloomwood needs above " + abs + ", so the feature declined - check the test structure,"
                + " not the sapling)";
    }

    private static Block gloomwoodSapling() {
        return NetherWoodBlocks.MAT_GLOOMWOOD.getSapling();
    }

    /**
     * Every registered block that grows a feature the way a sapling does. Throws rather than returning an
     * empty list: a sweep over nothing passes silently, which would make the check above prove nothing.
     */
    private static List<Block> featureSaplings() {
        final List<Block> blocks = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof FeatureSaplingBlock<?, ?>) blocks.add(block);
        }
        if (blocks.isEmpty()) {
            throw new AssertionError("no registered block is a FeatureSaplingBlock - nothing to sweep");
        }
        return blocks;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
