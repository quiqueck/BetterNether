package paulevs.betternether.world.structures.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier.Context;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.Nullable;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.world.structures.city.BuildingStructureProcessor;
import paulevs.betternether.world.structures.city.CityFeature;
import paulevs.betternether.world.structures.city.StructureCityBuilding;
import paulevs.betternether.world.structures.city.palette.CityPalette;
import paulevs.betternether.world.structures.piece.CustomPiece;
import paulevs.betternether.world.structures.piece.StructureTypes;
import ru.bclib.interfaces.SurfaceProvider;
import ru.bclib.mixin.common.NoiseBasedChunkGeneratorMixin;
import ru.bclib.world.structures.BCLStructureFeature;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class AnchorTreeFeature extends NoiseAffectingStructureFeature<NoneFeatureConfiguration> {
	public AnchorTreeFeature() {
		super(
			NoneFeatureConfiguration.CODEC,
			AnchorTreeFeature::pieceGeneratorSupplier
		);
	}
	
	private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
		if (!BCLStructureFeature.isValidBiome(context, 60))
			return Optional.empty();
		
		final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		random.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		
		final int MAX_HEIGHT = context.chunkGenerator().getGenDepth();
		final LevelHeightAccessor heightLimitView = context.heightAccessor();
		final ChunkGenerator chunkGenerator = context.chunkGenerator();
		if (!(chunkGenerator instanceof NoiseBasedChunkGenerator)) return Optional.empty();
		final SurfaceProvider surfaceProvider = (SurfaceProvider) chunkGenerator;
		
		
		BlockPos pos = context.chunkPos().getWorldPosition();
		int landHeight = surfaceProvider.bclGetUpwardBaseHeight(pos.getX(), pos.getZ(), SurfaceProvider.IS_AIR_OR_FLUID, heightLimitView);
		NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(pos.getX(), pos.getZ(), heightLimitView);
		BlockState topBlock = columnOfBlocks.getBlock(landHeight);
		boolean isOk = topBlock.getFluidState().isEmpty();
		
		return Optional.of((structurePiecesBuilder, context2) -> {
			structurePiecesBuilder.addPiece(new AnchorTreePiece(pos, new BlockPos(pos.getX(), landHeight, pos.getZ()), isOk));
		});
	}
	
	public static class AnchorTreePiece extends CustomPiece{
		final static int id = 0;
		BlockPos p1;
		BlockPos p2;
		boolean isOK;
		public AnchorTreePiece(BlockPos p1, BlockPos p2, boolean isOK) {
			super(StructureTypes.ANCHOR_TREE, id, new BoundingBox(p1).encapsulate(p2));
			this.p1 = p1;
			this.p2 = p2;
			this.isOK = isOK;
		}
		
		public AnchorTreePiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(StructureTypes.ANCHOR_TREE, tag);
			
			p1 = NbtUtils.readBlockPos(tag.getCompound("p1"));
			p2 = NbtUtils.readBlockPos(tag.getCompound("p2"));
			isOK = tag.getBoolean("ok");
		}
		
		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
			tag.put("p1", NbtUtils.writeBlockPos(p1));
			tag.put("p2", NbtUtils.writeBlockPos(p2));
			tag.putBoolean("ok", isOK);
		}
		
		@Override
		public void postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
		    //BlocksHelper.setWithoutUpdate(worldGenLevel, p1, (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.north(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.south(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.east(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.west(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			
			//BlocksHelper.setWithoutUpdate(worldGenLevel, p2, (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.north(), (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.south(), (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.east(), (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.west(), (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
		}
	}
}
