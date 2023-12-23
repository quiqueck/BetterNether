package org.betterx.betternether.registry;

import org.betterx.betternether.BN;
import org.betterx.betternether.registry.features.placed.NetherOresPlaced;
import org.betterx.betternether.world.biomes.util.NetherBiomeBuilder;
import org.betterx.betternether.world.features.*;
import org.betterx.betternether.world.structures.city.CityStructure;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.feature.api.FeatureManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NetherFeatures {
    public static final Feature<NoneFeatureConfiguration> JELLYFISH_MUSHROOM = FeatureManager.register(
            BN.id("jellyfish_mushroom"),
            new JellyfishMushroomFeature()
    );
    public static final Feature<NoneFeatureConfiguration> OBSIDIAN_CRYSTAL = FeatureManager.register(
            BN.id("obsidian_crystal"),
            new CrystalFeature()
    );
    public static final Feature<NoneFeatureConfiguration> WART_BUSH = FeatureManager.register(
            BN.id("wart_bush"),
            new WartBushFeature()
    );
    public static final RubeusTreeFeature RUBEUS_TREE = FeatureManager.register(
            BN.id("rubeus_tree"),
            new RubeusTreeFeature()
    );
    public static final MushroomFirFeature MUSHROOM_FIR = FeatureManager.register(
            BN.id("mushroom_fir"),
            new MushroomFirFeature()
    );
    public static final BigBrownMushroomFeature BIG_BROWN_MUSHROOM = FeatureManager.register(
            BN.id("big_brown_mushroom"),
            new BigBrownMushroomFeature()
    );
    public static final Feature<NoneFeatureConfiguration> RUBEUS_BUSH = FeatureManager.register(
            BN.id("rubeus_bush"),
            new RubeusBushFeature()
    );
    public static final Feature<NoneFeatureConfiguration> LUCIS = FeatureManager.register(
            BN.id("lucis"),
            new LucisFeature()
    );
    public static final SoulLilyFeature SOUL_LILY = FeatureManager.register(
            BN.id("soul_lily"),
            new SoulLilyFeature()
    );
    public static final WartTreeFeature WART_TREE = FeatureManager.register(
            BN.id("wart_tree"),
            new WartTreeFeature()
    );
    public static final WillowBushFeature WILLOW_BUSH = FeatureManager.register(
            BN.id("willow_bush"),
            new WillowBushFeature()
    );
    public static final WillowTreeFeature WILLOW_TREE = FeatureManager.register(
            BN.id("willow_tree"),
            new WillowTreeFeature()
    );
    public static final OldWillowTree OLD_WILLOW_TREE = FeatureManager.register(
            BN.id("old_willow_tree"),
            new OldWillowTree()
    );
    public static final NetherSakuraFeature SAKURA_TREE = FeatureManager.register(
            BN.id("sakura_tree"),
            new NetherSakuraFeature()
    );
    public static final NetherSakuraBushFeature SAKURA_BUSH = FeatureManager.register(
            BN.id("sakura_bush"),
            new NetherSakuraBushFeature()
    );
    public static final AnchorTreeBranchFeature ANCHOR_TREE_BRANCH = FeatureManager.register(
            BN.id("anchor_tree_branch"),
            new AnchorTreeBranchFeature()
    );
    public static final AnchorTreeFeature ANCHOR_TREE = FeatureManager.register(
            BN.id("anchor_tree"),
            new AnchorTreeFeature()
    );
    public static final AnchorTreeRootFeature ANCHOR_TREE_ROOT = FeatureManager.register(
            BN.id("anchor_tree_root"),
            new AnchorTreeRootFeature()
    );
    public static final WartCapFeature WART_CAP = FeatureManager.register(
            BN.id("wart_cap"),
            new WartCapFeature()
    );
    public static final TwistedVinesFeature TWISTING_VINES = FeatureManager.register(
            BN.id("twisting_vines"),
            new TwistedVinesFeature()
    );

    // Features that should be added to all Nether Biomes
    public static NetherBiomeBuilder addDefaultFeatures(NetherBiomeBuilder builder) {
        return builder;
    }

    // Features that should be added to all BN Biomes
    public static void addDefaultBNFeatures(NetherBiomeBuilder builder) {

    }

    public static NetherBiomeBuilder addDefaultOres(NetherBiomeBuilder builder) {
        return builder
                .feature(NetherOresPlaced.CINCINNASITE_ORE)
                .feature(NetherOresPlaced.NETHER_RUBY_ORE_RARE)
                .feature(NetherOresPlaced.NETHER_LAPIS_ORE)
                .feature(NetherOresPlaced.NETHER_REDSTONE_ORE);
    }


    public static void register() {
        WorldLifecycle.SERVER_LEVEL_READY.subscribe(NetherFeatures::onWorldLoad);
    }

    private static void onWorldLoad(
            ServerLevel level,
            ResourceKey<Level> levelResourceKey,
            LevelStem levelStem,
            long seed
    ) {
        if (levelResourceKey.equals(Level.NETHER)) {
            CavesFeature.onLoad(seed);
            PathsFeature.onLoad(seed);

            CityStructure.initGenerator();
        }
    }


}
