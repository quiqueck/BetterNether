package org.betterx.betternether.world.features;

import org.betterx.bclib.api.v3.levelgen.features.UserGrowableFeature;
import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.MHelper;
import org.betterx.betternether.blocks.BlockPlantWall;
import org.betterx.betternether.noise.OpenSimplexNoise;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.world.NetherBiomeBuilder;
import org.betterx.betternether.world.structures.StructureGeneratorThreadContext;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.betternether.world.structures.plants.LegacyStructureAnchorTree;

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

public class AnchorTreeFeature extends ContextFeature<NoneFeatureConfiguration> implements UserGrowableFeature<NoneFeatureConfiguration> {
    public AnchorTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
        legacyStructure = new LegacyStructureAnchorTree();
    }

    protected static final OpenSimplexNoise NOISE = new OpenSimplexNoise(2145);
    public static final Block[] wallPlants = {
            NetherBlocks.JUNGLE_MOSS,
            NetherBlocks.JUNGLE_MOSS,
            NetherBlocks.WALL_MUSHROOM_BROWN,
            NetherBlocks.WALL_MUSHROOM_RED
    };
    final private LegacyStructureAnchorTree legacyStructure;

    /**
     * How far sideways a naturally placed tree can reach from its (snapped) origin: branch endpoints are
     * clamped to 16 blocks and the cylinders drawn around them are at most 7 wide. A tree that close to a
     * city building is not placed at all - see {@link CityStructure#buildingsWithin}.
     */
    private static final int CITY_CLEARANCE = 16 + 7;

    /**
     * The leafy half of an anchor tree. This feature places no leaves itself - in the Upside Down Forest
     * the foliage comes from ANCHOR_TREE_BRANCH, scattered as a separate feature alongside it. A sapling
     * has no biome to do that composing, so {@link #growFoliage} does it for a grown tree.
     */
    private static final AnchorTreeBranchFeature FOLIAGE = new AnchorTreeBranchFeature();

    /**
     * The generator depth the anchor tree is shaped for. A grown tree pins only its scale to this, never
     * {@code MAX_HEIGHT} itself, which {@link #cylinder} also uses as an absolute Y ceiling.
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
        if (CityStructure.buildingsWithin(world, pos, CITY_CLEARANCE)) return false;
        if (NetherBiomeBuilder.useLegacyGeneration) {
            legacyStructure.generate(world, pos, random, MAX_HEIGHT, context);
            return true;
        }

        BlockPos down = pos.below(BlocksHelper.downRay(world, pos, MAX_HEIGHT));
        if (canGenerate(pos)) {
            // Result deliberately dropped: worldgen keeps its unconditional true.
            grow(world, pos, down, random, MAX_HEIGHT, context, false);
            return true;
        }
        return false;
    }

    /**
     * Grows the tree from a 2x2 of saplings a player planted on the floor, upward to whatever ceiling is
     * above it (or the build limit).
     * <p>
     * {@code pos} is the floor end here, not the ceiling end {@link #place} works from; the tree's geometry
     * spans {@code down..up} either way. Skips the {@link #toMiddle} snap, which would move the tree up to
     * 15 blocks away from the saplings. Like worldgen it keeps clear of nether city buildings.
     */
    @Override
    public boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            NoneFeatureConfiguration configuration
    ) {
        if (CityStructure.buildingsWithin(level, pos, CITY_CLEARANCE)) return false;
        // MAX_HEIGHT doubles as the absolute Y cap of the trunk. The generator depth alone (128 in the
        // Nether) would drop every block of a tree planted above the Nether roof while still using up the
        // saplings, so it is raised to the build limit where that is higher.
        final int maxHeight = Math.max(
                level.getLevel().getChunkSource().getGenerator().getGenDepth(),
                level.getMaxBuildHeight()
        );
        final BlockPos up = pos.above(BlocksHelper.upRay(level, pos, level.getMaxBuildHeight() - pos.getY()));
        return grow(
                level, up, pos, random, maxHeight,
                NetherThreadDataStorage.generatorForThread().context,
                true
        );
    }

    /**
     * Hangs leafy branch clusters off the upper trunk, so a grown tree looks like the ones in the Upside
     * Down Forest rather than a bare stem. Anchored to the trunk rather than to the ceiling, which a
     * player may have built barely wider than the saplings.
     */
    private void growFoliage(
            ServerLevelAccessor level,
            BlockPos up,
            BlockPos down,
            RandomSource random,
            StructureGeneratorThreadContext context
    ) {
        // Snapshot first: FOLIAGE.grow clears the shared per-thread context.
        final int span = up.getY() - down.getY();
        final int minY = up.getY() - Math.max(6, span * 2 / 3);
        final List<BlockPos> canopy = new ArrayList<>();
        for (BlockPos b : context.BLOCKS) {
            if (b.getY() >= minY && b.getY() <= up.getY()) canopy.add(b);
        }
        if (canopy.isEmpty()) return;
        canopy.sort((a, b) -> Integer.compare(b.getY(), a.getY()));

        // Roughly one cluster per 8 blocks of trunk, spread over the trunk's height by band.
        final int count = Mth.clamp(span / 8, 4, 48);
        for (int i = 0; i < count; i++) {
            final int from = canopy.size() * i / count;
            final int to = Math.max(from + 1, canopy.size() * (i + 1) / count);
            final BlockPos on = canopy.get(from + random.nextInt(to - from));

            // Start the cluster at the first air block next to the trunk, so its branches touch it.
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
        // A grown tree is always built at the scale the anchor tree is designed for.
        final float scale_factor = airOnly ? 1.0f : MAX_HEIGHT / (float) SHAPED_FOR_DEPTH;
        int HEIGHT_64;
        int HEIGHT_45;
        int HEIGHT_90;
        final int SEGMENT_LENGTH;
        if (NetherBiomeBuilder.useLegacyGeneration && !airOnly) {
            HEIGHT_64 = MAX_HEIGHT / 2;
            HEIGHT_45 = (int) (MAX_HEIGHT * 0.36);
            HEIGHT_90 = (int) (MAX_HEIGHT * 0.7);
            SEGMENT_LENGTH = (int) (15 * scale_factor);
        } else {
            HEIGHT_64 = (int) (MAX_HEIGHT / 4.0 + MHelper.nextFloat(random, 32));
            HEIGHT_45 = (int) (20 + MHelper.nextFloat(random, 20 * scale_factor));
            HEIGHT_90 = (int) (MAX_HEIGHT / 2.0 + MHelper.nextFloat(random, 15 * scale_factor));
            SEGMENT_LENGTH = (int) ((15 + MHelper.nextFloat(random, 5 * scale_factor)) * scale_factor);
        }
        if (up.getY() - down.getY() < 30) return false;

        // The mushroom and moss bands are absolute Y values tuned for a tree spanning a 128 block Nether.
        // A grown tree spans whatever room the player gave it, so map them onto its own span.
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
            if (block == Blocks.NETHER_BRICKS || block == NetherBlocks.NETHER_BRICK_TILE_LARGE || block == NetherBlocks.NETHER_BRICK_TILE_SMALL)
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
        final int minBuildHeight = level.getMinBuildHeight() + 1;
        // A grown tree is confined to the space it was grown in, from the floor it stands on to the ceiling
        // above: air-only alone would let the geometry skip a floor and carry on in the cave below.
        final net.minecraft.world.level.levelgen.structure.BoundingBox blockBox = airOnly
                ? BlocksHelper.decorationBounds(level, up, down.getY(), up.getY())
                : BlocksHelper.decorationBounds(level, up, minBuildHeight, MAX_HEIGHT - 2);
        // The canopy is the lucis mushrooms; a shorter grown trunk gets a proportionally higher chance.
        final int mushroomChance = airOnly
                ? Math.max(2, (int) (32 * scale_factor * (up.getY() - down.getY()) / (float) MAX_HEIGHT))
                : (int) (32 * scale_factor);
        for (BlockPos bpos : context.BLOCKS) {
            if (!blockBox.isInside(bpos)) continue;
            state = level.getBlockState(bpos);
            // Air-only for a grown tree, so it never eats what the player built around it.
            if (airOnly ? !state.isAir() : (!BlocksHelper.isNetherGround(state) && !state.canBeReplaced()))
                continue;
            boolean blockUp = true;
            if ((blockUp = context.BLOCKS.contains(bpos.above())) && context.BLOCKS.contains(bpos.below()))
                BlocksHelper.setWithoutUpdate(level, bpos, NetherBlocks.MAT_ANCHOR_TREE.getLog().defaultBlockState());
            else
                BlocksHelper.setWithoutUpdate(level, bpos, NetherBlocks.MAT_ANCHOR_TREE.getBark().defaultBlockState());

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
                if (!blockUp && (airOnly ? above.isAir() : above.canBeReplaced())) {
                    if (airOnly) {
                        // bpos.above() need not be inside the box, which on a grown tree ends at the ceiling.
                        BlocksHelper.setWithoutUpdate(
                                level,
                                bpos.above(),
                                NetherBlocks.MOSS_COVER.defaultBlockState(),
                                blockBox
                        );
                    } else {
                        BlocksHelper.setWithoutUpdate(level, bpos.above(), NetherBlocks.MOSS_COVER.defaultBlockState());
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

        // Grow path only: the trunk is finished and context.BLOCKS still holds it.
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
                cylinder(lerpCos(a, b, y, (y - a.getY()) / max), radius, level.getMinBuildHeight(), MAX_HEIGHT, context);
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

    /**
     * Collects one horizontal disc of trunk. The floor cut-off is relative to {@code minY}: in a dimension
     * whose floor is below zero an absolute cut-off at Y 2 dropped every disc below Y 3, so a tree grown
     * there used up the saplings and placed nothing.
     */
    private void cylinder(
            BlockPos pos,
            double radius,
            final int minY,
            final int MAX_HEIGHT,
            StructureGeneratorThreadContext context
    ) {
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
                ) * 0.25 + 0.75) && pos.getY() > minY + 2 && pos.getY() < MAX_HEIGHT - 2)
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

            if (findSurface && end.getY() > level.getMinBuildHeight() + 2 && end.getY() < MAX_HEIGHT - 2) {
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
            boolean north = world.getBlockState(p.north()).getBlock() != NetherBlocks.GIANT_LUCIS;
            boolean south = world.getBlockState(p.south()).getBlock() != NetherBlocks.GIANT_LUCIS;
            boolean east = world.getBlockState(p.east()).getBlock() != NetherBlocks.GIANT_LUCIS;
            boolean west = world.getBlockState(p.west()).getBlock() != NetherBlocks.GIANT_LUCIS;
            BlockState state = NetherBlocks.GIANT_LUCIS.defaultBlockState();
            BlocksHelper.setWithoutUpdate(world, p, state
                    .setValue(HugeMushroomBlock.NORTH, north)
                    .setValue(HugeMushroomBlock.SOUTH, south)
                    .setValue(HugeMushroomBlock.EAST, east)
                    .setValue(HugeMushroomBlock.WEST, west));
        }
    }
}
