package org.betterx.betternether.world.features;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherWallPlantBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.MHelper;
import org.betterx.betternether.blocks.BlockPlantWall;
import org.betterx.betternether.noise.OpenSimplexNoise;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;
import org.betterx.betternether.world.structures.plants.LegacyStructureAnchorTree;
import de.ambertation.wover.feature.api.features.GrowableFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class AnchorTreeFeature extends ContextFeature<NoneFeatureConfiguration> implements GrowableFeature<NoneFeatureConfiguration> {
    public AnchorTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
        legacyStructure = new LegacyStructureAnchorTree();
    }

    protected static final OpenSimplexNoise NOISE = new OpenSimplexNoise(2145);
    public static final Block[] wallPlants = {
            NetherPlantBlocks.JUNGLE_MOSS,
            NetherPlantBlocks.JUNGLE_MOSS,
            NetherWallPlantBlocks.WALL_MUSHROOM_BROWN,
            NetherWallPlantBlocks.WALL_MUSHROOM_RED
    };
    final private LegacyStructureAnchorTree legacyStructure;

    /**
     * The leafy half of an anchor tree.
     * <p>
     * This feature places no leaves at all - its own canopy is the giant lucis mushrooms below. What reads
     * as an anchor tree's foliage in the world comes from {@link AnchorTreeBranchFeature}, which the
     * Upside Down Forest scatters under its ceiling as a separate feature alongside this one (see
     * {@code UpsideDownForest}: ANCHOR_TREE, ANCHOR_TREE_BRANCH and ANCHOR_TREE_ROOT are three entries,
     * not one). A sapling has no biome to do that composing for it, so {@link #growFoliage} does it here.
     * Held as an instance the same way {@code AnchorTreeRootFeature} holds its {@code LucisFeature}.
     */
    private static final AnchorTreeBranchFeature FOLIAGE = new AnchorTreeBranchFeature();

    /**
     * The generator depth the anchor tree is shaped for, used to derive {@code scale_factor}.
     * <p>
     * Only the <em>scale</em> is pinned for a grown tree, never {@code MAX_HEIGHT} itself. That parameter
     * does two unrelated jobs: it sets the scale, and {@link #cylinder} also uses it as an absolute Y
     * ceiling ({@code pos.getY() < MAX_HEIGHT - 2}). Passing 128 for both looked like it just fixed the
     * scale, and in the Nether - Y 0 to 127 - it does. In the Overworld it silently clipped every block
     * above y=126, so a tree planted higher than that lost its whole trunk and canopy and came out as a
     * stump. The scale is pinned here; the Y bound keeps coming from the real generator depth.
     */
    private static final int SHAPED_FOR_DEPTH = 128;

    private static int toMiddle(int val) {

        return val + (7 - (val & 0xF));
    }

    @Override
    protected boolean place(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            NoneFeatureConfiguration config,
            int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    ) {
        pos = new BlockPos(toMiddle(pos.getX()), pos.getY(), toMiddle(pos.getZ()));

        BlockPos down = pos.below(BlocksHelper.downRay(world, pos, MAX_HEIGHT));
        if (canGenerate(pos)) {
            // Result deliberately dropped: this returned an unconditional true before, and worldgen keeps
            // it that way. Propagating the real outcome cannot move a block - the placement pipeline only
            // accumulates it into a "placed anything" flag - but the willow crown taught that reasoning of
            // that shape is worth exactly nothing here without a measurement behind it. Only
            // GrowableFeature#grow, which has to tell the sapling whether to stay standing, reads it.
            grow(world, pos, down, random, MAX_HEIGHT, context, false);
            return true;
        }
        return false;
    }

    /**
     * Grows the tree from a position a player planted, upward from the floor to whatever ceiling is above
     * it.
     * <p>
     * {@code pos} is the floor end here, not the ceiling one {@link #place} works from: the giant grows
     * from a 2x2 planted on the ground, and the far end is found by {@link BlocksHelper#upRay} - the next
     * ceiling, or as far as the generator's depth allows if there is none. The tree's own geometry spans
     * {@code down..up} and does not care which end it was anchored at, so only the search direction
     * differs. Everything downward-anchored stays with the small hanging branch, which is still what a
     * single ceiling-hung sapling grows.
     * <p>
     * Deliberately skips the {@link #toMiddle} snap {@link #place} applies. That snap exists to pull a
     * naturally placed tree towards the centre of its chunk, where its 32-block reach has the best chance
     * of staying inside the write zone; applied to a sapling it would move the tree up to 15 blocks away
     * from the four saplings the player planted. Outside a {@code WorldGenRegion} there is no write zone
     * to respect anyway, so nothing is lost by growing where the player asked.
     */
    @Override
    public boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            NoneFeatureConfiguration configuration
    ) {
        final int maxHeight = level.getLevel().getChunkSource().getGenerator().getGenDepth();
        // All the way to the next ceiling, or to the build limit when there is none. Capping this at a
        // fixed span is what made the tree look sawn off: the upward roots run past whatever the span
        // allows and the write bounds then slice them flat, so a cap does not shorten the tree, it
        // truncates it.
        final BlockPos up = pos.above(BlocksHelper.upRay(level, pos, level.getLevel().getMaxY() - pos.getY()));
        return grow(
                level, up, pos, random, maxHeight,
                NetherThreadDataStorage.generatorForThread().context,
                true
        );
    }

    /**
     * Hangs leafy branch clusters off the upper trunk, so a grown tree ends up looking like the ones in
     * the Upside Down Forest rather than a bare stem with mushrooms on it.
     * <p>
     * Anchored to the trunk rather than to the ceiling on purpose. Hanging them from the roof is what
     * worldgen effectively does, but it only works because a natural tree sits under a cavern roof that
     * runs for tens of blocks in every direction. A player plants under whatever they built - often a
     * patch of netherrack barely wider than the saplings - and then every candidate a few blocks out has
     * open air above it and produces nothing at all. The trunk is always there.
     *
     * @see #FOLIAGE
     */
    private void growFoliage(
            ServerLevelAccessor level,
            BlockPos up,
            BlockPos down,
            RandomSource random,
            StructureGeneratorThreadContext context
    ) {
        // Snapshot before the first FOLIAGE.grow: it clears the shared per-thread context, which is the
        // very set being read here.
        final int span = up.getY() - down.getY();
        // The clusters hang ~20 blocks below where they start, so sampling only the top of the trunk
        // leaves the lower half of a tall tree completely bare. Take the upper two thirds and spread the
        // clusters through it by height band instead of drawing them all from one pool - drawing at random
        // clumps them wherever the trunk happens to have the most blocks, which is the top.
        final int minY = up.getY() - Math.max(6, span * 2 / 3);
        final List<BlockPos> canopy = new ArrayList<>();
        for (BlockPos b : context.BLOCKS) {
            if (b.getY() >= minY && b.getY() <= up.getY()) canopy.add(b);
        }
        if (canopy.isEmpty()) return;
        canopy.sort((a, b) -> Integer.compare(b.getY(), a.getY()));

        // Roughly one cluster per 8 blocks of trunk, so a tall tree is not decorated as sparsely as a
        // short one, with a floor that keeps a minimum-height tree from looking bare.
        final int count = Mth.clamp(span / 8, 4, 48);
        for (int i = 0; i < count; i++) {
            // One band per cluster, top to bottom, so the foliage is distributed over the trunk's height.
            final int from = canopy.size() * i / count;
            final int to = Math.max(from + 1, canopy.size() * (i + 1) / count);
            final BlockPos on = canopy.get(from + random.nextInt(to - from));

            // Walk out from the trunk block and take the FIRST air position, so the cluster starts against
            // the trunk's surface. Picking a point 3-8 blocks out instead leaves it hanging in open air:
            // AnchorTreeBranchFeature's own geometry converges on the position it is given and spreads
            // outward from there, so it never reaches back to the trunk and the branches float.
            for (int attempt = 0; attempt < 4; attempt++) {
                final double angle = random.nextDouble() * Math.PI * 2;
                final double dx = Math.cos(angle);
                final double dz = Math.sin(angle);
                BlockPos at = null;
                for (int step = 1; step <= 10; step++) {
                    final BlockPos probe = new BlockPos(
                            on.getX() + (int) Math.round(dx * step),
                            on.getY(),
                            on.getZ() + (int) Math.round(dz * step)
                    );
                    if (level.getBlockState(probe).isAir()) {
                        at = probe;
                        break;
                    }
                }
                if (at == null) continue;
                FOLIAGE.grow(level, at, random, NoneFeatureConfiguration.INSTANCE);
                break;
            }
        }
    }

    private boolean canGenerate(BlockPos pos) {
        return true;
    }

    private boolean grow(
            ServerLevelAccessor level,
            BlockPos up,
            BlockPos down,
            RandomSource random,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context,
            final boolean airOnly
    ) {
        // Worldgen scales to the dimension it is generating; a grown tree is always built at the scale the
        // anchor tree is designed for. MAX_HEIGHT itself stays the real one either way - see
        // SHAPED_FOR_DEPTH for what happened when it did not.
        final float scale_factor = airOnly ? 1.0f : MAX_HEIGHT / (float) SHAPED_FOR_DEPTH;
        int HEIGHT_64;
        int HEIGHT_45;
        int HEIGHT_90;
        final int SEGMENT_LENGTH;

        HEIGHT_64 = (int) (MAX_HEIGHT / 4.0 + MHelper.nextFloat(random, 32));
        HEIGHT_45 = (int) (20 + MHelper.nextFloat(random, 20 * scale_factor));
        HEIGHT_90 = (int) (MAX_HEIGHT / 2.0 + MHelper.nextFloat(random, 15 * scale_factor));
        SEGMENT_LENGTH = (int) ((15 + MHelper.nextFloat(random, 5 * scale_factor)) * scale_factor);

        if (up.getY() - down.getY() < 30) return false;

        // The three bands that decide where mushrooms and moss go are absolute Y values tuned for a tree
        // spanning most of a 128-block Nether: mushrooms only between ~20-40 and ~64-79, moss only above
        // ~32-64. A naturally placed tree runs floor to ceiling, so it always crosses them. A tree grown
        // under a ceiling the player put up spans whatever they gave it, and outside those windows it
        // comes out as a bare stem with no canopy at all. Rescaling them onto the trunk's own span keeps
        // each decoration at the same relative height it would sit at naturally. Derived from the values
        // already drawn rather than drawn afresh, so worldgen's random stream is untouched.
        if (airOnly) {
            final int base = down.getY();
            final float span = (up.getY() - base) / (float) MAX_HEIGHT;
            HEIGHT_45 = base + (int) (HEIGHT_45 * span);
            HEIGHT_64 = base + (int) (HEIGHT_64 * span);
            HEIGHT_90 = base + (int) (HEIGHT_90 * span);
        }

        int pd = BlocksHelper.downRay(level, down, MAX_HEIGHT) + 1;
        for (int i = 0; i < 5; i++) {
            Block block = level.getBlockState(down.below(pd + i)).getBlock();
            if (block == Blocks.NETHER_BRICKS || block == NetherStoneBlocks.NETHER_BRICK_TILE_LARGE || block == NetherStoneBlocks.NETHER_BRICK_TILE_SMALL)
                return false;
        }

        final BlockPos trunkTop = lerp(down, up, 0.6);
        final BlockPos trunkBottom = lerp(down, up, 0.3);

        int count = (trunkTop.getY() - trunkBottom.getY()) / 7;
        if (count < 2) count = 2;
        List<BlockPos> blocks = line(trunkBottom, trunkTop, count, random, 2.5);

        context.BLOCKS.clear();


        count = Math.min(7, Math.max(3, (up.getY() - down.getY()) / (int) (10 * scale_factor) - 1));
        double radius = Math.min(7, Math.max(3.5, (up.getY() - down.getY()) / 15));

        drawLine(level, blocks, radius + (0.5 * scale_factor), MAX_HEIGHT, context);

        buildBigCircle(
                level,
                up,
                trunkTop,
                SEGMENT_LENGTH,
                count,
                2,
                random.nextDouble() * Math.PI * 2,
                radius,
                random,
                MAX_HEIGHT,
                context
        );
        buildBigCircle(
                level,
                up,
                trunkBottom,
                -SEGMENT_LENGTH,
                count,
                2,
                random.nextDouble() * Math.PI * 2,
                radius,
                random,
                MAX_HEIGHT,
                context
        );

        BlockState state;
        int offset = random.nextInt(4);
        final int minBuildHeight = level.getMinY() + 1;
        // A grown tree is confined to the space it was grown in: from the anchor point down to the first
        // floor under it, and no higher than the block the saplings hung from.
        //
        // Air-only alone is not enough, because it only decides what a block may be written *over*, not
        // where the geometry goes. The trunk and its roots are shaped to span a whole cavern, so they run
        // straight past a nearby floor or ceiling - skipping the solid blocks, then carrying on in the
        // open cave underneath or the sky above, which is what made a sapling-grown tree reappear in two
        // places it was never planted. Clamping the write bounds is what actually ends it at the floor.
        final net.minecraft.world.level.levelgen.structure.BoundingBox blockBox = airOnly
                ? BlocksHelper.decorationBounds(level, up, down.getY(), up.getY())
                : BlocksHelper.decorationBounds(level, up, minBuildHeight, MAX_HEIGHT - 2);
        // The canopy is these mushrooms - AnchorTreeFeature places no leaves at all - and the 1-in-32 roll
        // per side is tuned for a trunk spanning a whole cavern. A tree grown under a low ceiling has a
        // fraction of those trunk blocks and so drew a fraction of the mushrooms: one or two for the whole
        // tree. Scaling the roll by the same fraction restores roughly the density a natural tree has.
        final int mushroomChance = airOnly
                ? Math.max(2, (int) (32 * scale_factor * (up.getY() - down.getY()) / (float) MAX_HEIGHT))
                : (int) (32 * scale_factor);

        for (BlockPos bpos : context.BLOCKS) {
            if (!blockBox.isInside(bpos)) continue;
            state = level.getBlockState(bpos);
            // Air-only for a grown tree. This is also what stops a sapling-grown trunk from boring up
            // through the ceiling the player planted it under: the roots above the anchor point are
            // worldgen's way of gripping the cavern roof, and with nothing but air writable they simply
            // stop at it instead.
            if (airOnly ? !state.isAir() : (!BlocksHelper.isNetherGround(state) && !state.canBeReplaced()))
                continue;
            boolean blockUp = true;
            if ((blockUp = context.BLOCKS.contains(bpos.above())) && context.BLOCKS.contains(bpos.below()))
                BlocksHelper.setWithoutUpdate(level, bpos, NetherWoodBlocks.MAT_ANCHOR_TREE.getLog().defaultBlockState());
            else
                BlocksHelper.setWithoutUpdate(level, bpos, NetherWoodBlocks.MAT_ANCHOR_TREE.getBark().defaultBlockState());

            if (bpos.getY() > HEIGHT_45 && bpos.getY() < HEIGHT_90 && (bpos.getY() & 3) == offset && NOISE.eval(
                    bpos.getX() * 0.1,
                    bpos.getY() * 0.1,
                    bpos.getZ() * 0.1
            ) > 0) {
                if (random.nextInt(mushroomChance) == 0 && !context.BLOCKS.contains(bpos.north()))
                    makeMushroom(level, bpos.north(), random.nextDouble() * 3 + 1.5, blockBox, airOnly);
                if (random.nextInt(mushroomChance) == 0 && !context.BLOCKS.contains(bpos.south()))
                    makeMushroom(level, bpos.south(), random.nextDouble() * 3 + 1.5, blockBox, airOnly);
                if (random.nextInt(mushroomChance) == 0 && !context.BLOCKS.contains(bpos.east()))
                    makeMushroom(level, bpos.east(), random.nextDouble() * 3 + 1.5, blockBox, airOnly);
                if (random.nextInt(mushroomChance) == 0 && !context.BLOCKS.contains(bpos.west()))
                    makeMushroom(level, bpos.west(), random.nextDouble() * 3 + 1.5, blockBox, airOnly);
            }

            if (bpos.getY() > HEIGHT_64) {
                final BlockState above = level.getBlockState(bpos.above());
                // The only write here that carried no bounds check: bpos is inside blockBox, bpos.above()
                // need not be, and on a grown tree the box now ends exactly at the anchor point.
                //
                // Bounded on the grow path only. Adding the check for worldgen too looked provably free -
                // cylinder() caps trunk blocks below MAX_HEIGHT-2, so the block above one is always inside
                // the box - but "provably free" is exactly what was assumed about the willow crown reorder
                // before an A/B measured it changing natural trees. Worldgen keeps the call it had.
                if (!blockUp && (airOnly ? above.isAir() : above.canBeReplaced())) {
                    if (airOnly) {
                        BlocksHelper.setWithoutUpdate(
                                level,
                                bpos.above(),
                                NetherPlantBlocks.MOSS_COVER.defaultBlockState(),
                                blockBox
                        );
                    } else {
                        BlocksHelper.setWithoutUpdate(
                                level,
                                bpos.above(),
                                NetherPlantBlocks.MOSS_COVER.defaultBlockState()
                        );
                    }
                }

                if (NOISE.eval(bpos.getX() * 0.05, bpos.getY() * 0.05, bpos.getZ() * 0.05) > 0) {
                    state = wallPlants[random.nextInt(wallPlants.length)].defaultBlockState();
                    BlockPos _pos = bpos.north();
                    if (random.nextInt(8) == 0 && !context.BLOCKS.contains(_pos) && level.isEmptyBlock(_pos) && _pos.getZ() >= blockBox.minZ())
                        BlocksHelper.setWithoutUpdate(
                                level,
                                _pos,
                                state.setValue(BlockPlantWall.FACING, Direction.NORTH)
                        );

                    _pos = bpos.south();
                    if (random.nextInt(8) == 0 && !context.BLOCKS.contains(_pos) && level.isEmptyBlock(_pos) && _pos.getZ() <= blockBox.maxZ())
                        BlocksHelper.setWithoutUpdate(
                                level,
                                _pos,
                                state.setValue(BlockPlantWall.FACING, Direction.SOUTH)
                        );

                    _pos = bpos.east();
                    if (random.nextInt(8) == 0 && !context.BLOCKS.contains(_pos) && level.isEmptyBlock(_pos) && _pos.getX() <= blockBox.maxX())
                        BlocksHelper.setWithoutUpdate(
                                level,
                                _pos,
                                state.setValue(BlockPlantWall.FACING, Direction.EAST)
                        );

                    _pos = bpos.west();
                    if (random.nextInt(8) == 0 && !context.BLOCKS.contains(_pos) && level.isEmptyBlock(_pos) && _pos.getX() >= blockBox.minX())
                        BlocksHelper.setWithoutUpdate(
                                level,
                                _pos,
                                state.setValue(BlockPlantWall.FACING, Direction.WEST)
                        );
                }
            }
        }

        // Only on the grow path, and only here - the trunk is finished and context.BLOCKS still holds it,
        // which is what the clusters are hung from. Worldgen gets its foliage from the separate
        // ANCHOR_TREE_BRANCH entry in the biome instead.
        if (airOnly) growFoliage(level, up, down, random, context);

        return true;
    }

    private void buildBigCircle(
            ServerLevelAccessor level,
            BlockPos seedPos,
            BlockPos pos,
            int length,
            int count,
            int iteration,
            double angle,
            double size,
            RandomSource random,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    ) {
        if (iteration < 0) return;
        List<List<BlockPos>> lines = circleLinesEnds(
                level,
                seedPos,
                pos,
                angle,
                count,
                length,
                Math.abs(length) * 0.7,
                random,
                iteration == 0,
                MAX_HEIGHT
        );
        double sizeSmall = size * 0.8;
        length *= 0.8;
        angle += Math.PI * 4 / count;
        angle += random.nextDouble() * angle * 0.75;
        for (List<BlockPos> line : lines) {
            drawLine(level, line, size, MAX_HEIGHT, context);
            buildBigCircle(
                    level,
                    seedPos,
                    line.get(1),
                    length,
                    count,
                    iteration - 1,
                    angle,
                    sizeSmall,
                    random,
                    MAX_HEIGHT,
                    context
            );
        }
    }

    private void drawLine(
            ServerLevelAccessor level,
            List<BlockPos> blocks,
            double radius,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    ) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            BlockPos a = blocks.get(i);
            BlockPos b = blocks.get(i + 1);
            if (b.getY() < a.getY()) {
                BlockPos c = b;
                b = a;
                a = c;
            }
            double max = b.getY() - a.getY();
            if (max < 1) max = 1;
            for (int y = a.getY(); y <= b.getY(); y++)
                cylinder(lerpCos(a, b, y, (y - a.getY()) / max), radius, MAX_HEIGHT, context);
        }
    }

    private BlockPos lerp(BlockPos start, BlockPos end, double mix) {
        double x = Mth.lerp(mix, start.getX(), end.getX());
        double y = Mth.lerp(mix, start.getY(), end.getY());
        double z = Mth.lerp(mix, start.getZ(), end.getZ());
        return new BlockPos((int) x, (int) y, (int) z);
    }

    private BlockPos lerpCos(BlockPos start, BlockPos end, int y, double mix) {
        double v = lcos(mix);
        double x = Mth.lerp(v, start.getX(), end.getX());
        double z = Mth.lerp(v, start.getZ(), end.getZ());
        return new BlockPos((int) x, (int) y, (int) z);
    }

    private double lcos(double mix) {
        return Mth.clamp(0.5 - Math.cos(mix * Math.PI) * 0.5, 0, 1);
    }

    private List<BlockPos> line(BlockPos start, BlockPos end, int count, RandomSource random, double range) {
        List<BlockPos> result = new ArrayList<BlockPos>(count);
        int max = count - 1;
        result.add(start);
        for (int i = 1; i < max; i++) {
            double delta = (double) i / max;
            double x = Mth.lerp(delta, start.getX(), end.getX()) + random.nextGaussian() * range;
            double y = Mth.lerp(delta, start.getY(), end.getY());
            double z = Mth.lerp(delta, start.getZ(), end.getZ()) + random.nextGaussian() * range;
            result.add(new BlockPos((int) x, (int) y, (int) z));
        }
        result.add(end);
        return result;
    }

    private void cylinder(BlockPos pos, double radius, final int MAX_HEIGHT, StructureGeneratorThreadContext context) {
        int x1 = MHelper.floor(pos.getX() - radius);
        int z1 = MHelper.floor(pos.getZ() - radius);
        int x2 = MHelper.floor(pos.getX() + radius + 1);
        int z2 = MHelper.floor(pos.getZ() + radius + 1);
        radius *= radius;

        for (int x = x1; x <= x2; x++) {
            int px2 = x - pos.getX();
            px2 *= px2;
            for (int z = z1; z <= z2; z++) {
                int pz2 = z - pos.getZ();
                pz2 *= pz2;
                if (px2 + pz2 <= radius * (NOISE.eval(
                        x * 0.5,
                        pos.getY() * 0.5,
                        z * 0.5
                ) * 0.25 + 0.75) && pos.getY() > 2 && pos.getY() < MAX_HEIGHT - 2)
                    context.BLOCKS.add(new BlockPos(x, pos.getY(), z));
            }
        }
    }

    private List<List<BlockPos>> circleLinesEnds(
            ServerLevelAccessor level,
            BlockPos seedPos,
            BlockPos pos,
            double startAngle,
            int count,
            int length,
            double inRadius,
            RandomSource random,
            boolean findSurface,
            final int MAX_HEIGHT
    ) {
        final int MAX_DIST = 16;
        List<List<BlockPos>> result = new ArrayList<List<BlockPos>>(count);
        double angle = Math.PI * 2 / count;
        for (int i = 0; i < count; i++) {
            double radius = inRadius * (MHelper.nextDouble(random, 0.25) + 0.8); //jes the sum may be bigger than one :)

            double x = pos.getX() + Math.sin(startAngle) * radius;
            if (x - seedPos.getX() > MAX_DIST) x = seedPos.getX() + MAX_DIST - random.nextInt(10);
            if (x - seedPos.getX() < -MAX_DIST) x = seedPos.getX() - MAX_DIST + random.nextInt(10);

            double z = pos.getZ() + Math.cos(startAngle) * radius;
            if (z - seedPos.getZ() > MAX_DIST) z = seedPos.getZ() + MAX_DIST - random.nextInt(10);
            if (z - seedPos.getZ() < -MAX_DIST) z = seedPos.getZ() - MAX_DIST + random.nextInt(10);

            BlockPos end = new BlockPos(
                    (int) x,
                    (int) (pos.getY() + length + length * random.nextDouble() * 0.5),
                    (int) z
            );
            List<BlockPos> elem = new ArrayList<>(2);
            elem.add(pos);
            elem.add(end);
            result.add(elem);

            if (findSurface && end.getY() > 2 && end.getY() < MAX_HEIGHT - 2) {
                int dist = length < 0
                        ? -BlocksHelper.downRay(level, end, Math.abs(length * 2))
                        : BlocksHelper.upRay(level, end, Math.abs(length * 2));
                if (dist > 0) {
                    if (Math.abs(seedPos.getX() - x) > MAX_DIST || Math.abs(seedPos.getZ() - z) > MAX_DIST) radius = 2;
                    result.addAll(circleLinesEnds(
                            level,
                            seedPos,
                            end,
                            MHelper.nextFloat(random, 360),
                            radius < 5 ? 1 : (count % 2 + 1),
                            dist,
                            radius / 2,
                            random,
                            findSurface,
                            MAX_HEIGHT
                    ));
                }
            }

            startAngle += angle;
        }
        return result;
    }

    protected static void makeMushroom(
            ServerLevelAccessor world,
            BlockPos pos,
            double radius,
            BoundingBox bounds,
            boolean airOnly
    ) {
        if (airOnly ? !world.getBlockState(pos).isAir() : !world.getBlockState(pos).canBeReplaced()) return;

        int x1 = MHelper.floor(pos.getX() - radius);
        int z1 = MHelper.floor(pos.getZ() - radius);
        int x2 = MHelper.floor(pos.getX() + radius + 1);
        int z2 = MHelper.floor(pos.getZ() + radius + 1);
        radius *= radius;

        List<BlockPos> placed = new ArrayList<BlockPos>((int) (radius * 4));
        for (int x = x1; x <= x2; x++) {
            int px2 = x - pos.getX();
            px2 *= px2;
            for (int z = z1; z <= z2; z++) {
                int pz2 = z - pos.getZ();
                pz2 *= pz2;
                if (px2 + pz2 <= radius) {
                    BlockPos p = new BlockPos(x, pos.getY(), z);
                    final BlockState at = world.getBlockState(p);
                    if ((airOnly ? at.isAir() : at.canBeReplaced()) && bounds.isInside(p)) {
                        placed.add(p);
                    }
                }
            }
        }

        for (BlockPos p : placed) {
            boolean north = world.getBlockState(p.north()).getBlock() != NetherMushroomBlocks.GIANT_LUCIS;
            boolean south = world.getBlockState(p.south()).getBlock() != NetherMushroomBlocks.GIANT_LUCIS;
            boolean east = world.getBlockState(p.east()).getBlock() != NetherMushroomBlocks.GIANT_LUCIS;
            boolean west = world.getBlockState(p.west()).getBlock() != NetherMushroomBlocks.GIANT_LUCIS;
            BlockState state = NetherMushroomBlocks.GIANT_LUCIS.defaultBlockState();
            BlocksHelper.setWithoutUpdate(world, p, state
                    .setValue(HugeMushroomBlock.NORTH, north)
                    .setValue(HugeMushroomBlock.SOUTH, south)
                    .setValue(HugeMushroomBlock.EAST, east)
                    .setValue(HugeMushroomBlock.WEST, west));
        }
    }
}
