package org.betterx.betternether.world.structures.templates;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherStructures;
import de.ambertation.wover.structure.api.structures.StructurePlacement;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructureElement;
import de.ambertation.wover.util.RandomizedWeightedList;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.NotNull;

public class Pillars extends RandomNbtStructure {
    public static final MapCodec<Pillars> CODEC = RandomNbtStructure.simpleRandomCodec(Pillars::new);

    protected Pillars(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public Pillars(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE, false, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_01"), -1), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_02"), -1), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_03"), -1), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_04"), -1), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_05"), -1), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("pillar_06"), -1), 1.0
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.PILLARS.type();
    }
}
