package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.complex.NetherWoodenMaterial;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.test.api.gametest.FurnitureSweep;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Mirrors BetterEnd's {@code FurnitureParityGameTest} - covers storage/workstation slots, barrel/
 * composter villager POI, and sittable furniture across every {@code NetherWoodenMaterial} generically.
 * <p>
 * The cincinnasite (metal) chair/barstool/taburet trio is tested separately and explicitly: it uses
 * {@code BaseChair.Wood}/{@code BaseBarStool.Wood}/{@code BaseTaburet.Wood} directly rather than going
 * through the generic per-material {@code addFurniture} path (see the comment in
 * {@code NetherFurnitureBlocks.java:75-81} - a retired dispatch helper always resolved to the Wood
 * variant for these three, and the fix kept using it explicitly rather than fixing the dispatch), so it
 * is not covered by sweeping {@code NetherWoodenMaterial} instances at all.
 */
public class FurnitureParityGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    // gloomwood/gloomwood_dark (NetherWoodBlocks.MAT_GLOOMWOOD/MAT_GLOOMWOOD_DARK) do not exist on this
    // branch yet - that wood material was added after 1.21.6. Dropped from this sweep; every other
    // material below is unaffected.
    private static final Map<String, NetherWoodenMaterial<?>> MATERIALS = Map.of(
            "rubeus", NetherWoodBlocks.MAT_RUBEUS,
            "mushroom_fir", NetherWoodBlocks.MAT_MUSHROOM_FIR,
            "nether_mushroom", NetherWoodBlocks.MAT_NETHER_MUSHROOM,
            "anchor_tree", NetherWoodBlocks.MAT_ANCHOR_TREE,
            "nether_sakura", NetherWoodBlocks.MAT_NETHER_SAKURA
    );

    @GameTest
    public void everyMaterialHasWorkingStorageAndWorkstationSlots(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            // SlotType.SHELF is a vanilla 1.21.9-style ShelfBlock slot - that block type does not exist
            // on this (1.21.6-1.21.8) branch at all, so it's dropped from this check here; every other
            // slot below is unaffected.
            for (SlotType slot : List.of(
                    SlotType.CHEST, SlotType.BARREL, SlotType.COMPOSTER,
                    SlotType.CRAFTING_TABLE, SlotType.BOOKSHELF
            )) {
                if (material.getBlock(slot) == null) {
                    failures.add(name + ": missing a " + slot + " block");
                }
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Storage/workstation slot regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void barrelIsAFishermanPoiAndComposterIsAFarmerPoi(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            checkPoi(material.getBlock(SlotType.BARREL), PoiTypes.FISHERMAN, name + " barrel", failures);
            checkPoi(material.getBlock(SlotType.COMPOSTER), PoiTypes.FARMER, name + " composter", failures);
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Villager POI regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static void checkPoi(Block block, ResourceKey<PoiType> expectedType, String label, List<String> failures) {
        if (block == null) return;
        final BlockState state = block.defaultBlockState();
        final var poiType = PoiTypes.forState(state);
        if (poiType.isEmpty()) {
            failures.add(label + " is not registered as any villager POI type");
            return;
        }
        if (!poiType.get().is(expectedType)) {
            failures.add(label + " is a POI, but not " + expectedType.location());
        }
    }

    @GameTest
    public void chairBarstoolAndTaburetCanBeSatOn(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            checkSittable(helper, material.getBlock(SlotType.CHAIR), name + " chair", failures);
            checkSittable(helper, material.getBlock(SlotType.BAR_STOOL), name + " bar stool", failures);
            checkSittable(helper, material.getBlock(SlotType.TABURET), name + " taburet", failures);
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Sittable-furniture regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void cincinnasiteFurnitureTrioCanBeSatOn(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        checkSittable(helper, NetherFurnitureBlocks.CHAIR_CINCINNASITE, "cincinnasite chair", failures);
        checkSittable(helper, NetherFurnitureBlocks.BAR_STOOL_CINCINNASITE, "cincinnasite bar stool", failures);
        checkSittable(helper, NetherFurnitureBlocks.TABURET_CINCINNASITE, "cincinnasite taburet", failures);

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Cincinnasite furniture regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static void checkSittable(GameTestHelper helper, Block block, String label, List<String> failures) {
        if (block == null) {
            failures.add(label + " does not exist");
            return;
        }
        helper.setBlock(POS, block);
        final String failure = FurnitureSweep.assertPlayerCanSit(helper, POS, label);
        if (failure != null) {
            failures.add(failure);
        }
    }
}
