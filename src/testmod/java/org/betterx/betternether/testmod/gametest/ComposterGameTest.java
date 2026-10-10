package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherWoodBlocks;
import org.betterx.bclib.api.v2.ComposterAPI;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers composting of modded compostables through both routes a player can use: clicking the item in by
 * hand, and letting a hopper feed it.
 * <p>
 * Modded compostables carry a real {@code minecraft:compostable} component, set at registration from a
 * {@code CompostableBlockTrait}/{@code CompostableItemTrait} via {@code CompostTier}, so vanilla drives every
 * route unaided. That is worth a test precisely because the two routes are entirely separate vanilla code and
 * an earlier implementation resolved compostability through mixins and got one of them wrong: clicking goes
 * through {@code ComposterBlock.useItemOn}, while a hopper inserts through the container handed out by
 * {@code getContainer}, whose {@code InputContainer.canPlaceItemThroughFace} reads the component itself and
 * whose {@code InputContainer.setChanged} reads it a <em>second</em> time to build the value it hands to
 * {@code ComposterBlock.addLayer}. The symptom was a sapling that composted fine by hand and sat in the
 * hopper forever - and, once the face check alone was patched, one that was destroyed instead. Reading the
 * code cannot tell the routes apart, so both are actually driven here, and
 * {@link #everyCompostableIsAcceptedThroughTheComposterFace} additionally exercises the container directly to
 * catch a face-check regression without needing a real hopper per item.
 * <p>
 * Every assertion below is on a composter that starts <em>empty</em>, which is what makes them deterministic:
 * every tier's number provider dispatches on {@code composter[level=0]} and returns a full layer there, so an
 * empty composter always accepts the first compostable. Starting anywhere else would make these tests a coin
 * flip.
 */
public class ComposterGameTest {
    private static final BlockPos COMPOSTER = new BlockPos(1, 1, 1);
    private static final BlockPos HOPPER = new BlockPos(1, 2, 1);
    private static final BlockPos PLAYER = new BlockPos(2, 1, 1);
    private static final int STACK = 16;

    /**
     * The manual route, end to end: a survival player holding saplings clicks the composter.
     * <p>
     * Survival rather than creative on purpose - {@code ItemStack.consume} is a no-op for a player with
     * infinite materials, so a creative player would compost without the stack ever shrinking and the
     * "one item was spent" half of this would be untestable.
     */
    @GameTest
    public void aPlayerFillsAnEmptyComposterByHand(GameTestHelper helper) {
        helper.setBlock(COMPOSTER, Blocks.COMPOSTER);
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(sapling(), STACK));

        helper.useBlock(COMPOSTER, player);

        final List<String> failures = new ArrayList<>();
        final int level = helper.getBlockState(COMPOSTER).getValue(ComposterBlock.LEVEL);
        if (level != 1) {
            failures.add("clicking a gloomwood sapling into an empty composter left it at level " + level
                    + ", expected 1 - an empty composter always accepts the first compostable");
        }

        final int left = player.getItemInHand(InteractionHand.MAIN_HAND).getCount();
        if (left != STACK - 1) {
            failures.add("the player is holding " + left + " saplings after composting one, expected "
                    + (STACK - 1));
        }

        failIfAny(helper, "Manual composting regression", failures);
        helper.succeed();
    }

    /**
     * The hopper route, end to end: a real hopper above a real composter, ticked until it moves something.
     * <p>
     * Asserts the fill level as well as the item leaving the hopper. The item leaving is only half the
     * story on 26.3: the container's face check ({@code canPlaceItemThroughFace}) and its own second
     * component read in {@code setChanged} are two separate, independently patchable gaps - the item could
     * leave the hopper via the first without ever reaching level 1 if only the first were patched. The level
     * rising is what proves the insert was actually composted rather than swallowed and lost.
     */
    @GameTest(maxTicks = 200)
    public void aHopperFillsAnEmptyComposter(GameTestHelper helper) {
        helper.setBlock(COMPOSTER, Blocks.COMPOSTER);
        helper.setBlock(HOPPER, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));

        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(HOPPER));
        if (!(be instanceof HopperBlockEntity hopper)) {
            throw helper.assertionException(Component.literal("the hopper did not create a HopperBlockEntity"));
        }
        hopper.setItem(0, new ItemStack(sapling(), STACK));

        helper.succeedWhen(() -> {
            final int left = hopper.getItem(0).getCount();
            if (left >= STACK) {
                throw helper.assertionException(Component.literal(
                        "after " + helper.getTick() + " ticks the hopper above the composter still holds all "
                                + STACK + " gloomwood saplings - the composter refuses them from a hopper"
                ));
            }

            final int level = helper.getBlockState(COMPOSTER).getValue(ComposterBlock.LEVEL);
            if (level != 1) {
                throw helper.assertionException(Component.literal(
                        "the hopper spent " + (STACK - left) + " saplings but the composter is at level "
                                + level + ", expected 1 - the item was consumed without being composted"
                ));
            }
        });
    }

    /**
     * The manual route for every item we declare compostable, not just the one sapling above. The composter
     * is emptied between items so each one is checked against the deterministic level-0 rule.
     */
    @GameTest
    public void everyCompostableFillsAnEmptyComposterByHand(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final List<String> failures = new ArrayList<>();

        for (Item item : compostables()) {
            helper.setBlock(COMPOSTER, Blocks.COMPOSTER);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 2));

            helper.useBlock(COMPOSTER, player);

            final int level = helper.getBlockState(COMPOSTER).getValue(ComposterBlock.LEVEL);
            final int left = player.getItemInHand(InteractionHand.MAIN_HAND).getCount();
            if (level != 1) {
                failures.add(BuiltInRegistries.ITEM.getKey(item) + " carries a compostable trait but"
                        + " clicking it into an empty composter left it at level " + level);
            } else if (left != 1) {
                failures.add(BuiltInRegistries.ITEM.getKey(item) + " composted but " + left
                        + " are left of the 2 held, expected 1 to be spent");
            }
        }

        failIfAny(helper, "Manual composting regression", failures);
        helper.succeed();
    }

    /**
     * The hopper route for every item we declare compostable, asked of the composter's own input container
     * directly. A hopper per item would take thousands of ticks (one insert per 8), and the container is
     * exactly what the hopper consults, so this covers the whole family for the cost of one composter.
     */
    @GameTest
    public void everyCompostableIsAcceptedThroughTheComposterFace(GameTestHelper helper) {
        helper.setBlock(COMPOSTER, Blocks.COMPOSTER);

        final BlockPos abs = helper.absolutePos(COMPOSTER);
        final BlockState state = helper.getLevel().getBlockState(abs);
        final WorldlyContainer container = ((WorldlyContainerHolder) state.getBlock())
                .getContainer(state, helper.getLevel(), abs);

        final List<String> failures = new ArrayList<>();
        for (Item item : compostables()) {
            if (!container.canPlaceItemThroughFace(0, new ItemStack(item), Direction.UP)) {
                failures.add(BuiltInRegistries.ITEM.getKey(item) + " carries a compostable trait but the"
                        + " composter rejects it through the top face - a hopper can never insert it");
            }
        }

        failIfAny(helper, "Automated composting regression", failures);
        helper.succeed();
    }

    private static Block sapling() {
        return NetherWoodBlocks.MAT_GLOOMWOOD.getSapling();
    }

    /**
     * Every registered item carrying a compostable trait. Throws rather than returning an empty list: a
     * sweep over nothing passes silently, which would make the two sweeps above prove nothing at all.
     */
    private static List<Item> compostables() {
        final List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (ComposterAPI.compostChance(item) >= 0.0f) items.add(item);
        }
        if (items.isEmpty()) {
            throw new AssertionError("no registered item carries a compostable trait - nothing to sweep");
        }
        return items;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
