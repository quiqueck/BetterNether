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

public class JungleTemples extends RandomNbtStructure {
    public static final Codec<JungleTemples> CODEC = RandomNbtStructure.simpleRandomCodec(JungleTemples::new);

    protected JungleTemples(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public JungleTemples(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE_FLAT_4, true, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("ruined_temple"), -4), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("jungle_temple_altar"), -2), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("jungle_temple_2"), -2), 1.0
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.JUNGLE_TEMPLES.type();
    }
}
