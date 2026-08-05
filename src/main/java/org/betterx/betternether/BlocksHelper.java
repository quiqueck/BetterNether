package org.betterx.betternether;

import org.betterx.betternether.blocks.BlockFarmland;

import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashSet;
import java.util.Iterator;

public class BlocksHelper {
    public static final int FLAG_UPDATE_BLOCK = 1;
    public static final int FLAG_SEND_CLIENT_CHANGES = 2;
    public static final int FLAG_NO_RERENDER = 4;
    public static final int FORSE_RERENDER = 8;
    public static final int FLAG_IGNORE_OBSERVERS = 16;

    public static final int SET_SILENT = /*FLAG_UPDATE_BLOCK |*/ FLAG_IGNORE_OBSERVERS | FLAG_SEND_CLIENT_CHANGES;
    public static final int SET_UPDATE = FLAG_UPDATE_BLOCK | FLAG_SEND_CLIENT_CHANGES;
    public static final Direction[] HORIZONTAL = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };

    private static final Vec3i[] OFFSETS = new Vec3i[]{
            new Vec3i(-1, -1, -1), new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1),
            new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(-1, 0, 1),
            new Vec3i(-1, 1, -1), new Vec3i(-1, 1, 0), new Vec3i(-1, 1, 1),

            new Vec3i(0, -1, -1), new Vec3i(0, -1, 0), new Vec3i(0, -1, 1),
            new Vec3i(0, 0, -1), new Vec3i(0, 0, 0), new Vec3i(0, 0, 1),
            new Vec3i(0, 1, -1), new Vec3i(0, 1, 0), new Vec3i(0, 1, 1),

            new Vec3i(1, -1, -1), new Vec3i(1, -1, 0), new Vec3i(1, -1, 1),
            new Vec3i(1, 0, -1), new Vec3i(1, 0, 0), new Vec3i(1, 0, 1),
            new Vec3i(1, 1, -1), new Vec3i(1, 1, 0), new Vec3i(1, 1, 1)
    };

    public static BoundingBox chunkBounds(LevelAccessor world, BlockPos pos) {
        final int minBuildHeight = world.getMinY() + 1;
        final int maxBuildHeight = world.getMaxY() - 1;
        return chunkBounds(world, pos, minBuildHeight, maxBuildHeight);
    }

    public static BoundingBox chunkBounds(LevelAccessor world, BlockPos pos, int minY, int maxY) {
        final int chunkStartX = (pos.getX() >> 4) << 4;
        final int chunkStartZ = (pos.getZ() >> 4) << 4;

        return new BoundingBox(chunkStartX, minY, chunkStartZ, chunkStartX + 31, maxY, chunkStartZ + 31);
    }

    public static BoundingBox decorationBounds(LevelAccessor world, BlockPos pos) {
        final int minBuildHeight = world.getMinY() + 1;
        final int maxBuildHeight = world.getMaxY() - 1;
        return decorationBounds(world, pos, minBuildHeight, maxBuildHeight);
    }

    /**
     * The horizontal box a feature decorating {@code world} may currently touch, clamped to the given build
     * heights.
     * <p>
     * Delegates to {@link WriteZone#of(LevelAccessor)} rather than assuming a write radius of 1 around
     * {@code pos}' own chunk. Two differences follow, both in the direction of being more correct:
     * <ul>
     *     <li>the box is anchored on the chunk the region is actually decorating, not on whichever chunk a
     *     placement modifier happened to emit {@code pos} into - those are the same chunk in the normal
     *     case, and where they are not, the old box permitted writes the world was silently dropping;</li>
     *     <li>outside a {@code WorldGenRegion} - a sapling grown by a player in a live {@code ServerLevel} -
     *     there is no write restriction at all, so nothing is clipped. The old box cut such a tree off at a
     *     48-block wall for no reason.</li>
     * </ul>
     */
    public static BoundingBox decorationBounds(LevelAccessor world, BlockPos pos, int minY, int maxY) {
        return WriteZone.of(world).toBoundingBox(minY, maxY);
    }

    /**
     * Tests {@code x}/{@code z} against {@code bounds} while ignoring the Y axis.
     * <p>
     * A feature may only touch the 3x3 chunks around the one being decorated, and that limit is purely
     * horizontal - walking up or down inside a legal column never leaves the write zone. Reading past it is
     * not merely untidy: beyond the write radius the region only guarantees {@code structure_starts}, so the
     * neighbouring chunk may still be empty and what the feature decides from that read depends on how far
     * that chunk happened to get. (26.3 logs such reads; this version does not, which makes them quieter
     * rather than less wrong.) Guarding a read with the full {@link BoundingBox#isInside} would also cut it
     * off at {@link #decorationBounds(LevelAccessor, BlockPos) decorationBounds}' build-height clamp, which
     * changes what a feature generates near the ceiling; this rejects only the horizontal escape.
     * <p>
     * Same semantics as {@link WriteZone#contains(int, int)}, kept as a {@link BoundingBox} overload for the
     * call sites that already hold a box.
     */
    public static boolean isInsideHorizontally(BoundingBox bounds, int x, int z) {
        return x >= bounds.minX() && x <= bounds.maxX() && z >= bounds.minZ() && z <= bounds.maxZ();
    }

    /**
     * @see #isInsideHorizontally(BoundingBox, int, int)
     */
    public static boolean isInsideHorizontally(BoundingBox bounds, BlockPos pos) {
        return isInsideHorizontally(bounds, pos.getX(), pos.getZ());
    }

    public static boolean isNetherrack(BlockState state) {
        return state.is(de.ambertation.wover.tag.api.predefined.CommonBlockTags.NETHERRACK);
    }

    public static boolean isSoulSand(BlockState state) {
        return state.is(de.ambertation.wover.tag.api.predefined.CommonBlockTags.SOUL_GROUND);
    }

    /**
     * The ground anything in the nether may stand on or grow out of.
     * <p>
     * It is the runtime counterpart of
     * {@link de.ambertation.wover.tag.api.predefined.CommonBlockTags#NETHER_TERRAIN} and of the traits in
     * {@link org.betterx.betternether.blocks.NetherSurvival#netherGround()}; the three are meant to agree, so
     * a block added to one belongs in all three.
     * <p>
     * {@code SCULK_LIKE} is <b>not</b> among them. It is a material family rather than a terrain one - it
     * holds vanilla sculk and decorative pieces such as the gloomsculk geode - and the gloomwood's floor
     * blocks earn their place here by carrying NETHER_TERRAIN themselves.
     * <p>
     * The gloomwood's own species are the exception: a plant native to that floor merely stands on it, so
     * the gloomwood sapling takes {@link org.betterx.betternether.blocks.NetherSurvival#netherGroundAndSculk()} -
     * this set plus the whole sculk family - and its ground cover takes the wider
     * {@link org.betterx.bclib.trait.block.SurvivesOnSolidTrait}. Anything else that wants the family says so with
     * {@link #isSculkLike} alongside this - see
     * {@link org.betterx.betternether.world.features.GloomwoodTreeFeature}.
     */
    public static boolean isNetherGround(BlockState state) {
        return state.is(de.ambertation.wover.tag.api.predefined.CommonBlockTags.NETHER_STONES)
                || isSoulSand(state)
                || isNetherMycelium(state)
                || isNylium(state);
    }

    public static boolean isSculkLike(BlockState state) {
        return state.is(de.ambertation.wover.tag.api.predefined.CommonBlockTags.SCULK_LIKE);
    }

    public static boolean isNetherGroundMagma(BlockState state) {
        return isNetherGround(state) || state.is(Blocks.MAGMA_BLOCK);
    }

    public static boolean isNetherMycelium(BlockState state) {
        return state.is(de.ambertation.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM);
    }

    public static void setWithUpdate(LevelAccessor world, BlockPos pos, BlockState state, BoundingBox bounds) {
        if (bounds.isInside(pos))
            world.setBlock(pos, state, SET_UPDATE);
    }

    public static void setWithUpdate(LevelAccessor world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state, SET_UPDATE);
    }

    public static void setWithoutUpdate(LevelAccessor world, BlockPos pos, BlockState state, BoundingBox bounds) {
        if (bounds.isInside(pos))
            world.setBlock(pos, state, SET_SILENT);
    }

    public static void setWithoutUpdate(LevelAccessor world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state, SET_SILENT);
    }

    public static int upRay(LevelAccessor world, BlockPos pos, int maxDist) {
        int length = 0;
        for (int j = 1; j < maxDist && (world.isEmptyBlock(pos.above(j))); j++)
            length++;
        return length;
    }


    public static int downRay(LevelAccessor world, BlockPos pos, int maxDist) {
        int length = 0;
        for (int j = 1; j < maxDist && (world.isEmptyBlock(pos.below(j))); j++)
            length++;
        return length;
    }

    public static BlockState rotateHorizontal(BlockState state, Rotation rotation, Property<Direction> facing) {
        return state.setValue(facing, rotation.rotate(state.getValue(facing)));
    }

    public static BlockState mirrorHorizontal(BlockState state, Mirror mirror, Property<Direction> facing) {
        return state.rotate(mirror.getRotation(state.getValue(facing)));
    }

    public static int getLengthDown(ServerLevel world, BlockPos pos, Block block) {
        int count = 1;
        while (world.getBlockState(pos.below(count)).getBlock() == block)
            count++;
        return count;
    }

    public static boolean isFertile(BlockState state) {
        return state.getBlock() instanceof BlockFarmland;
    }

    public static void cover(
            LevelAccessor world,
            BlockPos center,
            Block ground,
            BlockState cover,
            int radius,
            RandomSource random
    ) {
        HashSet<BlockPos> points = new HashSet<BlockPos>();
        HashSet<BlockPos> points2 = new HashSet<BlockPos>();
        if (world.getBlockState(center).getBlock() == ground) {
            points.add(center);
            points2.add(center);
            for (int i = 0; i < radius; i++) {
                Iterator<BlockPos> iterator = points.iterator();
                while (iterator.hasNext()) {
                    BlockPos pos = iterator.next();
                    for (Vec3i offset : OFFSETS) {
                        if (random.nextBoolean()) {
                            BlockPos pos2 = pos.offset(offset);
                            if (random.nextBoolean() && world.getBlockState(pos2)
                                                             .getBlock() == ground && !points.contains(pos2))
                                points2.add(pos2);
                        }
                    }
                }
                points.addAll(points2);
                points2.clear();
            }
            Iterator<BlockPos> iterator = points.iterator();
            while (iterator.hasNext()) {
                BlockPos pos = iterator.next();
                BlocksHelper.setWithoutUpdate(world, pos, cover);
            }
        }
    }

    public static boolean isNylium(BlockState state) {
        return state.is(BlockTags.NYLIUM);
    }

    public static boolean createLogIfFree(
            LevelAccessor world,
            BlockPos pos,
            BlockState anchorBlock,
            Direction[] directions,
            MutableBlockPos mutableBlockPos
    ) {
        boolean hasNeighbor = false;
        for (Direction dir : directions) {
            mutableBlockPos.setWithOffset(pos, dir);
            BlockState currentState = world.getBlockState(mutableBlockPos);
            if (currentState.hasProperty(BlockStateProperties.DISTANCE) || currentState.is(BlockTags.LOGS)) {
                hasNeighbor = true;
                break;
            }
        }

        if (!hasNeighbor) {
            setWithoutUpdate(world, pos.above(), anchorBlock);
            return true;
        }

        return false;
    }
}
