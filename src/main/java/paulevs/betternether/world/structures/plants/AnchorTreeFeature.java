package paulevs.betternether.world.structures.plants;

import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import ru.bclib.util.SplineHelper;
import ru.bclib.world.structures.BCLStructureFeature;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Function;
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
		
		
		BlockPos pos = context.chunkPos().getMiddleBlockPosition(40);
		final int bottomHeight = surfaceProvider.bclGetDownwardBaseHeight(pos.getX(), pos.getZ(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, 5, pos.getY());
		final int topHeight = surfaceProvider.bclGetUpwardBaseHeight(pos.getX(), pos.getZ(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, pos.getY(), MAX_HEIGHT-5);
		final int distance = topHeight-bottomHeight;
		if (topHeight>MAX_HEIGHT-5 || bottomHeight<5 || distance<40) return Optional.empty();

		final int topRootAnchor = 2 * distance / 3 + bottomHeight;
		final float searchRadius =(float)(Math.log(distance/3)*5);
		int touchPointCount = random.nextInt(3, 7);
		List<Vector3f> topAnchors = new ArrayList<>(touchPointCount*2);


		for (int i=0; i<touchPointCount; i++) {
			float radius = (random.nextFloat() * 0.5f + 0.5f)*searchRadius;
			float angle = random.nextFloat()*360;
			float x = (float)Math.cos(angle) * radius + pos.getX();
			float z = (float)Math.sin(angle) * radius + pos.getY();
			BlockPos p = new BlockPos((int)x, topRootAnchor, (int)z);

			topAnchors.add(new Vector3f(x, topRootAnchor, z));
			int branchHeight = surfaceProvider.bclGetUpwardBaseHeight(p.getX(), p.getZ(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, p.getY(), MAX_HEIGHT-5);
			topAnchors.add(new Vector3f(x, branchHeight, z));
		}
		//NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(pos.getX(), pos.getZ(), heightLimitView);
		//BlockState topBlock = columnOfBlocks.getBlock(bottomHeight);
		boolean isOk = true; //topBlock.getFluidState().isEmpty();
		
		return Optional.of((structurePiecesBuilder, context2) -> {
			structurePiecesBuilder.addPiece(new AnchorTreePiece(pos, new BlockPos(pos.getX(), bottomHeight, pos.getZ()), new BlockPos(pos.getX(), topHeight, pos.getZ()), isOk, topAnchors));
		});
	}
	
	public static class AnchorTreePiece extends CustomPiece{
		final static int id = 0;
		BlockPos p1;
		BlockPos p2;
		BlockPos p3;
		List<Vector3f> points;
		boolean isOK;
		private static BoundingBox makeBoundingBos(BlockPos p1, BlockPos p2, BlockPos p3, boolean isOK, List<Vector3f> points){
			var bbox = new BoundingBox(p1).encapsulate(p2).encapsulate(p3);
			for (Vector3f f:points){
				bbox.encapsulate(new BlockPos((int)f.x(), (int)f.y(), (int)f.z()));
			}
			return bbox;
		}

		public AnchorTreePiece(BlockPos p1, BlockPos p2, BlockPos p3, boolean isOK, List<Vector3f> points) {
			super(StructureTypes.ANCHOR_TREE, id, makeBoundingBos(p1, p2, p3, isOK, points));
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.points = points;
			this.isOK = isOK;
		}
		
		public AnchorTreePiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(StructureTypes.ANCHOR_TREE, tag);
			
			p1 = NbtUtils.readBlockPos(tag.getCompound("p1"));
			p2 = NbtUtils.readBlockPos(tag.getCompound("p2"));
			p3 = NbtUtils.readBlockPos(tag.getCompound("p3"));
			isOK = tag.getBoolean("ok");

			points = readList(tag.getList("points", Tag.TAG_COMPOUND), AnchorTreePiece::readVector3f);
		}

		public static <T> ListTag buildList(List<T> list, Function<T, CompoundTag> writer) {
			ListTag listTag = new ListTag();
			for (T el : list){
				listTag.add(writer.apply(el));
			}

			return listTag;
		}

		public static <T> List<T> readList(ListTag list, Function<CompoundTag, T> reader) {
			List<T> l = new ArrayList<>(7);
			list.forEach(t -> l.add(reader.apply((CompoundTag) t)));

			return l;
		}

		public static CompoundTag writeVector3f(Vector3f v3f) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putFloat("X", v3f.x());
			compoundTag.putFloat("Y", v3f.y());
			compoundTag.putFloat("Z", v3f.z());
			return compoundTag;
		}

		public static Vector3f readVector3f(CompoundTag tag) {
			return new Vector3f(
					tag.getFloat("X"),
					tag.getFloat("Y"),
					tag.getFloat("Z")
			);
		}
		
		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
			tag.put("p1", NbtUtils.writeBlockPos(p1));
			tag.put("p2", NbtUtils.writeBlockPos(p2));
			tag.put("p3", NbtUtils.writeBlockPos(p3));

			tag.put("points", buildList(points, AnchorTreePiece::writeVector3f));

			tag.putBoolean("ok", isOK);
		}
		
		@Override
		public void postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
//			for (int y=boundingBox.minY(); y<boundingBox.maxY(); y++){
//				for (int x=-3; x<=3; x++) {
//					for (int z=-3; z<=3; z++) {
//						BlockPos pos = new BlockPos(p1.getX()+x, y, p2.getZ()+z);
//						if (worldGenLevel.getBlockState(pos).is(Blocks.ORANGE_STAINED_GLASS)) continue;
//						if (!worldGenLevel.getBlockState(pos).isAir())
//							BlocksHelper.setWithoutUpdate(worldGenLevel, pos, Blocks.GLASS.defaultBlockState());
//						else
//							BlocksHelper.setWithoutUpdate(worldGenLevel, pos, Blocks.AIR.defaultBlockState());
//					}
//				}
//			}

		    //BlocksHelper.setWithoutUpdate(worldGenLevel, p1, (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.north(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.south(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.east(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p1.west(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			
			//BlocksHelper.setWithoutUpdate(worldGenLevel, p2, (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.north(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.south(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.east(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p2.west(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());

			//BlocksHelper.setWithoutUpdate(worldGenLevel, p2, (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p3.north(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p3.south(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p3.east(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(worldGenLevel, p3.west(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());


//			var spline = SplineHelper.makeSpline(p2.getX(), p2.getY(), p2.getZ(), p3.getX(), p3.getY(), p3.getZ(), 5);
//			for (var v : spline) {
//				v.add(random.nextFloat()*6-3, 0, random.nextFloat()*6-3);
//			}
//			SplineHelper.smoothSpline(spline, 10);
//			SplineHelper.fillSpline(spline, worldGenLevel, Blocks.PINK_CONCRETE.defaultBlockState(), new BlockPos(0,0,0), (state)->true);

			for (Vector3f v : points) {
				BlocksHelper.setWithoutUpdate(worldGenLevel, new BlockPos((int)v.x(), (int)v.y(), (int)v.z()), Blocks.ORANGE_STAINED_GLASS.defaultBlockState());
			}
		}
	}
}
