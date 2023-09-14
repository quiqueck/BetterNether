package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.registry.NetherStructures;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverStructureProvider;
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
        NetherStructures.CITY_STRUCTURE
                .bootstrap(context)
                .register();


//        public static final BCLStructure<Pyramids> PYRAMIDS = BCLStructureBuilder
//                .start(BetterNether.C.id("pyramid"), Pyramids::new)
//                .randomPlacement(32, 8)
//                .codec(Pyramids.CODEC)
//                .build();
//
//        public static final BCLStructure<GhastHive> GHAST_HIVE = BCLStructureBuilder
//                .start(BetterNether.C.id("ghast_hive"), GhastHive::new)
//                .randomPlacement(80, 32)
//                .codec(GhastHive.CODEC)
//                .build();
//
//
//        public static final BCLStructure<SpawnAltarLadder> SPAWN_ALTAR_LADDER = BCLStructureBuilder
//                .start(BetterNether.C.id("spawn_altar_ladder"), SpawnAltarLadder::new)
//                .randomPlacement(40, 20)
//                .codec(SpawnAltarLadder.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//        public static final BCLStructure<RespawnPoints> RESPAWN_POINTS = BCLStructureBuilder
//                .start(BetterNether.C.id("respawn_points"), RespawnPoints::new)
//                .randomPlacement(32, 24)
//                .codec(RespawnPoints.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//
//        public static final BCLStructure<Pillars> PILLARS = BCLStructureBuilder
//                .start(BetterNether.C.id("pillars"), Pillars::new)
//                .randomPlacement(20, 8)
//                .codec(Pillars.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//
//        public static final BCLStructure<Gardens> GARDENS = BCLStructureBuilder
//                .start(BetterNether.C.id("gardens"), Gardens::new)
//                .randomPlacement(50, 16)
//                .codec(Gardens.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//        public static final BCLStructure<Portals> PORTALS = BCLStructureBuilder
//                .start(BetterNether.C.id("portals"), Portals::new)
//                .randomPlacement(40, 15)
//                .codec(Portals.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//        public static final BCLStructure<Altars> ALTARS = BCLStructureBuilder
//                .start(BetterNether.C.id("altars"), Altars::new)
//                .randomPlacement(40, 16)
//                .codec(Altars.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//        public static final BCLStructure<JungleTemples> JUNGLE_TEMPLES = BCLStructureBuilder
//                .start(BetterNether.C.id("jungle_temples"), JungleTemples::new)
//                .randomPlacement(32, 8)
//                .codec(JungleTemples.CODEC)
//                .biomeTag(NetherTags.BETTER_NETHER_DECORATIONS)
//                .build();
//
//
//        public static void register() {
//            NetherStructurePieces.ensureStaticLoad();
//
//            TagManager.BIOMES.add(CITY_STRUCTURE.biomeTag, BiomeAPI.NETHER_WASTES_BIOME.getBiomeKey());
//            if (Configs.GENERATOR.getBoolean("generator.world.cities", "overworld", false)) {
//                BiomeAPI.registerOverworldBiomeModification((biomeID, biome) -> {
//                    if (!biomeID.getNamespace().equals(BetterNether.C.modId)) {
//                        addNonBNBiomeTags(biomeID, biome);
//                    }
//                });
//            }
//        }
//
//        public static void addNonBNBiomeTags(ResourceLocation biomeID, Holder<Biome> biome) {
//            if (biomeID != null && !biomeID.equals(BiomeAPI.BASALT_DELTAS_BIOME.getID()) && !biomeID.equals(Biomes.THE_VOID.location())) {
//                TagManager.BIOMES.add(CITY_STRUCTURE.biomeTag, biome.value());
//            }
//        }
    }

    @Override
    protected void bootstrapSets(BootstapContext<StructureSet> context) {
        NetherStructures.CITY_STRUCTURE_SET
                .bootstrap(context)
                .addStructure(NetherStructures.CITY_STRUCTURE)
                .randomPlacement()
                .spacing(CITY_SPACING)
                .separation(CITY_SPACING >> 1)
                .setPlacement()
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
