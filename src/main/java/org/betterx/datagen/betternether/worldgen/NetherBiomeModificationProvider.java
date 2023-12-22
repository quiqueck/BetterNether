package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.features.placed.NetherOresPlaced;
import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;

public class NetherBiomeModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public NetherBiomeModificationProvider(
            ModCore modCore
    ) {
        super(modCore, "Bether Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstapContext<BiomeModification> context) {
        BiomeModification
                .build(context, BetterNether.C.id("overworld_nether_city"))
                .allOf(
                        BiomePredicate.hasConfig(Configs.WORLD.addNetherCityToOverworld, true),
                        BiomePredicate.inDimension(LevelStem.OVERWORLD)
                )
                .addStructureSet(NetherStructures.CITY_STRUCTURE)
                .register();

        BiomeModification
                .build(context, BetterNether.C.id("default_ores"))
                .allOf(
                        BiomePredicate.inDimension(LevelStem.NETHER),
                        BiomePredicate.not(BiomePredicate.inNamespace(BetterNether.C))
                )
                .addFeature(NetherOresPlaced.CINCINNASITE_ORE)
                .addFeature(NetherOresPlaced.NETHER_RUBY_ORE_RARE)
                .addFeature(NetherOresPlaced.NETHER_LAPIS_ORE)
                .addFeature(NetherOresPlaced.NETHER_REDSTONE_ORE)
                .register();

        BiomeModification
                .build(context, BetterNether.C.id("ruby_ore_dense"))
                .inBiomes(Biomes.SOUL_SAND_VALLEY)
                .addFeature(NetherOresPlaced.NETHER_RUBY_ORE_LARGE)
                .register();

        BiomeModification
                .build(context, BetterNether.C.id("ruby_ore"))
                .inBiomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST)
                .addFeature(NetherOresPlaced.NETHER_RUBY_ORE)
                .register();
    }
}
