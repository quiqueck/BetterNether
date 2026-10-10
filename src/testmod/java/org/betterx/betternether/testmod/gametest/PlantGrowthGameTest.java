package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.blocks.AbstractVineBlock;
import org.betterx.betternether.blocks.BNBlockProperties;
import org.betterx.betternether.blocks.BNLeaves;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Every BetterNether plant that is coded to grow or spread on its own actually does so from random ticks
 * alone - no player, no bone meal.
 * <p>
 * This is the shape of bug behind several long-standing reports (nether reed not growing next to lava,
 * BetterNether crops only growing with bone meal, hook mushrooms never spreading): the growth code sits in
 * {@code randomTick()}, but a block is only ever offered a random tick if its {@code Properties} asked for
 * one ({@code randomTicks()}), and nothing about a block that forgot to ask is visible - it places, renders,
 * survives and often still accepts bone meal. {@link SaplingGrowthGameTest} covers the feature saplings;
 * this covers everything else.
 * <p>
 * The plant set is not a hand-kept list. It is every {@code betternether} block whose class (or a mod
 * superclass) overrides {@code randomTick}, minus {@link #NOT_GROWTH}, so a new plant joins the sweep the
 * moment it is written. {@link #everyGrowingPlantAsksForRandomTicks} is the cheap property sweep;
 * {@link #everyGrowingPlantGrowsFromRandomTicksAlone} plants each one and drives it end to end, so that
 * "asks for random ticks" cannot pass while the growing itself is broken.
 */
public class PlantGrowthGameTest {
    /** 12x24x12 - trees grown from seeds need the headroom (see gametest-arena notes in SaplingGrowthGameTest). */
    private static final String TALL_ARENA = "betternether-testmod:tall_empty";

    private static final int SIZE_XZ = 12;
    private static final int SIZE_Y = 24;

    /** Hook mushrooms and molds spread sideways, so their ceiling/floor must extend under the neighbours. */
    private static final int SOIL_RADIUS = 1;

    /** Driving random ticks costs no server ticks; this is generous for the slowest (1-in-32, two stages). */
    private static final int MAX_RANDOM_TICKS = 3000;
    /** Supports a plant that survives but does not grow is tried on before it is reported. */
    private static final int MAX_ATTEMPTS = 4;

    /**
     * Blocks that override {@code randomTick} for something other than growing or spreading, so there is no
     * growth to assert. Keyed by path in the {@code betternether} namespace. Leaves and static vines are
     * excluded by type in {@link #isGrowingPlant}.
     */
    private static final Set<String> NOT_GROWTH = Set.of(
            // fills a cauldron below it
            "eyeball", "eyeball_small"
    );

    /** Supports a plant is tried on, in order. Ordinary soils come first so the slow path is what is measured. */
    private static List<Block> soils() {
        return List.of(
                Blocks.NETHERRACK,
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL,
                Blocks.CRIMSON_NYLIUM,
                NetherTerrainBlocks.NETHER_MYCELIUM,
                NetherTerrainBlocks.MUSHROOM_GRASS,
                Blocks.BONE_BLOCK,
                Blocks.MAGMA_BLOCK,
                Blocks.SCULK,
                Blocks.OAK_LOG,
                Blocks.DIRT,
                NetherTerrainBlocks.FARMLAND,
                Blocks.GRAVEL,
                Blocks.RED_SAND
        );
    }

    private enum Outcome {GREW, NOT_RANDOMLY_TICKING, NEVER_GREW, DIED}

    /**
     * Where a plant is tried: standing on a floor, hanging from a ceiling, or stuck to a wall (lucis spores).
     * {@code facing} is what a {@code FACING}-style property is set to - the direction away from the support.
     */
    private enum Placement {
        FLOOR("on ", new BlockPos(6, 1, 6), Direction.UP),
        CEILING("hanging from ", new BlockPos(6, 17, 6), Direction.DOWN),
        WALL("on the side of ", new BlockPos(6, 8, 5), Direction.SOUTH);

        final String label;
        final BlockPos soil;
        final Direction facing;
        final BlockPos plant;

        Placement(String label, BlockPos soil, Direction facing) {
            this.label = label;
            this.soil = soil;
            this.facing = facing;
            this.plant = soil.relative(facing);
        }
    }

    /**
     * Every growing plant has at least one randomly ticking state. Without one, {@code ServerLevel#tickChunk}
     * never calls its {@code randomTick()} and it can only ever grow from bone meal.
     */
    @GameTest
    public void everyGrowingPlantAsksForRandomTicks(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (Block block : growingPlants()) {
            if (block.getStateDefinition().getPossibleStates().stream().noneMatch(BlockState::isRandomlyTicking)) {
                failures.add(BuiltInRegistries.BLOCK.getKey(block) + " overrides randomTick but none of its"
                        + " states is randomly ticking - it can never grow on its own");
            }
        }
        if (!Blocks.NETHER_WART.defaultBlockState().isRandomlyTicking()) {
            failures.add("vanilla nether wart reads as not randomly ticking - this check is broken, not the mod");
        }
        failIfAny(helper, "Plant random-tick regression", failures);
        helper.succeed();
    }

    /**
     * Each growing plant, planted on the first support it survives on, grows (changes state or adds blocks)
     * from random ticks alone, with vanilla nether wart as a control through the identical loop.
     * <p>
     * Ticks are applied through the gate {@code ServerLevel#tickChunk} uses ({@code isRandomlyTicking}), so a
     * plant missing {@code randomTicks()} fails here exactly as it fails in a world. The first growth step is
     * enough: everything past it is the same code path again.
     */
    @GameTest(structure = TALL_ARENA, maxTicks = 400)
    public void everyGrowingPlantGrowsFromRandomTicksAlone(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final List<Block> plants = new ArrayList<>(growingPlants());
        plants.add(Blocks.NETHER_WART);

        for (Block plant : plants) {
            final String name = BuiltInRegistries.BLOCK.getKey(plant).toString();
            Outcome best = null;
            String where = null;
            boolean placed = false;
            int attempts = 0;

            search:
            for (Placement placement : Placement.values()) {
                for (Block soil : soils()) {
                    for (boolean lava : new boolean[]{false, true}) {
                        if (!setup(helper, plant, soil, placement, lava)) continue;
                        placed = true;
                        final Outcome outcome = drive(helper, placement.plant);
                        where = placement.label + BuiltInRegistries.BLOCK.getKey(soil)
                                + (lava ? " next to lava" : "");
                        if (outcome == Outcome.GREW) {
                            best = outcome;
                            break search;
                        }
                        if (best == null || outcome == Outcome.NOT_RANDOMLY_TICKING) best = outcome;
                        // A plant that refuses the gate refuses it on every support; one that just never
                        // grew gets a few more supports before it is reported.
                        if (outcome == Outcome.NOT_RANDOMLY_TICKING || ++attempts >= MAX_ATTEMPTS) break search;
                    }
                }
            }
            clear(helper);

            if (!placed) {
                failures.add(name + " could not be planted on any of the test supports - extend soils()");
            } else if (best == Outcome.NOT_RANDOMLY_TICKING) {
                failures.add(name + " (" + where + ") is not randomly ticking, so the server never offers it"
                        + " a random tick - it can only grow from bone meal");
            } else if (best == Outcome.NEVER_GREW) {
                failures.add(name + " (" + where + ") took " + MAX_RANDOM_TICKS + " random ticks without growing");
            } else if (best == Outcome.DIED) {
                failures.add(name + " (" + where + ") broke under random ticks instead of growing");
            }
        }

        failIfAny(helper, "Natural plant growth regression", failures);
        helper.succeed();
    }

    /**
     * Clears the arena and plants {@code plant} against {@code soil}. A patch of soil is laid so that
     * spreading plants have somewhere to go; {@code lava} replaces one soil block beside the support with
     * lava (nether reed needs it, like sugar cane needs water).
     *
     * @return whether the plant can survive where it was put
     */
    private static boolean setup(GameTestHelper helper, Block plant, Block soil, Placement placement, boolean lava) {
        clear(helper);
        final Direction.Axis normal = placement.facing.getAxis();
        for (int a = -SOIL_RADIUS; a <= SOIL_RADIUS; a++) {
            for (int b = -SOIL_RADIUS; b <= SOIL_RADIUS; b++) {
                final BlockPos p = switch (normal) {
                    case Y -> placement.soil.offset(a, 0, b);
                    case Z -> placement.soil.offset(a, b, 0);
                    case X -> placement.soil.offset(0, a, b);
                };
                put(helper, p, soil.defaultBlockState());
            }
        }
        if (lava) {
            final BlockPos beside = normal == Direction.Axis.X ? placement.soil.north() : placement.soil.east();
            put(helper, beside, Blocks.LAVA.defaultBlockState());
        }

        BlockState state = plant.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.FACING)) {
            state = state.setValue(BlockStateProperties.FACING, placement.facing);
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            if (placement.facing.getAxis() == Direction.Axis.Y) return false;
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, placement.facing);
        }
        // The egg plant's growth is regrowing after something walked through it.
        if (state.hasProperty(BNBlockProperties.DESTRUCTED)) state = state.setValue(BNBlockProperties.DESTRUCTED, true);
        put(helper, placement.plant, state);
        return state.canSurvive(helper.getLevel(), helper.absolutePos(placement.plant));
    }

    /**
     * Applies vanilla's random-tick gate to the plant until it grows. Growth is the plant's own position
     * changing to another non-air state (an age step, a seed turning into its plant or tree, a reed segment
     * no longer being the top) or a block appearing beside it (a mold or hook mushroom spreading, a bud).
     */
    private static Outcome drive(GameTestHelper helper, BlockPos plantRel) {
        final ServerLevel level = helper.getLevel();
        final RandomSource random = level.getRandom();
        final BlockPos plantAbs = helper.absolutePos(plantRel);
        final BlockState initial = level.getBlockState(plantAbs);
        // The gate itself, as ServerLevel#tickChunk applies it. Ticking unconditionally would grow a plant
        // that is missing randomTicks() just as happily, and the test could not fail on the actual bug.
        if (!initial.isRandomlyTicking()) return Outcome.NOT_RANDOMLY_TICKING;

        final List<BlockPos> around = new ArrayList<>();
        final List<BlockState> before = new ArrayList<>();
        for (BlockPos p : BlockPos.betweenClosed(plantAbs.offset(-1, -1, -1), plantAbs.offset(1, 1, 1))) {
            if (p.equals(plantAbs)) continue;
            around.add(p.immutable());
            before.add(level.getBlockState(p));
        }

        for (int tick = 0; tick < MAX_RANDOM_TICKS; tick++) {
            final BlockState state = level.getBlockState(plantAbs);
            if (state.isRandomlyTicking()) state.randomTick(level, plantAbs, random);

            final BlockState now = level.getBlockState(plantAbs);
            for (int i = 0; i < around.size(); i++) {
                final BlockState other = level.getBlockState(around.get(i));
                if (other != before.get(i) && !other.isAir()) return Outcome.GREW;
            }
            if (now.isAir()) return Outcome.DIED;
            if (now != initial) return Outcome.GREW;
        }
        return Outcome.NEVER_GREW;
    }

    private static void clear(GameTestHelper helper) {
        for (int x = 0; x < SIZE_XZ; x++) {
            for (int y = SIZE_Y - 1; y >= 0; y--) {
                for (int z = 0; z < SIZE_XZ; z++) {
                    put(helper, new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    /** Sets a block without neighbour updates or drops, so building and clearing never break the plant. */
    private static void put(GameTestHelper helper, BlockPos rel, BlockState state) {
        helper.getLevel().setBlock(
                helper.absolutePos(rel),
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS
        );
    }

    /**
     * Every {@code betternether} block whose class, or one of its mod superclasses, overrides
     * {@code randomTick} - i.e. every block that does something on its own over time - minus
     * {@link #NOT_GROWTH}. Throws rather than returning nothing: a sweep over nothing passes silently.
     */
    private static List<Block> growingPlants() {
        final List<Block> blocks = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            final var key = BuiltInRegistries.BLOCK.getKey(block);
            if (!"betternether".equals(key.getNamespace())) continue;
            if (NOT_GROWTH.contains(key.getPath())) continue;
            if (isGrowingPlant(block)) blocks.add(block);
        }
        if (blocks.isEmpty()) throw new AssertionError("no BetterNether block overrides randomTick - nothing to sweep");
        return blocks;
    }

    private static boolean isGrowingPlant(Block block) {
        // BetterNether leaves deliberately switch random ticks off (no decay), and their randomTick is empty.
        if (block instanceof BNLeaves) return false;
        // A bclib vine with growChance 0 is a static vine - it only grows from bone meal, by design.
        if (block instanceof AbstractVineBlock && vineGrowChance(block) <= 0) return false;
        return overridesRandomTick(block.getClass());
    }

    private static int vineGrowChance(Block block) {
        try {
            final var field = AbstractVineBlock.class.getDeclaredField("growChance");
            field.setAccessible(true);
            return field.getInt(block);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("AbstractVineBlock.growChance is gone - update this sweep", e);
        }
    }

    private static boolean overridesRandomTick(Class<?> type) {
        for (Class<?> c = type; c != null && c != Block.class; c = c.getSuperclass()) {
            if (!c.getName().startsWith("org.betterx.")) continue;
            try {
                c.getDeclaredMethod(
                        "randomTick",
                        BlockState.class,
                        ServerLevel.class,
                        BlockPos.class,
                        RandomSource.class
                );
                return true;
            } catch (NoSuchMethodException ignored) {
                // keep walking up
            }
        }
        return false;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
