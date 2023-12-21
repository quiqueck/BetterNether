package org.betterx.betternether.world.biomes.util;

import org.betterx.bclib.api.v2.levelgen.structures.BCLStructure;
import org.betterx.bclib.api.v3.levelgen.features.BCLFeature;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherEntities;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.betternether.registry.features.placed.NetherVegetationPlaced;
import org.betterx.betternether.world.NetherBiome;
import org.betterx.betternether.world.NetherBiomeConfig;
import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import org.jetbrains.annotations.NotNull;

public class NetherBiomeBuilder extends WoverBiomeBuilder.AbstractWoverBiomeBuilder<NetherBiomeBuilder> {

    protected NetherBiomeBuilder(
            BiomeBootstrapContext context,
            BiomeKey<NetherBiomeBuilder> key
    ) {
        super(context, key);

        this.tag(NetherTags.BETTER_NETHER)
            .temperature(BiomeBuilder.DEFAULT_NETHER_TEMPERATURE)
            .downfall(BiomeBuilder.DEFAULT_NETHER_WETNESS)
            .hasPrecipitation(false)
            .waterColor(BiomeBuilder.DEFAULT_NETHER_WATER_COLOR)
            .waterFogColor(BiomeBuilder.DEFAULT_NETHER_WATER_FOG_COLOR)
            .skyColor(BiomeBuilder.calculateSkyColor(BiomeBuilder.DEFAULT_NETHER_TEMPERATURE))
            .music(SoundEvents.MUSIC_BIOME_NETHER_WASTES)
            .mood(SoundEvents.AMBIENT_NETHER_WASTES_MOOD)
            .loop(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
            .additions(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS)
            .isNetherBiome()

            .feature(NetherVegetationPlaced.WART_CAP);
    }

    public static final SurfaceRules.RuleSource BEDROCK = SurfaceRules.state(Blocks.BEDROCK.defaultBlockState());

    public static final SurfaceRules.VerticalGradientConditionSource BEDROCK_BOTTOM
            = new SurfaceRules.VerticalGradientConditionSource(
            BetterNether.C.id("bedrock_floor"),
            VerticalAnchor.bottom(),
            VerticalAnchor.aboveBottom(5)
    );
    public static final SurfaceRules.VerticalGradientConditionSource BEDROCK_TOP
            = new SurfaceRules.VerticalGradientConditionSource(
            BetterNether.C.id("bedrock_roof"),
            VerticalAnchor.belowTop(5),
            VerticalAnchor.top()
    );

    private void addVanillaStructures() {
        this.carver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE)
            .structure(BiomeTags.HAS_RUINED_PORTAL_NETHER);
    }

    private void addVanillaFeatures() {
        this.feature(GenerationStep.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA)
            .defaultMushrooms()
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA)
            .feature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED);
    }

    private void addBNStructures() {
        this.structure(NetherStructures.PYRAMIDS)
            .structure(NetherStructures.GHAST_HIVE)
            .structure(NetherTags.BETTER_NETHER_DECORATIONS);
    }

    NetherBiomeBuilder configure(NetherBiomeConfig config) {
        final BiomeSurfaceRuleBuilder<NetherBiomeBuilder> surfaceBuilder = this.startSurface();
        config.surface(surfaceBuilder);
        surfaceBuilder.finishSurface();

        ResourceKey<Biome> otherBiome = config.subBiomeOf();
        if (otherBiome != null) this.parent(otherBiome);

        otherBiome = config.withEdgeBiome();
        if (otherBiome != null) this.edge(otherBiome);

        if (config.hasVanillaStructures()) addVanillaStructures();
        if (config.hasBNStructures()) addBNStructures();
        if (config.hasVanillaFeatures()) addVanillaFeatures();
        if (config.hasVanillaOres()) this.netherDefaultOres();

        if (config.hasBNFeatures()) NetherFeatures.addDefaultBNFeatures(this);

        for (NetherEntities.KnownSpawnTypes spawnType : NetherEntities.KnownSpawnTypes.values()) {
            spawnType.addSpawn(this, config);
        }

        NetherFeatures.addDefaultFeatures(this);

        if (config.hasDefaultOres()) NetherFeatures.addDefaultOres(this);
        if (config.hasNetherCity()) this.structure(NetherStructures.CITY_STRUCTURE);


        config.addCustomBuildData(this);
        return this;
    }


    public static <C extends NetherBiomeConfig> NetherBiomeKey<C> createKey(@NotNull String name) {
        return new NetherBiomeKey<>(BetterNether.C.id(name.replace(' ', '_')
                                                          .toLowerCase()));
    }

    @Deprecated(forRemoval = true)
    public NetherBiomeBuilder structure(BCLStructure<?> structure) {
        return this.structure(structure.biomeTag);
    }

    @Deprecated(forRemoval = true)
    public NetherBiomeBuilder feature(BCLFeature<?, ?> feature) {
        return feature(feature.decoration, feature.placedFeature);
    }

    @Override
    public void registerBiomeData(BootstapContext<BiomeData> dataContext) {
        dataContext.register(
                key.dataKey,
                new NetherBiome(
                        fogDensity, key.key, parameters,
                        terrainHeight, genChance, edgeSize, vertical, edge, parent
                )
        );
    }
}


