package org.betterx.betternether.world.structures.city;

import org.betterx.betternether.blocks.BlockBNPot;
import org.betterx.betternether.blocks.BlockPottedPlant;
import org.betterx.betternether.blocks.BlockSmallLantern;
import org.betterx.betternether.world.structures.city.palette.CityPalette;
import org.betterx.betternether.world.structures.city.palette.Palettes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * Rewrites the blocks of a placed city building so that they match the {@link CityPalette} rolled
 * for it.
 * <p>
 * Minecraft 26.2 changes reflected here:
 * <ul>
 * <li>{@code StructureProcessor} became an <b>interface</b>, so this class implements it.</li>
 * <li>{@code getType()} is gone; a processor now returns its {@link MapCodec} directly from
 * {@code codec()} instead of wrapping it in a {@code KeyDispatchDataCodec}. Up to 26.1 this returned
 * {@code StructureProcessorType.NOP}, which was only ever a placeholder - this processor is built
 * directly in Java by {@code CityPiece}, is never serialized, and is not registered in
 * {@code BuiltInRegistries.STRUCTURE_PROCESSOR}. A real codec over the palette name is supplied
 * instead, mirroring how {@code CityPiece} already persists its palette; it still cannot round-trip
 * through JSON until someone registers the type.</li>
 * <li>{@code processBlock}'s fourth parameter is no longer the original {@code StructureBlockInfo}
 * but only its {@code BlockPos} - see {@link #processBlock}.</li>
 * </ul>
 */
public class BuildingStructureProcessor implements StructureProcessor {
    // Decoding an unknown palette name yields Palettes.EMPTY rather than an error, because
    // Palettes.getPalette falls back silently. Harmless today - this processor is built in Java by
    // CityPiece and never decoded - but if it is ever registered in
    // BuiltInRegistries.STRUCTURE_PROCESSOR, a typo in a datapack would produce an unpalettized
    // building instead of a failed load. Swap the xmap for a flatXmap returning DataResult.error
    // at that point.
    public static final MapCodec<BuildingStructureProcessor> CODEC = Codec
            .STRING
            .fieldOf("palette")
            .xmap(
                    name -> new BuildingStructureProcessor(Palettes.getPalette(name)),
                    processor -> processor.palette.getName()
            );

    protected final CityPalette palette;

    public BuildingStructureProcessor(CityPalette palette) {
        this.palette = palette;
    }

    private StructureBlockInfo setState(BlockState state, StructureBlockInfo info) {
        return new StructureBlockInfo(info.pos(), state, info.nbt());
    }

    /**
     * 26.2 replaced the original {@code StructureBlockInfo} parameter with just its position, so the
     * original {@code BlockState} is no longer handed to processors. Both uses of it are preserved:
     * <ul>
     * <li>the state is now read from {@code structureBlockInfo2}. {@code StructureTemplate
     * .processBlockInfos} seeds that with {@code new StructureBlockInfo(worldPos, original.state(),
     * original.nbt().copy())} and only overwrites it with the result of the <i>previous</i>
     * processor - and {@code StructureCityBuilding.placeInChunk} installs this processor as the only
     * one - so for every block it sees {@code structureBlockInfo2.state()} is the original state.
     * Verified against the 26.1 and 26.2 bytecode of {@code processBlockInfos}.</li>
     * <li>the template-local position used for the {@code isCollisionShapeFullBlock} probe below is
     * exactly what 26.2 now passes as {@code originalPos}.</li>
     * </ul>
     * If a second processor is ever chained ahead of this one the first bullet stops holding, and
     * this processor would start palettizing that processor's output rather than the template's.
     */
    @Override
    public StructureBlockInfo processBlock(
            LevelReader worldView,
            BlockPos pos,
            BlockPos blockPos,
            BlockPos originalPos,
            StructureBlockInfo structureBlockInfo2,
            StructurePlaceSettings structurePlacementData
    ) {
        BlockState state = structureBlockInfo2.state();

        if (state.isAir())
            return structureBlockInfo2;

        Block block = state.getBlock();
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        if (name.startsWith("roof_tile")) {
            if (block instanceof StairBlock) {
                return setState(palette.getRoofStair(state), structureBlockInfo2);
            } else if (block instanceof SlabBlock) {
                return setState(palette.getRoofSlab(state), structureBlockInfo2);
            }
            return setState(palette.getRoofBlock(state), structureBlockInfo2);
        } else if (name.contains("nether") && name.contains("brick")) {
            if (block instanceof StairBlock) {
                return setState(palette.getFoundationStair(state), structureBlockInfo2);
            } else if (block instanceof SlabBlock) {
                return setState(palette.getFoundationSlab(state), structureBlockInfo2);
            } else if (block instanceof WallBlock) {
                return setState(palette.getFoundationWall(state), structureBlockInfo2);
            }
            return setState(palette.getFoundationBlock(state), structureBlockInfo2);
        } else if (name.contains("plank") || name.contains("reed") || state.is(BlockTags.PLANKS)) {
            if (block instanceof StairBlock) {
                return setState(palette.getPlanksStair(state), structureBlockInfo2);
            } else if (block instanceof SlabBlock) {
                return setState(palette.getPlanksSlab(state), structureBlockInfo2);
            }
            return setState(palette.getPlanksBlock(state), structureBlockInfo2);
        } else if (name.contains("glass") || name.contains("frame")) {
            if (block instanceof IronBarsBlock)
                return setState(palette.getGlassPane(state), structureBlockInfo2);
            return setState(palette.getGlassBlock(state), structureBlockInfo2);
        } else if (block instanceof RotatedPillarBlock) {
            if (name.contains("log")) {
                return setState(palette.getLog(state), structureBlockInfo2);
            }
            return setState(palette.getBark(state), structureBlockInfo2);
        } else if (block instanceof StairBlock) {
            return setState(palette.getStoneStair(state), structureBlockInfo2);
        } else if (block instanceof SlabBlock) {
            return setState(palette.getStoneSlab(state), structureBlockInfo2);
        } else if (block instanceof WallBlock) {
            return setState(palette.getWall(state), structureBlockInfo2);
        } else if (block instanceof FenceBlock) {
            return setState(palette.getFence(state), structureBlockInfo2);
        } else if (block instanceof FenceGateBlock) {
            return setState(palette.getGate(state), structureBlockInfo2);
        } else if (block instanceof DoorBlock) {
            return setState(palette.getDoor(state), structureBlockInfo2);
        } else if (block instanceof TrapDoorBlock) {
            return setState(palette.getTrapdoor(state), structureBlockInfo2);
        } else if (block instanceof PressurePlateBlock) {
            if (block.getSoundType(state) == SoundType.WOOD)
                return setState(palette.getWoodenPlate(state), structureBlockInfo2);
            else
                return setState(palette.getStonePlate(state), structureBlockInfo2);
        } else if (block instanceof BlockSmallLantern) {
            if (state.getValue(BlockSmallLantern.FACING) == Direction.UP)
                return setState(palette.getCeilingLight(state), structureBlockInfo2);
            else if (state.getValue(BlockSmallLantern.FACING) != Direction.DOWN)
                return setState(palette.getWallLight(state), structureBlockInfo2);
            else
                return setState(palette.getFloorLight(state), structureBlockInfo2);
        } else if (block instanceof BlockBNPot) {
            return setState(palette.getPot(state), structureBlockInfo2);
        } else if (block instanceof BlockPottedPlant) {
            return setState(palette.getPlant(state), structureBlockInfo2);
        } else if (block instanceof StructureBlock) {
            return setState(Blocks.AIR.defaultBlockState(), structureBlockInfo2);
        } else if (!name.contains("nether") && !name.contains("mycelium") && state.isCollisionShapeFullBlock(
                worldView,
                originalPos
        ) && state.canOcclude() && !(state.getBlock() instanceof BaseEntityBlock)) {
            if (state.getLightEmission() > 0)
                return setState(palette.getGlowingBlock(state), structureBlockInfo2);
            return setState(palette.getStoneBlock(state), structureBlockInfo2);
        }

        return structureBlockInfo2;
    }

    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
