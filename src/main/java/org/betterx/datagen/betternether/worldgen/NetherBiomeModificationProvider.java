package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.data.worldgen.BootstapContext;

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
                .hasConfig(Configs.WORLD.addNetherCityToOverworld, true)
                .addStructureSet(NetherStructures.CITY_STRUCTURE)
                .register();
    }
}
