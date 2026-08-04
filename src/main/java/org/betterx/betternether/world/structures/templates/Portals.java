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

public class Portals extends RandomNbtStructure {
    public static final MapCodec<Portals> CODEC = RandomNbtStructure.simpleRandomCodec(Portals::new);

    protected Portals(
            StructureSettings structureSettings,
            StructurePlacement placement,
            boolean keepAir,
            @NotNull RandomizedWeightedList<RandomNbtStructureElement> elements
    ) {
        super(structureSettings, placement, keepAir, elements);
    }

    public Portals(StructureSettings structureSettings) {
        super(structureSettings, StructurePlacement.NETHER_SURFACE_FLAT_2, true, RandomizedWeightedList.of(
                        new RandomNbtStructureElement(BetterNether.C.id("portal_01"), -4), 1.0,
                        new RandomNbtStructureElement(BetterNether.C.id("portal_02"), -3), 1.0
                )
        );
    }

    @Override
    public @NotNull StructureType<?> type() {
        return NetherStructures.PORTALS.type();
    }
}
