package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.registry.NetherStructures;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverStructureProvider;
import org.betterx.wover.structure.api.sets.StructureSetManager;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class StructureDataProvider extends WoverStructureProvider {
    public static final int CITY_SPACING = 64;

    /**
     * Creates a new instance of {@link WoverStructureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public StructureDataProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapSturctures(BootstapContext<Structure> context) {
        NetherStructures.CITY_STRUCTURE.bootstrap(context).register();
        NetherStructures.PYRAMIDS.bootstrap(context).register();
        NetherStructures.GHAST_HIVE.bootstrap(context).register();
        NetherStructures.SPAWN_ALTAR_LADDER.bootstrap(context).register();
        NetherStructures.RESPAWN_POINTS.bootstrap(context).register();
        NetherStructures.PILLARS.bootstrap(context).register();
        NetherStructures.GARDENS.bootstrap(context).register();
        NetherStructures.PORTALS.bootstrap(context).register();
        NetherStructures.ALTARS.bootstrap(context).register();
        NetherStructures.JUNGLE_TEMPLES.bootstrap(context).register();
    }

    @Override
    protected void bootstrapSets(BootstapContext<StructureSet> context) {
        StructureSetManager
                .bootstrap(NetherStructures.CITY_STRUCTURE, context)
                .randomPlacement(CITY_SPACING, CITY_SPACING >> 1)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.PYRAMIDS, context)
                .addStructure(NetherStructures.JUNGLE_TEMPLES)
                .randomPlacement(32, 8)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.GHAST_HIVE, context)
                .randomPlacement(80, 32)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.SPAWN_ALTAR_LADDER, context)
                .randomPlacement(40, 20)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.RESPAWN_POINTS, context)
                .randomPlacement(32, 24)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.PILLARS, context)
                .randomPlacement(20, 8)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.GARDENS, context)
                .randomPlacement(50, 16)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.PORTALS, context)
                .randomPlacement(40, 15)
                .register();

        StructureSetManager
                .bootstrap(NetherStructures.ALTARS, context)
                .randomPlacement(40, 16)
                .register();
    }

    @Override
    protected void bootstrapPools(BootstapContext<StructureTemplatePool> context) {

    }

    @Override
    protected void bootstrapProcessors(BootstapContext<StructureProcessorList> context) {

    }

    @Override
    protected void prepareBiomeTags(TagBootstrapContext<Biome> context) {

    }
}
