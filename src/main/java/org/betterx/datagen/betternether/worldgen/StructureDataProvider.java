package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v2.levelgen.structures.BCLStructureBuilder;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class StructureDataProvider {
    public static void bootstrap(BootstapContext<Structure> ctx) {
        BCLStructureBuilder.registerUnbound(ctx);
    }

    public static void bootstrapSets(BootstapContext<StructureSet> ctx) {
        BCLStructureBuilder.registerUnboundSets(ctx);
    }
}
