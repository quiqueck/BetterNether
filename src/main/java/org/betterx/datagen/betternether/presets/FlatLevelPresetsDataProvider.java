package org.betterx.datagen.betternether.presets;

import org.betterx.betternether.BetterNether;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.preset.api.flat.FlatLevelPresetManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

import java.util.Collections;

public class FlatLevelPresetsDataProvider extends WoverRegistryContentProvider<FlatLevelGeneratorPreset> {
    public static final ResourceKey<FlatLevelGeneratorPreset> BN_FLAT = FlatLevelPresetManager.createKey(
            BetterNether.C.id("flat")
    );

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public FlatLevelPresetsDataProvider(
            ModCore modCore
    ) {
        super(modCore, "Flat Level Presets", Registries.FLAT_LEVEL_GENERATOR_PRESET);
    }

    public void bootstrap(BootstapContext<FlatLevelGeneratorPreset> ctx) {
        FlatLevelPresetManager
                .register(
                        ctx, BN_FLAT,
                        Blocks.NETHERRACK,
                        Biomes.NETHER_WASTES,
                        Collections.emptySet(),
                        false, false,
                        new FlatLayerInfo(63, Blocks.NETHERRACK),
                        new FlatLayerInfo(1, Blocks.BEDROCK)
                );
    }
}
