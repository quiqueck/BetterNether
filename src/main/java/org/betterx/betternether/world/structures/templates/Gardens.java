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

public class Gardens extends RandomNbtStructure {
    public static final MapCodec<Gardens> CODEC = RandomNbtStructure.simpleRandomCodec(Gardens::new);

    protected Gardens(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public Gardens(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE_FLAT_2, true, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("garden_01"), -3), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("garden_02"), -2), 1.0
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.GARDENS.type();
    }
}
