package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.features.configured.NetherOres;
import org.betterx.betternether.registry.features.placed.NetherOresPlaced;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class OreFeatureDataProvider extends WoverFeatureProvider {
    public OreFeatureDataProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("ores"));
    }

    @Override
    protected void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        NetherOres.CINCINNASITE_ORE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.CINCINNASITE_ORE)
                .veinSize(8)
                .discardChanceOnAirExposure(0.0f)
                .register();
        NetherOres.NETHER_RUBY_ORE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.NETHER_RUBY_ORE)
                .veinSize(8)
                .discardChanceOnAirExposure((float) 0)
                .register();
        NetherOres.NETHER_RUBY_ORE_SOUL
                .bootstrap(ctx)
                .add(Blocks.SOUL_SOIL, NetherBlocks.NETHER_RUBY_ORE)
                .veinSize(5)
                .discardChanceOnAirExposure(0.1f)
                .register();
        NetherOres.NETHER_RUBY_ORE_LARGE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.NETHER_RUBY_ORE)
                .veinSize(5)
                .discardChanceOnAirExposure(0.1f)
                .register();
        NetherOres.NETHER_RUBY_ORE_RARE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.NETHER_RUBY_ORE)
                .veinSize(12)
                .discardChanceOnAirExposure(0.0f)
                .register();
        NetherOres.NETHER_LAPIS_ORE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.NETHER_LAPIS_ORE)
                .veinSize(4)
                .discardChanceOnAirExposure(0.0f)
                .register();
        NetherOres.NETHER_REDSTONE_ORE
                .bootstrap(ctx)
                .add(Blocks.NETHERRACK, NetherBlocks.NETHER_REDSTONE_ORE)
                .veinSize(16)
                .discardChanceOnAirExposure(0.3f)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstapContext<PlacedFeature> ctx) {
        NetherOresPlaced.CINCINNASITE_ORE
                .place(ctx)
                .count(10)
                .modifier(PlacementUtils.RANGE_10_10).squarePlacement().onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_RUBY_ORE
                .place(ctx)
                .count(3)
                .modifier(HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(32), VerticalAnchor.belowTop(32)))
                .squarePlacement()
                .onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_RUBY_ORE_SOUL
                .place(ctx)
                .count(5)
                .modifier(HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(32), VerticalAnchor.top()))
                .squarePlacement()
                .onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_RUBY_ORE_LARGE
                .place(ctx)
                .count(5)
                .modifier(HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(32), VerticalAnchor.top()))
                .squarePlacement()
                .onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_RUBY_ORE_RARE
                .place(ctx)
                .onceEvery(2)
                .modifier(HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(70), VerticalAnchor.top()))
                .squarePlacement()
                .onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_LAPIS_ORE
                .place(ctx)
                .count(14)
                .modifier(HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(32), VerticalAnchor.belowTop(10)))
                .squarePlacement()
                .onlyInBiome()
                .register();
        NetherOresPlaced.NETHER_REDSTONE_ORE
                .place(ctx)
                .onceEvery(1)
                .modifier(HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(8), VerticalAnchor.aboveBottom(40)))
                .squarePlacement()
                .onlyInBiome()
                .register();
    }
}
