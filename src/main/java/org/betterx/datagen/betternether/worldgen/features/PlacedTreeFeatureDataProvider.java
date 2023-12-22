package org.betterx.datagen.betternether.worldgen.features;

import org.betterx.bclib.api.v3.levelgen.features.blockpredicates.BlockPredicates;
import org.betterx.betternether.BN;
import org.betterx.betternether.registry.NetherFeatures;
import org.betterx.betternether.registry.features.configured.NetherTrees;
import org.betterx.betternether.registry.features.placed.NetherTreesPlaced;
import org.betterx.betternether.world.features.configs.NaturalTreeConfiguration;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedTreeFeatureDataProvider extends WoverRegistryContentProvider<PlacedFeature> {
    public PlacedTreeFeatureDataProvider(
            ModCore modCore
    ) {
        super(modCore, "Placed Tree Features", Registries.PLACED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<PlacedFeature> ctx) {
        NetherTreesPlaced.CRIMSON_GLOWING_TREE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/crimson_glow_tree_01"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_02"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_03"), 0, 1.0f)
                .add(BN.id("trees/crimson_glow_tree_04"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.CRIMSON_PINE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/crimson_pine_01"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_02"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_03"), -1, 1.0f)
                .add(BN.id("trees/crimson_pine_04"), 0, 1.0f)
                .add(BN.id("trees/crimson_pine_05"), 0, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.RUBEUS_TREE
                .place(ctx)
                .vanillaNetherGround(12)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.MUSHROOM_FIR
                .place(ctx)
                .vanillaNetherGround(14)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.STALAGNATE
                .place(ctx, NetherTrees.STALAGNATE)
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(7)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.GIANT_MOLD
                .place(ctx, NetherTrees.GIANT_MOLD)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .inRandomPatch()
                .tries(30)
                .spreadXZ(6)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(5)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.BIG_RED_MUSHROOM
                .place(ctx)
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.BIG_BROWN_MUSHROOM
                .place(ctx)
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.OLD_RED_MUSHROOM
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/red_mushroom_01"), -1, 1.0f)
                .add(BN.id("trees/red_mushroom_02"), -0, 1.0f)
                .add(BN.id("trees/red_mushroom_03"), -0, 1.0f)
                .add(BN.id("trees/red_mushroom_04"), -3, 1.0f)
                .add(BN.id("trees/red_mushroom_05"), -3, 1.0f)
                .add(BN.id("trees/red_mushroom_06"), -1, 1.0f)
                .add(BN.id("trees/red_mushroom_07"), -4, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(5)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .register();

        NetherTreesPlaced.OLD_BROWN_MUSHROOM
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/brown_mushroom_02"), -3, 1.0f)
                .add(BN.id("trees/brown_mushroom_03"), -2, 1.0f)
                .add(BN.id("trees/brown_mushroom_01"), -2, 1.0f)
                .add(BN.id("trees/brown_mushroom_04"), -1, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(4)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_MYCELIUM)
                .register();

        NetherTreesPlaced.SOUL_LILY
                .inlineConfiguration(ctx)
                .configuration(NetherFeatures.SOUL_LILY)
                .inlinePlace()
                .isEmptyAndOnNetherGround()
                .inRandomPatch()
                .spreadXZ(3)
                .spreadY(3)
                .inlinePlace()
                .vanillaNetherGround(6)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        NetherTreesPlaced.WART_TREE
                .place(ctx)
                .vanillaNetherGround(14)
                .onceEvery(3)
                .isEmptyAndOn(BlockPredicates.ONLY_SOUL_GROUND)
                .register();

        NetherTreesPlaced.WILLOW_TREE
                .place(ctx)
                .vanillaNetherGround(14)
                .onceEvery(3)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.OLD_WILLOW_TREE
                .inlineConfiguration(ctx)
                .configuration(NetherFeatures.OLD_WILLOW_TREE)
                .configuration(NaturalTreeConfiguration.naturalLarge())
                .inlinePlace()
                .vanillaNetherGround(14)
                .onceEvery(16)
                .isEmptyAndOnNetherGround()
                .register();

        NetherTreesPlaced.SAKURA_TREE
                .place(ctx)
                .betterNetherCeiling(5)
                .onceEvery(6)
                .isEmptyAndUnderNetherGround()
                .register();

        NetherTreesPlaced.ANCHOR_TREE
                .place(ctx)
                .betterNetherCeiling(5)
                .isEmptyAndUnderNetherGround()
                .onceEvery(15)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_SPARSE
                .place(ctx)
                .onlyInBiome()
                .count(1)
                .randomHeight4FromFloorCeil()
                .findSolidCeil(5)
                .isEmptyAndUnderNetherGround()
                .onceEvery(7)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_BRANCH
                .place(ctx)
                .betterNetherCeiling(3)
                .isEmptyAndUnderNetherGround()
                .onceEvery(11)
                .register();

        NetherTreesPlaced.ANCHOR_TREE_ROOT
                .place(ctx)
                .betterNetherCeiling(2)
                .onceEvery(5)
                .isEmptyAndUnderNetherGround()
                .register();

        NetherTreesPlaced.BIG_WARPED_TREE
                .inlineConfiguration(ctx)
                .templates()
                .add(BN.id("trees/warped_tree_01"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_02"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_03"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_04"), -1, 1.0f)
                .add(BN.id("trees/warped_tree_05"), -3, 1.0f)
                .inlinePlace()
                .vanillaNetherGround(6)
                .onceEvery(2)
                .isEmptyAndOnNetherGround()
                .register();

    }
}
