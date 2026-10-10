package org.betterx.betternether.world.features;

import org.betterx.betternether.registry.block.NetherLeavesBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.MHelper;
import org.betterx.betternether.blocks.BlockPlantWall;
import org.betterx.betternether.blocks.BlockStalagnateSeed;
import org.betterx.betternether.blocks.RubeusLog;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.feature.api.features.GrowableFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RubeusTreeFeature extends NonOverlappingFeature<NaturalTreeConfiguration> implements GrowableFeature<NaturalTreeConfiguration> {
    public RubeusTreeFeature() {
        super(NaturalTreeConfiguration.CODEC);
    }

    @Override
    protected boolean isStructure(BlockState state) {
        return state.getBlock() == NetherWoodBlocks.MAT_RUBEUS.getLog();
    }

    @Override
    protected boolean isGround(BlockState state) {
        return BlocksHelper.isNetherGround(state);
    }

    @Override
    protected boolean place(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            NaturalTreeConfiguration config,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    ) {
        int length = BlocksHelper.upRay(world, pos, BlockStalagnateSeed.MAX_SEARCH_LENGTH + 2);
        if (length >= BlockStalagnateSeed.MAX_SEARCH_LENGTH)
            return super.place(world, pos, random, config, MAX_HEIGHT, context);
        return false;
    }

    private static final float[] CURVE_X = new float[]{9F, 7F, 1.5F, 0.5F, 3F, 7F};
    private static final float[] CURVE_Y = new float[]{20F, 17F, 12F, 4F, 0F, -2F};
    private static final int MIDDLE_Y = 10;

    /**
     * Gives every leaf of the crown its true vanilla decay distance and prunes the ones out of reach.
     * <p>
     * {@code LeavesBlock} relaxes {@code distance} through other leaves from the nearest log, and a
     * non-persistent leaf that ends up at {@link LeavesBlock#DECAY_DISTANCE} decays. So this is a
     * breadth-first sweep from the tree's wood through the leaves it placed - a path length, not the
     * straight-line distance used before, which under-reported leaves around the hollow of a crown and
     * would have let them vanish the first time a neighbour changed. Leaves that cannot be reached
     * within the budget are removed now, together with any cone hanging from them, rather than left to
     * decay in front of the player.
     */
    private void updateDistances(ServerLevelAccessor world, Set<BlockPos> leaves, StructureGeneratorThreadContext context) {
        final Map<BlockPos, Integer> distance = new HashMap<>();
        final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos log : context.POINTS) {
            if (world.getBlockState(log).is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) {
                distance.put(log, 0);
                queue.add(log);
            }
        }

        while (!queue.isEmpty()) {
            final BlockPos current = queue.poll();
            final int next = distance.get(current) + 1;
            if (next >= LeavesBlock.DECAY_DISTANCE) continue;
            for (Direction dir : Direction.values()) {
                final BlockPos neighbour = current.relative(dir);
                if (!leaves.contains(neighbour) || distance.containsKey(neighbour)) continue;
                if (!world.getBlockState(neighbour).is(NetherLeavesBlocks.RUBEUS_LEAVES)) continue;
                distance.put(neighbour, next);
                queue.add(neighbour);
            }
        }

        final BlockState cone = NetherWoodBlocks.MAT_RUBEUS.getCone().defaultBlockState();
        for (BlockPos leaf : leaves) {
            final BlockState state = world.getBlockState(leaf);
            if (!state.is(NetherLeavesBlocks.RUBEUS_LEAVES)) continue;
            final Integer dist = distance.get(leaf);
            if (dist != null) {
                BlocksHelper.setWithoutUpdate(world, leaf, state.setValue(LeavesBlock.DISTANCE, dist));
            } else {
                BlocksHelper.setWithoutUpdate(world, leaf, Blocks.AIR.defaultBlockState());
                final BlockPos below = leaf.below();
                if (world.getBlockState(below).is(cone.getBlock())) {
                    BlocksHelper.setWithoutUpdate(world, below, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    public boolean grow(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            NaturalTreeConfiguration config,
            StructureGeneratorThreadContext context
    ) {
        final boolean natural = config.natural;
        context.clear();
        final Set<BlockPos> leaves = new HashSet<>();
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
        float scale = MHelper.randRange(0.5F, 1F, random);
        int minCount = scale < 0.75 ? 3 : 4;
        int maxCount = scale < 0.75 ? 5 : 7;
        int count = MHelper.randRange(minCount, maxCount, random);
        for (int n = 0; n < count; n++) {
            float branchSize = MHelper.randRange(0.5F, 0.8F, random) * scale;
            float angle = n * MHelper.PI2 / count;
            float radius = CURVE_X[0] * branchSize;
            int x1 = Math.round(pos.getX() + radius * (float) Math.cos(angle) + MHelper.randRange(
                    -2F,
                    2F,
                    random
            ) * branchSize);
            int y1 = Math.round(pos.getY() + CURVE_Y[0] * branchSize + MHelper.randRange(-2F, 2F, random) * branchSize);
            int z1 = Math.round(pos.getZ() + radius * (float) Math.sin(angle) + MHelper.randRange(
                    -2F,
                    2F,
                    random
            ) * branchSize);
            float crownR = 5 * branchSize;
            if (crownR < 1.5F)
                crownR = 1.5F;
            crown(world, x1, y1 + 1, z1, crownR, random, leaves);

            int middle = Math.round(pos.getY() + (MIDDLE_Y + MHelper.randRange(-2, 2, random)) * branchSize);
            boolean generate = true;
            for (int i = 1; i < CURVE_X.length && generate; i++) {
                radius = CURVE_X[i] * branchSize;
                int x2 = Math.round(pos.getX() + radius * (float) Math.cos(angle) + MHelper.randRange(
                        -2F,
                        2F,
                        random
                ) * branchSize);
                int y2 = Math.round(pos.getY() + CURVE_Y[i] * branchSize + (CURVE_Y[i] > 0
                        ? MHelper.randRange(
                        -2F,
                        2F,
                        random
                ) * branchSize
                        : 0));
                int z2 = Math.round(pos.getZ() + radius * (float) Math.sin(angle) + MHelper.randRange(
                        -2F,
                        2F,
                        random
                ) * branchSize);

                if (CURVE_Y[i] <= 0) {
                    if (!isGround(world.getBlockState(context.POS.set(x2, y2, z2)))) {
                        boolean noGround = true;
                        for (int d = 1; d < 3; d++) {
                            if (isGround(world.getBlockState(context.POS.set(x2, y2 - d, z2)))) {
                                y2 -= d;
                                noGround = false;
                                break;
                            }
                        }
                        if (noGround) {
                            x2 = pos.getX();
                            y2 = pos.getY();
                            generate = false;
                        }
                    }
                }

                line(world, x1, y1, z1, x2, y2, z2, middle, context);
                x1 = x2;
                y1 = y2;
                z1 = z2;
            }
        }

        BlockState state;
        Iterator<BlockPos> iterator = context.TOP.iterator();
        while (iterator.hasNext()) {
            BlockPos bpos = iterator.next();
            if (bpos != null) {
                if (context.POINTS.contains(bpos.above()) && !context.TOP.contains(bpos.above()))
                    iterator.remove();
            }
        }

        iterator = context.MIDDLE.iterator();
        while (iterator.hasNext()) {
            BlockPos bpos = iterator.next();
            if (bpos != null) {
                BlockPos up = bpos.above();
                if (context.MIDDLE.contains(up) || (!context.TOP.contains(up) && context.POINTS.contains(up)))
                    iterator.remove();
            } else
                iterator.remove();
        }

        for (BlockPos bpos : context.POINTS) {
            if (context.POINTS.contains(bpos.above()) && context.POINTS.contains(bpos.below())) {
                state = NetherWoodBlocks.MAT_RUBEUS.getLog().defaultBlockState();
                if (context.MIDDLE.contains(bpos))
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.MIDDLE),
                            false,
                            random
                    );
                else if (context.TOP.contains(bpos))
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.TOP),
                            false,
                            random
                    );
                else
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.BOTTOM),
                            natural,
                            random
                    );
            } else {
                state = NetherWoodBlocks.MAT_RUBEUS.getBark().defaultBlockState();
                if (context.MIDDLE.contains(bpos))
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.MIDDLE),
                            false,
                            random
                    );
                else if (context.TOP.contains(bpos))
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.TOP),
                            false,
                            random
                    );
                else
                    setCondition(
                            world,
                            bpos,
                            pos.getY(),
                            state.setValue(RubeusLog.SHAPE, TripleShape.BOTTOM),
                            natural,
                            random
                    );
            }
        }

        updateDistances(world, leaves, context);

        context.POINTS.clear();
        context.MIDDLE.clear();
        context.TOP.clear();

        return true;
    }


    private void line(
            LevelAccessor level,
            int x1,
            int y1,
            int z1,
            int x2,
            int y2,
            int z2,
            int middleY,
            StructureGeneratorThreadContext context
    ) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int dz = z2 - z1;
        int mx = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
        float fdx = (float) dx / mx;
        float fdy = (float) dy / mx;
        float fdz = (float) dz / mx;
        float px = x1;
        float py = y1;
        float pz = z1;

        BlockPos pos = context.POS.set(x1, y1, z1).immutable();
        context.POINTS.add(pos);
        if (pos.getY() == middleY)
            context.MIDDLE.add(pos);
        else if (pos.getY() > middleY)
            context.TOP.add(pos);

        pos = context.POS.set(x2, y2, z2).immutable();
        context.POINTS.add(pos);
        if (pos.getY() == middleY)
            context.MIDDLE.add(pos);
        else if (pos.getY() > middleY)
            context.TOP.add(pos);

        for (int i = 0; i < mx; i++) {
            px += fdx;
            py += fdy;
            pz += fdz;

            context.POS.set(Math.round(px), Math.round(py), Math.round(pz));
            pos = context.POS.immutable();
            context.POINTS.add(pos);
            if (context.POS.getY() == middleY)
                context.MIDDLE.add(pos);
            else if (context.POS.getY() > middleY)
                context.TOP.add(pos);
        }
    }

    private void crown(
            LevelAccessor world,
            int x,
            int y,
            int z,
            float radius,
            RandomSource random,
            Set<BlockPos> placed
    ) {
        final BlockPos.MutableBlockPos POS = new BlockPos.MutableBlockPos();

        // Not persistent: these leaves decay once the tree is felled (#97). updateDistances() gives each
        // one its real distance afterwards; until then they carry the default (DECAY_DISTANCE).
        BlockState leaves = NetherLeavesBlocks.RUBEUS_LEAVES.defaultBlockState();
        BlockState cone = NetherWoodBlocks.MAT_RUBEUS.getCone().defaultBlockState();
        float halfR = radius * 0.5F;
        float r2 = radius * radius;
        int start = (int) Math.floor(-radius);
        for (int cy = start; cy <= radius; cy++) {
            int cy2_out = cy * cy;
            float cy2_in = cy + halfR;
            cy2_in *= cy2_in;
            // Mth.floor, not an (int) cast: the cast rounds toward zero, which lifts the crown a block off
            // its branch whenever it sits below y=0.
            POS.setY(Mth.floor(y + cy - halfR));
            for (int cx = start; cx <= radius; cx++) {
                int cx2 = cx * cx;
                POS.setX(x + cx);
                for (int cz = start; cz <= radius; cz++) {
                    int cz2 = cz * cz;
                    if (cx2 + cy2_out + cz2 < r2 && cx2 + cy2_in + cz2 > r2) {
                        POS.setZ(z + cz);
                        if (setIfAirLeaves(world, POS, leaves)) placed.add(POS.immutable());
                        if (((POS.getX() + POS.getZ()) & 1) == 0 && random.nextInt(6) == 0)
                            setIfAir(world, POS.below(), cone);
                    }
                }
            }
        }
    }

    private void setCondition(
            LevelAccessor world,
            BlockPos pos,
            int y,
            BlockState state,
            boolean moss,
            RandomSource random
    ) {
        if (pos.getY() > y)
            setIfAir(world, pos, state);
        else
            setIfGroundOrAir(world, pos, state);
        if (moss && Math.abs(pos.getY() - y) < 4) {
            for (Direction dir : BlocksHelper.HORIZONTAL) {
                if (random.nextInt(3) > 0)
                    setIfAir(world, pos.relative(dir), NetherPlantBlocks.JUNGLE_MOSS.defaultBlockState().setValue(
                            BlockPlantWall.FACING, dir));
            }
        }
    }

    // Cones count as free space here and in setIfGroundOrAir: a crown hangs them before its own branch
    // is drawn up into it, and the wood has to win or the branch gets a gap.
    private void setIfAir(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockState bState = world.getBlockState(pos);
        if (world.isEmptyBlock(pos)
                || bState.canBeReplaced()
                || bState.getBlock() == NetherLeavesBlocks.RUBEUS_LEAVES
                || bState.getBlock() == NetherWoodBlocks.MAT_RUBEUS.getCone()
        ) {
            if (state.canSurvive(world, pos)) {
                BlocksHelper.setWithoutUpdate(world, pos, state);
            }
        }
    }

    private void setIfGroundOrAir(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockState bState = world.getBlockState(pos);
        if (bState.isAir()
                || bState.getBlock() == NetherLeavesBlocks.RUBEUS_LEAVES
                || bState.getBlock() == NetherWoodBlocks.MAT_RUBEUS.getCone()
                || bState.canBeReplaced()
                || BlocksHelper.isNetherGround(bState)) {
            if (state.canSurvive(world, pos)) {
                BlocksHelper.setWithoutUpdate(world, pos, state);
            }
        }
    }

    private boolean setIfAirLeaves(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockState bState = world.getBlockState(pos);
        if (world.isEmptyBlock(pos) || bState.canBeReplaced()) {
            BlocksHelper.setWithoutUpdate(world, pos, state);
            return true;
        }
        return false;
    }

    @Override
    public boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            NaturalTreeConfiguration configuration
    ) {
        return grow(
                level,
                pos,
                random,
                new NaturalTreeConfiguration(false, configuration.distance),
                NetherThreadDataStorage.generatorForThread().context
        );
    }
}
