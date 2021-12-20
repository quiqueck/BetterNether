package paulevs.betternether.world.structures.plants;

import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.world.structures.piece.CustomPiece;
import paulevs.betternether.world.structures.piece.StructureTypes;
import ru.bclib.interfaces.SurfaceProvider;
import ru.bclib.util.NbtUtil;
import ru.bclib.util.SplineHelper;
import ru.bclib.world.structures.BCLStructureFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


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
		final int topHeight = surfaceProvider.bclGetUpwardBaseHeight(pos.getX(), pos.getZ(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, pos.getY(), MAX_HEIGHT-5)-2;
		final int distance = topHeight-bottomHeight;
		if (topHeight>MAX_HEIGHT-5 || bottomHeight<5 || distance<40) return Optional.empty();

		final float searchRadius =(float)(Math.log(distance/3)*7);
		int touchPointCount = random.nextInt(3, 7);
		List<List<Vector3f>> topAnchors = new ArrayList<>(touchPointCount*2);

		List<Vector3f> trunk = SplineHelper.makeSpline(pos.getX(), bottomHeight, pos.getZ(), pos.getX(), topHeight, pos.getZ(), distance / 8);
		trunk.forEach(v-> v.add(random.nextFloat(-2, 2), 0, random.nextFloat(-2, 2)));
		topAnchors.add(trunk);

		int trunkSegments = trunk.size()-1;
		int maxSgmnt =-1;
		for (int i=0; i<touchPointCount; i++) {
			int sgmnt = random.nextInt((int)(trunkSegments*0.7), (int)(trunkSegments*0.9));
			maxSgmnt = Math.max(maxSgmnt, sgmnt);

			float radius = (random.nextFloat() * 0.25f + 0.75f)*searchRadius;
			float angle = random.nextFloat()*360;
			float x = (float)Math.cos(angle) * radius + pos.getX();
			float z = (float)Math.sin(angle) * radius + pos.getZ();

			Vector3f start = trunk.get(sgmnt);
			int branchHeight = surfaceProvider.bclGetUpwardBaseHeight((int)start.x(), (int)start.z(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, (int)start.y(), MAX_HEIGHT-5);
			Vector3f end = new Vector3f(x, branchHeight, z);


			List<Vector3f> branch = SplineHelper.makeSpline(start.x(), start.y(), start.z(), end.x(), end.y(), end.z(), (branchHeight-(int)start.y()) / 8);
			branch.forEach(v-> v.add(random.nextFloat(-2, 2), 0, random.nextFloat(-2, 2)));

			topAnchors.add(branch);
		}

		int count = 5;
		for (int i=1; i<topAnchors.size(); i++){
			if (count<0 || random.nextInt(4)!=0) continue;
			List<Vector3f> branch = topAnchors.get(i);
			if (branch.size()<3) continue;

			int idx = random.nextInt(1, branch.size()-1);
			Vector3f start = branch.get(idx);
			int branchHeight = surfaceProvider.bclGetUpwardBaseHeight((int)start.x(), (int)start.z(), SurfaceProvider.NOT_AIR_OR_FLUID, heightLimitView, (int)start.y(), MAX_HEIGHT-5);
		int length = branchHeight-(int)start.y();

			if (length>9) {
				count--;

				float radius = (random.nextFloat() * 0.25f + 0.75f)*(searchRadius/3);
				float angle = random.nextFloat()*360;
				float x = (float)Math.cos(angle) * radius + start.x();
				float z = (float)Math.sin(angle) * radius + start.z();
				Vector3f end = new Vector3f(x, branchHeight, z);


				List<Vector3f> newBranch = SplineHelper.makeSpline(start.x(), start.y(), start.z(), end.x(), end.y(), end.z(), length / 8);
				newBranch.forEach(v -> v.add(random.nextFloat(-2, 2), 0, random.nextFloat(-2, 2)));
				topAnchors.add(newBranch);
			}
		}

		if (maxSgmnt>=0) {
			while (trunk.size()>maxSgmnt+1) trunk.remove(maxSgmnt+1);
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
		List<List<Vector3f>> branches;
		boolean isOK;
		private static BoundingBox makeBoundingBos(BlockPos p1, BlockPos p2, BlockPos p3, boolean isOK, List<List<Vector3f>> points){
			var bbox = new BoundingBox(p1).encapsulate(p2).encapsulate(p3);
			for (var branch: points) {
				for (Vector3f f : branch) {
					bbox.encapsulate(new BlockPos((int) f.x(), (int) f.y(), (int) f.z()));
				}
			}
			return bbox;
		}

		public AnchorTreePiece(BlockPos p1, BlockPos p2, BlockPos p3, boolean isOK, List<List<Vector3f>> branches) {
			super(StructureTypes.ANCHOR_TREE, id, makeBoundingBos(p1, p2, p3, isOK, branches));
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.branches = branches;
			this.isOK = isOK;
		}
		
		public AnchorTreePiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(StructureTypes.ANCHOR_TREE, tag);
			
			p1 = NbtUtils.readBlockPos(tag.getCompound("p1"));
			p2 = NbtUtils.readBlockPos(tag.getCompound("p2"));
			p3 = NbtUtils.readBlockPos(tag.getCompound("p3"));
			isOK = tag.getBoolean("ok");

			branches = NbtUtil.readListList(tag.getList("points", Tag.TAG_COMPOUND), NbtUtil::readVector3f);
		}


		
		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
			tag.put("p1", NbtUtils.writeBlockPos(p1));
			tag.put("p2", NbtUtils.writeBlockPos(p2));
			tag.put("p3", NbtUtils.writeBlockPos(p3));

			tag.put("points", NbtUtil.buildListList(branches, NbtUtil::writeVector3f));

			tag.putBoolean("ok", isOK);
		}

		static int colorIdx = 0;
		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
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
			generateAirBox(level, bb, bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());

		    //BlocksHelper.setWithoutUpdate(worldGenLevel, p1, (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p1.north(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p1.south(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p1.east(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p1.west(), (isOK? Blocks.WHITE_CONCRETE:Blocks.BLACK_CONCRETE).defaultBlockState());
			
			//BlocksHelper.setWithoutUpdate(worldGenLevel, p2, (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p2.north(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p2.south(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p2.east(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p2.west(), (isOK? Blocks.BLUE_CONCRETE:Blocks.LIGHT_BLUE_CONCRETE).defaultBlockState());

			//BlocksHelper.setWithoutUpdate(worldGenLevel, p2, (isOK? Blocks.BLUE_CONCRETE:Blocks.YELLOW_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p3.north(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p3.south(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p3.east(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());
			BlocksHelper.setWithoutUpdate(level, p3.west(), (isOK? Blocks.GREEN_CONCRETE:Blocks.LIME_CONCRETE).defaultBlockState());

BlockState[] colors = {Blocks.BLUE_CONCRETE.defaultBlockState(),
			Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
					Blocks.CYAN_CONCRETE.defaultBlockState(),
					Blocks.GREEN_CONCRETE.defaultBlockState(),
					Blocks.LIME_CONCRETE.defaultBlockState(),
					Blocks.YELLOW_CONCRETE.defaultBlockState(),
					Blocks.ORANGE_CONCRETE.defaultBlockState(),
					Blocks.RED_CONCRETE.defaultBlockState(),
					Blocks.PINK_CONCRETE.defaultBlockState(),
					Blocks.PURPLE_CONCRETE.defaultBlockState(),
					Blocks.MAGENTA_CONCRETE.defaultBlockState(),
					Blocks.BLACK_CONCRETE.defaultBlockState()};
//			var spline = SplineHelper.makeSpline(p2.getX(), p2.getY(), p2.getZ(), p3.getX(), p3.getY(), p3.getZ(), 5);
//			for (var v : spline) {
//				v.add(random.nextFloat()*6-3, 0, random.nextFloat()*6-3);
//			}
//			SplineHelper.smoothSpline(spline, 10);
//			SplineHelper.fillSpline(spline, worldGenLevel, Blocks.PINK_CONCRETE.defaultBlockState(), new BlockPos(0,0,0), (state)->true);

			BlockPos ZERO = new BlockPos(0,0,0);
			for (int i=0; i<branches.size(); i++) {
				colorIdx++;
				List<Vector3f> branch = branches.get(i);
				if (branch.size()>0) {
					Vector3f start = branch.get(0);
					placeBlock(level, colors[i % colors.length], (int) start.x(), (int) start.y(), (int) start.z(), bb);
					for (int j = 1; j < branch.size(); j++) {
						Vector3f end = branch.get(j);
						placeBlock(level, colors[i % colors.length], (int) end.x(), (int) end.y(), (int) end.z(), bb);
						SplineHelper.fillLine(start, end, level, colors[i % colors.length], ZERO, (state)->true);
						start = end;
					}
				}
			}
		}
	}
}
