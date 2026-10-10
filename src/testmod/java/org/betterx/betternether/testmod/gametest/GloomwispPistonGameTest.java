package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockGloomwispVine;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * What a piston does to a gloomwisp.
 * <p>
 * The plant is {@code PushReaction.DESTROY} - it comes from {@code NetherMaterial.plant()}, and nothing
 * in the registration overrides it - so a piston never carries a wisp anywhere: it breaks it. That is a
 * property value rather than mod code, which is exactly why it is worth a test. Nothing would fail, or
 * even look odd, if a later change to the material bundle quietly made wisps pushable; they would simply
 * start riding flying machines around the Nether.
 * <p>
 * The other half is the drop. A wisp's loot table only yields an item for the {@code TOP} segment, so
 * however tall the stalk and wherever the piston hits it, the whole plant comes down and exactly one
 * item comes out of it.
 */
public class GloomwispPistonGameTest {
    private static final int FLOOR_Y = 1;

    /** The stalk: two stems and a head, tall enough that the piston can hit it well below the top. */
    private static final BlockPos BASE = new BlockPos(3, 2, 3);
    private static final BlockPos MIDDLE = BASE.above();
    private static final BlockPos TOP = BASE.above(2);

    /** Aimed at {@link #BASE}, with its power source behind it. */
    private static final BlockPos PISTON = new BlockPos(2, 2, 3);
    private static final BlockPos POWER = new BlockPos(1, 2, 3);

    /**
     * The floor block the stalk stands on, the sticky piston that drags it out from under, and the gap
     * between them the piston head extends into - see
     * {@link #aPistonTakingTheGroundAwayBreaksTheWholePlant} for why the gap has to be there.
     */
    private static final BlockPos SUPPORT = BASE.below();
    private static final BlockPos FLOOR_PISTON = new BlockPos(1, FLOOR_Y, 3);
    private static final BlockPos FLOOR_GAP = new BlockPos(2, FLOOR_Y, 3);
    private static final BlockPos FLOOR_POWER = new BlockPos(0, FLOOR_Y, 3);

    /** Long enough for the piston to schedule, fire and finish its two-tick extension several times over. */
    private static final int SETTLE = 20;

    /**
     * A piston driven into the foot of a stalk takes the whole plant with it.
     */
    @GameTest(maxTicks = 100)
    public void aPistonPushingIntoAWispBreaksTheWholePlant(GameTestHelper helper) {
        floor(helper);
        plant(helper);

        helper.setBlock(PISTON, Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.EAST));
        helper.setBlock(POWER, Blocks.REDSTONE_BLOCK);

        helper.startSequence()
              .thenIdle(SETTLE)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  standingSegments(helper, failures);
                  droppedExactlyOneWisp(helper, failures);
                  failIfAny(helper, "Gloomwisp piston-push regression", failures);
              })
              .thenSucceed();
    }

    /**
     * And a sticky piston that drags the ground out from under one does the same, by way of
     * {@code canSurvive} rather than by touching the plant at all.
     * <p>
     * Pulling, not pushing, and that is the whole difficulty of the setup: a piston that <em>pushes</em>
     * the ground block along leaves either the next floor block or its own head in the vacated space, and
     * the plant stands on that quite happily. Only a retracting sticky piston actually leaves a hole. So
     * the gap in front of the piston is deliberate - it lets the head extend without touching
     * {@link #SUPPORT} - and the pull is the second half, when the power goes away.
     */
    @GameTest(maxTicks = 100)
    public void aPistonTakingTheGroundAwayBreaksTheWholePlant(GameTestHelper helper) {
        floor(helper);
        plant(helper);

        helper.setBlock(FLOOR_PISTON, Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.EAST));
        helper.setBlock(FLOOR_GAP, Blocks.AIR);
        helper.setBlock(FLOOR_POWER, Blocks.REDSTONE_BLOCK);

        helper.startSequence()
              .thenIdle(SETTLE)
              // Extended, head in the gap, the ground under the plant untouched and the plant still up.
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  if (!helper.getBlockState(SUPPORT).is(Blocks.NETHERRACK)) {
                      failures.add("extending the piston already disturbed the ground under the plant,"
                              + " so the pull below is not what breaks it");
                  }
                  failIfAny(helper, "Gloomwisp piston-undermine setup", failures);
              })
              .thenExecute(() -> helper.setBlock(FLOOR_POWER, Blocks.AIR))
              .thenIdle(SETTLE)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  if (helper.getBlockState(SUPPORT).is(Blocks.NETHERRACK)) {
                      failures.add("the retracting piston never pulled the ground block away, so the plant"
                              + " was never unsupported and this test asserts nothing");
                  }
                  standingSegments(helper, failures);
                  droppedExactlyOneWisp(helper, failures);
                  failIfAny(helper, "Gloomwisp piston-undermine regression", failures);
              })
              .thenSucceed();
    }

    /**
     * No segment of the plant may still be standing - including the ones the piston never reached, which
     * are held up by nothing once the foot is gone.
     */
    private static void standingSegments(GameTestHelper helper, List<String> failures) {
        for (BlockPos segment : List.of(BASE, MIDDLE, TOP)) {
            if (helper.getBlockState(segment).is(NetherVineBlocks.GLOOMWISP_VINE)) {
                failures.add(segment + " is still a wisp - a piston moved the plant instead of breaking it,"
                        + " or left part of the stalk hanging in the air");
            }
        }
    }

    private static void droppedExactlyOneWisp(GameTestHelper helper, List<String> failures) {
        final List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class,
                new AABB(helper.absolutePos(BASE)).inflate(8.0)
        );
        final int wisps = drops.stream()
                               .filter(e -> e.getItem().is(NetherVineBlocks.GLOOMWISP_VINE.asItem()))
                               .mapToInt(e -> e.getItem().getCount())
                               .sum();
        if (wisps != 1) {
            failures.add("a three-segment plant dropped " + wisps + " wisps, expected exactly 1 - only the"
                    + " head is in the loot table, and all three segments came down");
        }
        final List<String> others = drops.stream()
                                         .filter(e -> !e.getItem().is(NetherVineBlocks.GLOOMWISP_VINE.asItem()))
                                         .map(e -> e.getItem().toString())
                                         .toList();
        if (!others.isEmpty()) {
            failures.add("unexpected extra drops: " + others);
        }
    }

    /** A floor with room ahead of the pistons for whatever they push. */
    private static void floor(GameTestHelper helper) {
        for (int x = 0; x <= 6; x++) {
            for (int z = 2; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, FLOOR_Y, z), Blocks.NETHERRACK);
            }
        }
    }

    /** Two stems and a head, so the piston hits the plant two blocks below the only segment that drops. */
    private static void plant(GameTestHelper helper) {
        final BlockState stalk = NetherVineBlocks.GLOOMWISP_VINE
                .defaultBlockState()
                .setValue(BlockGloomwispVine.OFFSET, false);
        helper.setBlock(BASE, stalk.setValue(BlockGloomwispVine.SHAPE, BlockProperties.TripleShape.BOTTOM));
        helper.setBlock(MIDDLE, stalk.setValue(BlockGloomwispVine.SHAPE, BlockProperties.TripleShape.MIDDLE));
        helper.setBlock(TOP, stalk.setValue(BlockGloomwispVine.SHAPE, BlockProperties.TripleShape.TOP));
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
