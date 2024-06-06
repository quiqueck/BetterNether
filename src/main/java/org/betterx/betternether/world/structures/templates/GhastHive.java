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

public class GhastHive extends RandomNbtStructure {
    public static final MapCodec<GhastHive> CODEC = RandomNbtStructure.simpleRandomCodec(GhastHive::new);

    protected GhastHive(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public GhastHive(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_CEIL, false, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("ghast_hive"), 0)
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.GHAST_HIVE.type();
    }
}