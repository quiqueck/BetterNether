package org.betterx.datagen.betternether.presets;

import org.betterx.bclib.api.v3.datagen.FlatLevelPresetHelper;
import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.flatLevel.FlatLevelPresets;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

import java.util.Collections;

public class FlatLevelPresetsDataProvider {
    public static final ResourceKey<FlatLevelGeneratorPreset> BN_FLAT = FlatLevelPresets.register(
            BetterNether.makeID("flat")
    );

    public static void bootstrap(BootstapContext<FlatLevelGeneratorPreset> ctx) {
        FlatLevelPresetHelper
                .register(
                        ctx, BN_FLAT,
                        Blocks.NETHERRACK,
                        Biomes.NETHER_WASTES,
                        Collections.emptySet(),
                        false, false,
                        new FlatLayerInfo(63, Blocks.NETHERRACK),
                        new FlatLayerInfo(1, Blocks.BEDROCK)
                );
        //needs to get called in order for the static variables to be initialized
    }
}
