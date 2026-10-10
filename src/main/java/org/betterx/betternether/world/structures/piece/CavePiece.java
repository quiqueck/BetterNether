package org.betterx.betternether.world.structures.piece;

import org.betterx.betternether.BlocksHelper;
import org.betterx.betternether.noise.OpenSimplexNoise;
import org.betterx.betternether.registry.NetherStructurePieces;
import org.betterx.betternether.world.structures.city.StructureAvoidingProcessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class CavePiece extends CustomPiece {
    private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(927649);

    private final BlockPos center;
    private final int radius;
    private final int radSqr;

    public CavePiece(BlockPos center, int radius, RandomSource random, BoundingBox blockBox) {
        super(NetherStructurePieces.CAVE_PIECE, random.nextInt(), makeBoundingBox(center, radius));
        this.center = center.immutable();
        this.radius = radius;
        this.radSqr = radius * radius;
    }

    public CavePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(NetherStructurePieces.CAVE_PIECE, tag);
        this.center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        this.radius = tag.getIntOr("radius", 0);
        this.radSqr = radius * radius;
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext structurePieceSerializationContext,
            CompoundTag tag
    ) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putInt("radius", radius);
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager arg,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        // The cave runs after a bastion or fortress it overlaps (the city generates last), so carving
        // freely hollowed out the complex - and the buildings, which yield to those pieces through
        // StructureAvoidingProcessor, then left gaps around a structure that was no longer there.
        // Measured on one city/bastion overlap: 63k bastion cells turned to cave_air, ~10% of the
        // city's building blocks missing. Leave the other structure's boxes exactly as it built them -
        // that covers every structure in #betternether:city_avoids (fortress and bastion by default).
        final StructureAvoidingProcessor avoiding = new StructureAvoidingProcessor(
                StructureAvoidingProcessor.collect(world, arg, blockBox)
        );
        MutableBlockPos POS = new MutableBlockPos();
        BlockState bottom = LAVA;
        if (!(world.dimensionType().hasCeiling())) {
            bottom = Blocks.NETHERRACK.defaultBlockState();
        }
        for (int x = blockBox.minX(); x <= blockBox.maxX(); x++) {
            int px = x - center.getX();
            px *= px;
            for (int z = blockBox.minZ(); z <= blockBox.maxZ(); z++) {
                int pz = z - center.getZ();
                pz *= pz;
                for (int y = blockBox.minY(); y <= blockBox.maxY(); y++) {
                    int py = (y - center.getY()) << 1;
                    py *= py;
                    if (px + py + pz <= radSqr + NOISE.eval(x * 0.1, y * 0.1, z * 0.1) * 800) {
                        POS.set(x, y, z);
                        if (avoiding.claimed(POS)) continue;
                        if (y > 31) {
                            BlocksHelper.setWithoutUpdate(world, POS, CAVE_AIR);
                            //world.setBlock(POS, CAVE_AIR, 0);
                        } else {
                            BlocksHelper.setWithoutUpdate(world, POS, bottom);
                            //world.setBlock(POS, bottom, 0);
                        }
                    }
                }
            }
        }
    }

    private static BoundingBox makeBoundingBox(BlockPos center, int radius) {
        final int x1 = center.getX() - radius;
        final int x2 = center.getX() + radius;
        final int minY = Math.max(22, center.getY() - radius);
        final int maxY = Math.min(96, center.getY() + radius);
        final int z1 = center.getZ() - radius;
        final int z2 = center.getZ() + radius;
        return new BoundingBox(x1, minY, z1, x2, maxY, z2);
    }
}
