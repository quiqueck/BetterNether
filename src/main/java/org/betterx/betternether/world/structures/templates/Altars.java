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

public class Altars extends RandomNbtStructure {
    public static final Codec<Altars> CODEC = simpleRandomCodec(Altars::new);

    protected Altars(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public Altars(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE, false, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("altar_01"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_02"), -4), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_03"), -3), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_04"), -3), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_05"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_06"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_07"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("altar_08"), -2), 1.0
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.ALTARS.type();
    }
}
