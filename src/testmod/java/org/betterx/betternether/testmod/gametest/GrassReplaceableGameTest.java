package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherPlantBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether's grass tufts must behave like vanilla {@code short_grass}: a placed block overwrites them,
 * flowing water and lava wash them away, and trees/mushrooms may grow through them
 * (quiqueck/BetterNether#166). Vanilla short grass runs through every check as the control.
 */
public class GrassReplaceableGameTest {
    private static final List<Block> SOILS = List.of(
            Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.SOUL_SOIL, Blocks.SOUL_SAND, Blocks.GRASS_BLOCK
    );

    private static List<Block> grasses() {
        return List.of(
                Blocks.SHORT_GRASS,
                NetherPlantBlocks.NETHER_GRASS,
                NetherPlantBlocks.SWAMP_GRASS,
                NetherPlantBlocks.SOUL_GRASS,
                NetherPlantBlocks.JUNGLE_PLANT,
                NetherPlantBlocks.BONE_GRASS,
                NetherPlantBlocks.PALE_GLOOMGRASS,
                NetherPlantBlocks.GLOOMGRASS,
                NetherPlantBlocks.SEPIA_BONE_GRASS
        );
    }

    private static String id(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    /** Spreads the plants over the 8x8 arena, two blocks apart. */
    private static BlockPos plantPos(int i) {
        return new BlockPos((i % 3) * 2 + 1, 2, (i / 3) * 2 + 1);
    }

    /** Plants {@code block} on the first soil it survives on; returns false if there is none. */
    private static boolean plant(GameTestHelper helper, BlockPos rel, Block block) {
        for (Block soil : SOILS) {
            put(helper, rel.below(), soil.defaultBlockState());
            put(helper, rel, block.defaultBlockState());
            if (block.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(rel))) return true;
        }
        return false;
    }

    private static void put(GameTestHelper helper, BlockPos rel, BlockState state) {
        helper.getLevel().setBlock(
                helper.absolutePos(rel),
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS
        );
    }

    @GameTest
    public void grassHasVanillaReplaceability(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (Block block : grasses()) {
            final BlockState state = block.defaultBlockState();
            if (!state.canBeReplaced()) failures.add(id(block) + " is not replaceable()");
            // 26.3 renamed PushReaction.DESTROY to POPPED.
            if (state.getPistonPushReaction() != PushReaction.POPPED) {
                failures.add(id(block) + " push reaction is " + state.getPistonPushReaction());
            }
            // 26.3 dropped BlockState#blocksMotion(): a fluid flows into a block only if it is in
            // #minecraft:washed_away_by_fluids (checked with the other tags below), and BLOCKS_MOTION is now a tag.
            if (state.is(BlockTags.BLOCKS_MOTION)) failures.add(id(block) + " is in #minecraft:blocks_motion");
            for (TagKey<Block> tag : List.of(
                    BlockTags.REPLACEABLE,
                    BlockTags.REPLACEABLE_BY_TREES,
                    BlockTags.REPLACEABLE_BY_MUSHROOMS,
                    BlockTags.WASHED_AWAY_BY_FLUIDS
            )) {
                if (!state.is(tag)) failures.add(id(block) + " is not in #" + tag.location());
            }
        }
        failIfAny(helper, "Grass replaceability regression", failures);
        helper.succeed();
    }

    @GameTest
    public void placingABlockReplacesGrass(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerPlayer player = MockPlayers.survival(helper, new BlockPos(0, 2, 0));
        final List<Block> grasses = grasses();
        for (int i = 0; i < grasses.size(); i++) {
            final Block block = grasses.get(i);
            final BlockPos rel = plantPos(i);
            if (!plant(helper, rel, block)) {
                failures.add(id(block) + " survives on none of the test soils");
                continue;
            }
            final BlockPos abs = helper.absolutePos(rel);
            final ItemStack stack = new ItemStack(Items.COBBLESTONE);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            final BlockPlaceContext context = new BlockPlaceContext(
                    helper.getLevel(), player, InteractionHand.MAIN_HAND, stack,
                    new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)
            );
            ((BlockItem) Items.COBBLESTONE).place(context);
            if (!helper.getLevel().getBlockState(abs).is(Blocks.COBBLESTONE)) {
                failures.add("placing cobblestone into " + id(block) + " did not replace it (now "
                        + helper.getLevel().getBlockState(abs) + ", above: " + helper.getLevel().getBlockState(abs.above()) + ")");
            }
        }
        failIfAny(helper, "Grass placement regression", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 200)
    public void waterWashesGrassAway(GameTestHelper helper) {
        washTest(helper, Blocks.WATER);
    }

    @GameTest(maxTicks = 200)
    public void lavaWashesGrassAway(GameTestHelper helper) {
        washTest(helper, Blocks.LAVA);
    }

    /** A fluid source directly above each plant flows down into it, exactly as a flowing stream would. */
    private static void washTest(GameTestHelper helper, Block fluid) {
        final List<Block> grasses = grasses();
        final List<String> setup = new ArrayList<>();
        for (int i = 0; i < grasses.size(); i++) {
            if (!plant(helper, plantPos(i), grasses.get(i))) setup.add(id(grasses.get(i)) + " survives on none of the test soils");
        }
        failIfAny(helper, "Grass wash setup", setup);
        for (int i = 0; i < grasses.size(); i++) {
            helper.setBlock(plantPos(i).above(), fluid.defaultBlockState());
        }
        helper.succeedWhen(() -> {
            final List<String> failures = new ArrayList<>();
            for (int i = 0; i < grasses.size(); i++) {
                final BlockState now = helper.getBlockState(plantPos(i));
                if (now.is(grasses.get(i))) failures.add(id(fluid) + " did not wash away " + id(grasses.get(i)));
            }
            failIfAny(helper, "Grass fluid regression", failures);
        });
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
