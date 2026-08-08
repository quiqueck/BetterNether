package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.registry.NetherStructures;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverStructureProvider;
import de.ambertation.wover.structure.api.sets.StructureSetManager;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class StructureDataProvider extends WoverStructureProvider {
    public static final int CITY_SPACING = 64;
    public static final int MEGA_LAVA_LAKE_SPACING = 24;

    /**
     * Creates a new instance of {@link WoverStructureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public StructureDataProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapSturctures(BootstrapContext<Structure> context) {
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
        NetherStructures.MEGA_LAVA_LAKE.bootstrap(context).register();
    }

    @Override
    protected void bootstrapSets(BootstrapContext<StructureSet> context) {
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

        // Spacing 24 gives one attempt per ~576 chunks, and almost all of those land outside the gloomwood
        // and fail. Read against the other entries here it looks generous, but it is the only structure in
        // the list restricted to a single biome, and the two filters compound: what comes out is roughly
        // one lake per couple of dozen gloomwood forests, which is the "rare, but there" this is meant to
        // be rather than something a player is unlikely ever to meet.
        StructureSetManager
                .bootstrap(NetherStructures.MEGA_LAVA_LAKE, context)
                .randomPlacement(MEGA_LAVA_LAKE_SPACING, MEGA_LAVA_LAKE_SPACING / 3)
                .register();
    }

    @Override
    protected void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {

    }

    @Override
    protected void bootstrapProcessors(BootstrapContext<StructureProcessorList> context) {

    }

    @Override
    protected void prepareBiomeTags(TagBootstrapContext<Biome> context) {
        context.add(NetherStructures.CITY_STRUCTURE.biomeTag(), Biomes.NETHER_WASTES);
    }
}
