package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.test.api.gametest.FluidWashSweep;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Every BetterNether {@link ButtonBlock} is in {@code #minecraft:buttons} (block and item), like vanilla's
 * buttons. The set-built wood buttons got that from {@code BUTTON_BLOCK}; the standalone bone and cincinnasite
 * buttons did not. On 26.3 {@code #minecraft:buttons} is part of {@code #minecraft:washed_away_by_fluids}, so
 * a button missing from it was no longer washed away by water and lava - the in-world tests check that, with
 * vanilla's {@code stone_button} as the control.
 */
public class ButtonTagGameTest {
    /** 26.3 dropped the {@code ItemTags.BUTTONS} handle; vanilla still ships the tag. */
    private static final TagKey<Item> ITEM_BUTTONS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("buttons"));

    private static List<Block> buttons() {
        return FluidWashSweep.plantsIn(BetterNether.C.namespace, block -> block instanceof ButtonBlock);
    }

    @GameTest
    public void everyButtonIsInButtonsTag(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final List<Block> buttons = buttons();
        if (buttons.size() < 13) failures.add("found only " + buttons.size() + " buttons");
        for (Block button : buttons) {
            if (!button.defaultBlockState().is(BlockTags.BUTTONS)) {
                failures.add(FluidWashSweep.id(button) + " is not in #minecraft:buttons");
            }
            if (!button.asItem().builtInRegistryHolder().is(ITEM_BUTTONS)) {
                failures.add(FluidWashSweep.id(button) + " (item) is not in #minecraft:buttons");
            }
            if (!button.defaultBlockState().is(BlockTags.WASHED_AWAY_BY_FLUIDS)) {
                failures.add(FluidWashSweep.id(button) + " is not in #minecraft:washed_away_by_fluids");
            }
        }
        GameTestAssertions.failIfAny(helper, "Buttons vs #minecraft:buttons", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 200)
    public void waterWashesButtonsAway(GameTestHelper helper) {
        wash(helper, Blocks.WATER);
    }

    @GameTest(maxTicks = 200)
    public void lavaWashesButtonsAway(GameTestHelper helper) {
        wash(helper, Blocks.LAVA);
    }

    /**
     * Each button lies on a stone floor (FACE=FLOOR, so it needs no wall) under a fluid source. As in
     * {@link FluidWashSweep#washEach}, everything is placed without neighbour updates and only the source's own
     * fluid tick is scheduled, so a button can only disappear by being washed away.
     */
    private static void wash(GameTestHelper helper, Block fluid) {
        final List<Block> buttons = new ArrayList<>();
        buttons.add(Blocks.STONE_BUTTON);
        buttons.addAll(buttons());
        if (buttons.size() > 36) {
            GameTestAssertions.failIfAny(helper, "Setup", List.of("more buttons than the 36 slots of one wash test"));
        }

        for (int x = 0; x <= 7; x++) {
            for (int z = 0; z <= 7; z++) {
                final boolean wall = x < 1 || x > 6 || z < 1 || z > 6;
                for (int y = 1; y <= 3; y++) {
                    put(helper, new BlockPos(x, y, z), wall || y == 1 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState());
                }
            }
        }

        final List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            final BlockPos rel = new BlockPos(1 + i % 6, 2, 1 + i / 6);
            put(helper, rel, buttons.get(i).defaultBlockState()
                                    .setValue(ButtonBlock.FACE, AttachFace.FLOOR)
                                    .setValue(ButtonBlock.FACING, Direction.NORTH));
            positions.add(rel);
        }

        final Fluid type = fluid.defaultBlockState().getFluidState().getType();
        for (BlockPos rel : positions) {
            final BlockPos source = helper.absolutePos(rel.above());
            helper.getLevel().setBlock(source, fluid.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            helper.getLevel().scheduleTick(source, type, type.getTickDelay(helper.getLevel()));
        }

        helper.succeedWhen(() -> {
            final List<String> failures = new ArrayList<>();
            for (int i = 0; i < buttons.size(); i++) {
                final BlockState now = helper.getBlockState(positions.get(i));
                if (!now.getFluidState().getType().isSame(type)) {
                    failures.add(FluidWashSweep.id(fluid) + " did not wash away " + FluidWashSweep.id(buttons.get(i)) + " (now " + now + ")");
                }
            }
            GameTestAssertions.failIfAny(helper, "Button wash regression", failures);
        });
    }

    private static void put(GameTestHelper helper, BlockPos rel, BlockState state) {
        helper.getLevel().setBlock(
                helper.absolutePos(rel),
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS
        );
    }
}
