package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * #252: a chair is two blocks tall (a {@code top=false} seat and a {@code top=true} back), and breaking it
 * used to drop two chairs. However it is removed - a survival player mining either half, an explosion, or a
 * piston shoving either half - it must yield exactly one chair and leave neither half behind.
 */
public class ChairDropGameTest {
    private static final BlockPos POS = new BlockPos(3, 2, 3);

    @GameTest(maxTicks = 40)
    public void minedChairDropsExactlyOneChair(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final List<String> failures = new ArrayList<>();
        for (BaseChair chair : allChairs()) {
            for (boolean top : new boolean[]{false, true}) {
                place(helper, chair);
                // The cincinnasite chair is metal and needs a pickaxe; the wooden ones an axe.
                final boolean pickaxe = chair.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE);
                player.setItemInHand(
                        InteractionHand.MAIN_HAND,
                        new ItemStack(pickaxe ? Items.NETHERITE_PICKAXE : Items.NETHERITE_AXE)
                );
                player.gameMode.destroyBlock(helper.absolutePos(top ? POS.above() : POS));
                check(helper, chair, "mining the " + (top ? "top" : "bottom") + " half of", failures);
            }
        }
        player.discard();
        fail(helper, failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 40)
    public void explodedChairDropsExactlyOneChair(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (BaseChair chair : allChairs()) {
            place(helper, chair);
            final var center = net.minecraft.world.phys.Vec3.atCenterOf(helper.absolutePos(POS));
            helper.getLevel().explode(null, center.x, center.y + 0.5, center.z, 3.0f, Level.ExplosionInteraction.BLOCK);
            check(helper, chair, "blowing up", failures);
            restoreFloor(helper);
        }
        fail(helper, failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 60)
    public void pistonPushingTheBottomHalfDropsExactlyOneChair(GameTestHelper helper) {
        pistonCase(helper, false);
    }

    @GameTest(maxTicks = 60)
    public void pistonPushingTheTopHalfDropsExactlyOneChair(GameTestHelper helper) {
        pistonCase(helper, true);
    }

    /** Pushes one half of a wooden and the cincinnasite chair with a piston, then checks the drops. */
    private static void pistonCase(GameTestHelper helper, boolean top) {
        final List<BaseChair> chairs = List.of(
                (BaseChair) NetherWoodBlocks.MAT_RUBEUS.getBlock(SlotType.CHAIR),
                (BaseChair) NetherFurnitureBlocks.CHAIR_CINCINNASITE
        );
        final List<BlockPos> seats = List.of(POS.west(2), POS.east(1));
        for (int i = 0; i < chairs.size(); i++) {
            final BlockPos seat = seats.get(i);
            place(helper, chairs.get(i), seat);
            final BlockPos piston = (top ? seat.above() : seat).north();
            helper.setBlock(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.SOUTH));
            helper.setBlock(piston.north(), Blocks.REDSTONE_BLOCK);
        }
        // The broken half's drop pops out a few ticks in and may roll; count it anywhere in the arena.
        helper.runAfterDelay(20, () -> {
            final List<String> failures = new ArrayList<>();
            for (int i = 0; i < chairs.size(); i++) {
                check(
                        helper, chairs.get(i), seats.get(i), helper.getBoundsWithPadding(),
                        "a piston pushing the " + (top ? "top" : "bottom") + " half of", failures
                );
            }
            fail(helper, failures);
            helper.succeed();
        });
    }

    private static List<BaseChair> allChairs() {
        final List<BaseChair> chairs = new ArrayList<>();
        BuiltInRegistries.BLOCK.entrySet().forEach(e -> {
            if (e.getKey().identifier().getNamespace().equals("betternether")
                    && e.getValue() instanceof BaseChair chair) {
                chairs.add(chair);
            }
        });
        return chairs;
    }

    private static void place(GameTestHelper helper, Block chair) {
        place(helper, chair, POS);
    }

    /** Places both halves the way {@code BaseChair#setPlacedBy} does, after clearing old item entities. */
    private static void place(GameTestHelper helper, Block chair, BlockPos seat) {
        collect(helper.getLevel(), area(helper, seat), chair);
        helper.setBlock(seat, chair.defaultBlockState());
        helper.setBlock(seat.above(), chair.defaultBlockState().setValue(BaseChair.TOP, true));
    }

    private static void check(GameTestHelper helper, BaseChair chair, String what, List<String> failures) {
        check(helper, chair, POS, area(helper, POS), what, failures);
    }

    private static void check(
            GameTestHelper helper,
            BaseChair chair,
            BlockPos seat,
            AABB dropArea,
            String what,
            List<String> failures
    ) {
        final ServerLevel level = helper.getLevel();
        final AABB area = area(helper, seat);
        final int drops = collect(level, dropArea, chair);
        final String label = what + " " + BuiltInRegistries.BLOCK.getKey(chair);
        if (drops != 1) failures.add(label + " dropped " + drops + " chairs, expected 1");
        BlockPos.betweenClosedStream(area).forEach(p -> {
            if (level.getBlockState(p).is(chair)) {
                failures.add(label + " left a chair block behind at " + helper.relativePos(p));
                level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        });
    }

    private static AABB area(GameTestHelper helper, BlockPos seat) {
        return new AABB(helper.absolutePos(seat)).inflate(2.0);
    }

    /** Counts and discards the {@code chair} item entities in {@code area}, leaving any other item alone. */
    private static int collect(ServerLevel level, AABB area, Block chair) {
        int count = 0;
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, area, e -> e.getItem().is(chair.asItem()))) {
            count += e.getItem().getCount();
            e.discard();
        }
        return count;
    }

    /** The explosion can crater the arena floor; refill it so the next chair stands on something. */
    private static void restoreFloor(GameTestHelper helper) {
        for (int dx = -3; dx <= 3; dx++)
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = -1; dy <= 3; dy++) {
                    final BlockPos p = POS.offset(dx, dy, dz);
                    if (dy < 0) helper.setBlock(p, Blocks.STONE);
                    else helper.setBlock(p, Blocks.AIR);
                }
            }
    }

    private static void fail(GameTestHelper helper, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Chair drop regression (#252):\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
