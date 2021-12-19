package paulevs.betternether.world.structures.piece;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import paulevs.betternether.BetterNether;
import paulevs.betternether.world.structures.plants.AnchorTreeFeature.AnchorTreePiece;

public class StructureTypes {
	public static final StructurePieceType NETHER_CITY = register(CityPiece::new, "bncity");
	public static final StructurePieceType ANCHOR_TREE = register(AnchorTreePiece::new, "anchor_tree_piece");
	public static final StructurePieceType CAVE = register(CavePiece::new, "bncave");
	public static final StructurePieceType DESTRUCTION = register(DestructionPiece::new, "bndestr");

	public static void init() {}

	protected static StructurePieceType register(StructurePieceType pieceType, String id) {
		return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(BetterNether.MOD_ID, id), pieceType);
	}
}
