package org.betterx.betternether.registry;

import org.betterx.bclib.api.v2.LifeCycleAPI;
import org.betterx.bclib.api.v2.levelgen.structures.StructurePlacementType;
import org.betterx.bclib.api.v2.levelgen.structures.StructureWorldNBT;
import org.betterx.bclib.api.v3.levelgen.features.BCLFeature;
import org.betterx.bclib.api.v3.levelgen.features.config.TemplateFeatureConfig;
import org.betterx.betternether.BN;
import org.betterx.betternether.registry.features.placed.NetherOresPlaced;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.betternether.world.features.*;
import org.betterx.betternether.world.structures.city.CityStructure;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NetherFeatures {
    public static final Feature<NoneFeatureConfiguration> JELLYFISH_MUSHROOM = BCLFeature.register(
            BN.id("jellyfish_mushroom"),
            new JellyfishMushroomFeature()
    );
    public static final Feature<NoneFeatureConfiguration> OBSIDIAN_CRYSTAL = BCLFeature.register(
            BN.id("obsidian_crystal"),
            new CrystalFeature()
    );
    public static final Feature<NoneFeatureConfiguration> WART_BUSH = BCLFeature.register(
            BN.id("wart_bush"),
            new WartBushFeature()
    );
    public static final RubeusTreeFeature RUBEUS_TREE = BCLFeature.register(
            BN.id("rubeus_tree"),
            new RubeusTreeFeature()
    );
    public static final MushroomFirFeature MUSHROOM_FIR = BCLFeature.register(
            BN.id("mushroom_fir"),
            new MushroomFirFeature()
    );
    public static final BigBrownMushroomFeature BIG_BROWN_MUSHROOM = BCLFeature.register(
            BN.id("big_brown_mushroom"),
            new BigBrownMushroomFeature()
    );
    public static final Feature<NoneFeatureConfiguration> RUBEUS_BUSH = BCLFeature.register(
            BN.id("rubeus_bush"),
            new RubeusBushFeature()
    );
    public static final Feature<NoneFeatureConfiguration> LUCIS = BCLFeature.register(
            BN.id("lucis"),
            new LucisFeature()
    );
    public static final SoulLilyFeature SOUL_LILY = BCLFeature.register(
            BN.id("soul_lily"),
            new SoulLilyFeature()
    );
    public static final WartTreeFeature WART_TREE = BCLFeature.register(
            BN.id("wart_tree"),
            new WartTreeFeature()
    );
    public static final WillowBushFeature WILLOW_BUSH = BCLFeature.register(
            BN.id("willow_bush"),
            new WillowBushFeature()
    );
    public static final WillowTreeFeature WILLOW_TREE = BCLFeature.register(
            BN.id("willow_tree"),
            new WillowTreeFeature()
    );
    public static final OldWillowTree OLD_WILLOW_TREE = BCLFeature.register(
            BN.id("old_willow_tree"),
            new OldWillowTree()
    );
    public static final NetherSakuraFeature SAKURA_TREE = BCLFeature.register(
            BN.id("sakura_tree"),
            new NetherSakuraFeature()
    );
    public static final NetherSakuraBushFeature SAKURA_BUSH = BCLFeature.register(
            BN.id("sakura_bush"),
            new NetherSakuraBushFeature()
    );
    public static final AnchorTreeBranchFeature ANCHOR_TREE_BRANCH = BCLFeature.register(
            BN.id("anchor_tree_branch"),
            new AnchorTreeBranchFeature()
    );
    public static final AnchorTreeFeature ANCHOR_TREE = BCLFeature.register(
            BN.id("anchor_tree"),
            new AnchorTreeFeature()
    );
    public static final AnchorTreeRootFeature ANCHOR_TREE_ROOT = BCLFeature.register(
            BN.id("anchor_tree_root"),
            new AnchorTreeRootFeature()
    );
    public static final WartCapFeature WART_CAP = BCLFeature.register(
            BN.id("wart_cap"),
            new WartCapFeature()
    );
    public static final TwistedVinesFeature TWISTING_VINES = BCLFeature.register(
            BN.id("twisting_vines"),
            new TwistedVinesFeature()
    );

    // MANAGE DEFAULT FEATURES //
    public static StructureWorldNBT cfg(
            ResourceLocation location,
            int offsetY,
            StructurePlacementType type,
            float chance
    ) {
        return TemplateFeatureConfig.cfg(location, offsetY, type, chance);
    }


    public static NetherBiomeBuilder addDefaultFeatures(NetherBiomeBuilder builder) {
        return builder;
    }

    public static void addDefaultBNFeatures(NetherBiomeBuilder builder) {

    }

    public static NetherBiomeBuilder addDefaultOres(NetherBiomeBuilder builder) {
        return builder
                .feature(NetherOresPlaced.CINCINNASITE_ORE)
                .feature(NetherOresPlaced.NETHER_RUBY_ORE_RARE)
                .feature(NetherOresPlaced.NETHER_LAPIS_ORE)
                .feature(NetherOresPlaced.NETHER_REDSTONE_ORE);
    }

    public static void modifyNonBNBiome(ResourceLocation biomeID, Holder<Biome> biome) {

    }

    public static void register() {
        LifeCycleAPI.onLevelLoad(NetherFeatures::onWorldLoad);
    }

    public static void onWorldLoad(ServerLevel level, long seed, Registry<Biome> registry) {
        CavesFeature.onLoad(seed);
        PathsFeature.onLoad(seed);

        CityStructure.initGenerator();
    }

}
