package org.betterx.betternether.world.structures.templates;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.wover.structure.api.structures.StructurePlacement;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructureElement;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.NotNull;

public class SpawnAltarLadder extends RandomNbtStructure {
    public static final Codec<SpawnAltarLadder> CODEC = RandomNbtStructure.simpleRandomCodec(SpawnAltarLadder::new);

    protected SpawnAltarLadder(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public SpawnAltarLadder(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE_FLAT_2, false, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("spawn_altar_ladder"), -5)
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.SPAWN_ALTAR_LADDER.type();
    }
}
