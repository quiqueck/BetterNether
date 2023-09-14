package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.world.structures.piece.CavePiece;
import org.betterx.betternether.world.structures.piece.CityPiece;
import org.betterx.betternether.world.structures.piece.DestructionPiece;
import org.betterx.wover.structure.api.StructureManager;

import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class NetherStructurePieces {
    public static final StructurePieceType NETHER_CITY_PIECE = StructureManager.registerPiece(
            BetterNether.C.id("bncity"),
            CityPiece::new
    );
    public static final StructurePieceType CAVE_PIECE = StructureManager.registerPiece(
            BetterNether.C.id("bncave"),
            CavePiece::new
    );
    public static final StructurePieceType DESTRUCTION_PIECE = StructureManager.registerPiece(
            BetterNether.C.id("bndestr"),
            DestructionPiece::new
    );
    public static final StructurePieceType ANCHOR_TREE_PIECE = StructureManager.registerPiece(
            BetterNether.C.id("anchor_tree"),
            DestructionPiece::new
    );

    public static void ensureStaticLoad() {

    }
}
