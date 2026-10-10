package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * #84: the whispering gourd vine. Its {@code shape} is not purely structural - below the tip, {@code MIDDLE}
 * is a segment carrying gourds and {@code TOP} a bare one (worldgen mixes the two along the column).
 * <ul>
 *   <li>Shearing a gourd segment harvests it: 1-2 gourds, one point of shear durability, and the segment
 *       turns bare - it used to turn into the tip ({@code BOTTOM}) and cost the shears nothing.</li>
 *   <li>A bare segment must stay bare across neighbour updates; bclib's structural rule turned it back into
 *       a gourd one on the first update.</li>
 *   <li>Breaking a gourd segment yields gourds with any tool, and shears/silk touch reliably return the
 *       vine from a bare segment or the tip - instead of gourds from the tip and a 5% vine otherwise.</li>
 * </ul>
 */
public class WhisperingGourdGameTest {
    /** Leaves the arena's barrier ceiling (y=7) alone - see gametest-arena-is-8-blocks-tall. */
    private static final BlockPos CEILING = new BlockPos(3, 6, 3);
    private static final BlockPos LOOT_POS = new BlockPos(1, 2, 1);
    private static final int PLACE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SKIP_ON_PLACE;
    private static final int TRIALS = 8;

    @GameTest(maxTicks = 40)
    public void shearingHarvestsTheGourdsAndWearsTheShears(GameTestHelper helper) {
        final BlockPos fruit = CEILING.below(2);
        column(helper, TripleShape.TOP, TripleShape.MIDDLE, TripleShape.BOTTOM);

        final ServerPlayer player = MockPlayers.survival(helper, fruit.east(2));
        final ItemStack shears = new ItemStack(Items.SHEARS);
        player.setItemInHand(InteractionHand.MAIN_HAND, shears);
        final BlockPos abs = helper.absolutePos(fruit);
        player.gameMode.useItemOn(
                player, helper.getLevel(), shears, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.EAST, abs, false)
        );

        final List<String> failures = new ArrayList<>();
        final BlockState after = helper.getBlockState(fruit);
        if (!after.is(NetherVineBlocks.WHISPERING_GOURD_VINE) || after.getValue(BaseVineBlock.SHAPE) != TripleShape.TOP) {
            failures.add("sheared segment is " + after + ", expected a bare (shape=top) vine");
        }
        final BlockState tip = helper.getBlockState(CEILING.below(3));
        if (!tip.is(NetherVineBlocks.WHISPERING_GOURD_VINE) || tip.getValue(BaseVineBlock.SHAPE) != TripleShape.BOTTOM) {
            failures.add("tip below the sheared segment is " + tip + ", expected it untouched (shape=bottom)");
        }
        if (shears.getDamageValue() != 1) {
            failures.add("shears took " + shears.getDamageValue() + " damage, expected 1");
        }
        final int gourds = collect(helper.getLevel(), new AABB(abs).inflate(3.0), NetherCropBlocks.WHISPERING_GOURD.asItem());
        if (gourds < 1 || gourds > 2) failures.add("shearing dropped " + gourds + " gourds, expected 1-2");
        fail(helper, "shearing", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 40)
    public void bareSegmentsStayBareAcrossNeighbourUpdates(GameTestHelper helper) {
        // Placed top-down with full updates, so every segment is shape-updated by the one placed below it.
        column(helper, TripleShape.TOP, TripleShape.TOP, TripleShape.MIDDLE, TripleShape.BOTTOM);

        final List<String> failures = new ArrayList<>();
        final TripleShape[] expected = {TripleShape.TOP, TripleShape.TOP, TripleShape.MIDDLE, TripleShape.BOTTOM};
        for (int i = 0; i < expected.length; i++) {
            final BlockState state = helper.getBlockState(CEILING.below(i + 1));
            if (!state.is(NetherVineBlocks.WHISPERING_GOURD_VINE) || state.getValue(BaseVineBlock.SHAPE) != expected[i]) {
                failures.add("segment " + (i + 1) + " is " + state + ", expected shape=" + expected[i].getSerializedName());
            }
        }
        fail(helper, "neighbour updates", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 200)
    public void breakingTheVineDropsGourdsAndSeeds(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, LOOT_POS.east(2));
        final Item gourd = NetherCropBlocks.WHISPERING_GOURD.asItem();
        final Item seeds = NetherVineBlocks.WHISPERING_GOURD_VINE.asItem();
        final List<String> failures = new ArrayList<>();

        int handGourds = 0;
        int shearedBare = 0;
        int shearedTip = 0;
        int tipGourds = 0;
        for (int i = 0; i < TRIALS; i++) {
            handGourds += breakOnce(helper, player, TripleShape.MIDDLE, ItemStack.EMPTY, gourd);
            shearedBare += breakOnce(helper, player, TripleShape.TOP, new ItemStack(Items.SHEARS), seeds);
            shearedTip += breakOnce(helper, player, TripleShape.BOTTOM, new ItemStack(Items.SHEARS), seeds);
            tipGourds += breakOnce(helper, player, TripleShape.BOTTOM, new ItemStack(Items.SHEARS), gourd);
        }
        if (handGourds < TRIALS) failures.add("a gourd segment broken by hand dropped " + handGourds + " gourds over " + TRIALS + " breaks, expected at least 1 each");
        if (shearedBare != TRIALS) failures.add("a bare segment broken with shears dropped " + shearedBare + " seeds over " + TRIALS + " breaks, expected 1 each");
        if (shearedTip != TRIALS) failures.add("the tip broken with shears dropped " + shearedTip + " seeds over " + TRIALS + " breaks, expected 1 each");
        if (tipGourds != 0) failures.add("the bare tip broken with shears dropped " + tipGourds + " gourds, expected none");
        fail(helper, "breaking", failures);
        helper.succeed();
    }

    /** Hangs a vine column with the given shapes, top-down, from a netherrack ceiling - with full updates. */
    private static void column(GameTestHelper helper, TripleShape... shapes) {
        helper.setBlock(CEILING, Blocks.NETHERRACK);
        for (int i = 0; i < shapes.length; i++) {
            helper.setBlock(
                    CEILING.below(i + 1),
                    NetherVineBlocks.WHISPERING_GOURD_VINE.defaultBlockState().setValue(BaseVineBlock.SHAPE, shapes[i])
            );
        }
    }

    /** Places one segment without shape updates, mines it as a survival player and counts {@code item} dropped. */
    private static int breakOnce(GameTestHelper helper, ServerPlayer player, TripleShape shape, ItemStack tool, Item item) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(LOOT_POS);
        final AABB area = new AABB(abs).inflate(3.0);
        level.getEntitiesOfClass(ItemEntity.class, area).forEach(ItemEntity::discard);

        level.setBlock(abs, NetherVineBlocks.WHISPERING_GOURD_VINE.defaultBlockState().setValue(BaseVineBlock.SHAPE, shape), PLACE_FLAGS);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.gameMode.destroyBlock(abs);
        final int count = collect(level, area, item);
        level.setBlock(abs, Blocks.AIR.defaultBlockState(), PLACE_FLAGS);
        return count;
    }

    /** Counts {@code item} among the item entities in {@code area}, and discards every item entity there. */
    private static int collect(ServerLevel level, AABB area, Item item) {
        int count = 0;
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, area)) {
            if (e.getItem().is(item)) count += e.getItem().getCount();
            e.discard();
        }
        return count;
    }

    private static void fail(GameTestHelper helper, String what, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Whispering gourd regression (" + what + "):\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
