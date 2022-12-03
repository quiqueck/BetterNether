package org.betterx.datagen.betternether.worldgen;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeRegistry;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeData;
import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.WorldsTogether;
import org.betterx.worlds.together.surfaceRules.AssignedSurfaceRule;
import org.betterx.worlds.together.surfaceRules.SurfaceRuleRegistry;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import com.google.gson.JsonElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BCLibRegistriesDataProvider implements DataProvider {
    public static final List<RegistryDataLoader.RegistryData<?>> REGISTRIES = List.of(
            new RegistryDataLoader.RegistryData<>(BCLBiomeRegistry.BCL_BIOMES_REGISTRY, BiomeData.CODEC),
            new RegistryDataLoader.RegistryData<>(
                    SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                    AssignedSurfaceRule.CODEC
            ),
            new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE, Structure.DIRECT_CODEC)

    );


    private final PackOutput output;

    public BCLibRegistriesDataProvider(FabricDataOutput generator) {
        this.output = generator;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        HolderLookup.Provider registryAccess = VanillaRegistries.createLookup();
        RegistryOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        for (RegistryDataLoader.RegistryData<?> registryData : REGISTRIES) {
            futures.add(this.dumpRegistryCapFuture(
                    cachedOutput,
                    registryAccess,
                    dynamicOps,
                    (RegistryDataLoader.RegistryData) registryData
            ));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private <T> CompletableFuture dumpRegistryCapFuture(
            CachedOutput cachedOutput,
            HolderLookup.Provider registryAccess,
            DynamicOps<JsonElement> dynamicOps,
            RegistryDataLoader.RegistryData<T> registryData
    ) {
        return CompletableFuture.runAsync(() -> dumpRegistryCap(
                cachedOutput,
                registryAccess,
                dynamicOps,
                registryData
        ));
    }

    private <T> void dumpRegistryCap(
            CachedOutput cachedOutput,
            HolderLookup.Provider registryAccess,
            DynamicOps<JsonElement> dynamicOps,
            RegistryDataLoader.RegistryData<T> registryData
    ) {
        ResourceKey<? extends Registry<T>> resourceKey = registryData.key();

        HolderLookup.RegistryLookup<T> registry = registryAccess.lookupOrThrow(resourceKey);
        PackOutput.PathProvider pathProvider = this.output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                resourceKey.location().getPath()
        );
        registry.listElementIds().filter(k -> k.location().getNamespace().equals(BetterNether.MOD_ID)).forEach(entry ->
                dumpValue(
                        pathProvider.json(entry.location()),
                        cachedOutput,
                        dynamicOps,
                        registryData.elementCodec(),
                        registry.get(entry).orElseThrow().value()
                )
        );

    }

    private static <E> void dumpValue(
            Path path,
            CachedOutput cachedOutput,
            DynamicOps<JsonElement> dynamicOps,
            Encoder<E> encoder,
            E object
    ) {

        Optional<JsonElement> optional = encoder.encodeStart(dynamicOps, object)
                                                .resultOrPartial(string -> WorldsTogether.LOGGER.error(
                                                        "Couldn't serialize element {}: {}",
                                                        path,
                                                        string
                                                ));
        if (optional.isPresent()) {
            DataProvider.saveStable(cachedOutput, optional.get(), path);
        }

    }

    @Override
    public String getName() {
        return "BCL Registries";
    }
}
