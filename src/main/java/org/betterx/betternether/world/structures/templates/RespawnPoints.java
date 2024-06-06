package org.betterx.betternether.world.structures.templates;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.wover.structure.api.structures.StructurePlacement;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructureElement;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.NotNull;

public class RespawnPoints extends RandomNbtStructure {
    public static final MapCodec<RespawnPoints> CODEC = RandomNbtStructure.simpleRandomCodec(RespawnPoints::new);

    protected RespawnPoints(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public RespawnPoints(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE, false, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("respawn_point_01"), -3), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("respawn_point_02"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("respawn_point_03"), -3), 0.6,
                        new RandomNbtStructureElement(BetterNether.C.id("respawn_point_04"), -2), 0.3
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.RESPAWN_POINTS.type();
    }
}
