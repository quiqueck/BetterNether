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

public class Pyramids extends RandomNbtStructure {
    public static final MapCodec<Pyramids> CODEC = RandomNbtStructure.simpleRandomCodec(Pyramids::new);

    protected Pyramids(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public Pyramids(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.LAVA, true, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("lava/pyramid_1"), 0), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("lava/pyramid_2"), 0), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("lava/pyramid_3"), 0), 0.6,
                        new RandomNbtStructureElement(BetterNether.C.id("lava/pyramid_4"), 0), 0.3
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.PYRAMIDS.type();
    }
}
