package org.betterx.datagen.betternether.advancements;

import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import org.betterx.betternether.registry.block.NetherLightBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.betternether.registry.item.NetherEquipmentItems;
import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.bclib.api.v3.datagen.AdvancementDataProvider;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.betternether.registry.NetherStructures;
import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;

import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetherAdvancementDataProvider extends AdvancementDataProvider {
    public NetherAdvancementDataProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(List.of(BetterNether.C.modId), output, registryLookup);
    }

    @Override
    @SuppressWarnings("removal")
    protected void bootstrap(HolderLookup.Provider registryLookup) {
        final HolderLookup.RegistryLookup<Biome> biomeLookup = registryLookup.lookupOrThrow(Registries.BIOME);
        final HolderLookup.RegistryLookup<Structure> structureLookup = registryLookup.lookupOrThrow(Registries.STRUCTURE);
        Identifier root = AdvancementManager.Builder
                .create(BetterNether.C.id("root"))
                .startDisplay(NetherLightBlocks.CINCINNASITE_LANTERN)
                .task()
                .hideFromChat()
                .background(Identifier.withDefaultNamespace("gui/advancements/backgrounds/nether"))
                .endDisplay()
                .addCriterion(
                        "welcome",
                        PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())
                )
                .requireOne()
                .build();


        Identifier enterNether = AdvancementManager.Builder
                .create(BetterNether.C.id("enter_nether"))
                .parent(root)
                .startDisplay(NetherPlantBlocks.JUNGLE_MOSS)
                .endDisplay()
                .addCriterion(
                        "entered_nether",
                        ChangeDimensionTrigger
                                .TriggerInstance
                                .changedDimensionTo(Level.NETHER)
                )
                .requireOne()
                .build();

        Identifier blueObsidian = AdvancementManager.Builder
                .create(BetterNether.C.id("blue_obsidian"))
                .parent(root)
                .startDisplay(NetherObsidianBlocks.BLUE_OBSIDIAN)
                .endDisplay()
                .addCriterion("brew_blue", BNCriterion.BREW_BLUE_CRITERION)
                .requireOne()
                .build();

        Identifier obsidianBlocks = AdvancementManager.Builder
                .create(BetterNether.C.id("obsidian_blocks"))
                .parent(blueObsidian)
                .startDisplay(NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                .endDisplay()
                .addInventoryChangedCriterion("made_brick", NetherObsidianBlocks.OBSIDIAN_BRICKS)
                .addInventoryChangedCriterion("made_tile", NetherObsidianBlocks.OBSIDIAN_TILE)
                .addInventoryChangedCriterion("made_small_tile", NetherObsidianBlocks.OBSIDIAN_TILE_SMALL)
                .addInventoryChangedCriterion("made_rods", NetherObsidianBlocks.OBSIDIAN_ROD_TILES)
                .addInventoryChangedCriterion("made_blue_brick", NetherObsidianBlocks.BLUE_OBSIDIAN_BRICKS)
                .addInventoryChangedCriterion("made_blue_tile", NetherObsidianBlocks.BLUE_OBSIDIAN_TILE)
                .addInventoryChangedCriterion("made_small_blue_tile", NetherObsidianBlocks.BLUE_OBSIDIAN_TILE_SMALL)
                .addInventoryChangedCriterion("made_blue_rods", NetherObsidianBlocks.BLUE_OBSIDIAN_ROD_TILES)
                .requireAll()
                .build();

        Identifier makeCrying = AdvancementManager.Builder
                .create(BetterNether.C.id("make_crying"))
                .parent(blueObsidian)
                .startDisplay(NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN)
                .challenge()
                .endDisplay()
                .addCriterion(
                        "made_blue_crying",
                        BNCriterion.CONVERT_BY_LIGHTNING.match(NetherObsidianBlocks.BLUE_CRYING_OBSIDIAN)
                )
                .addCriterion(
                        "made_crying",
                        BNCriterion.CONVERT_BY_LIGHTNING.match(NetherObsidianBlocks.BLUE_WEEPING_OBSIDIAN)
                )
                .addCriterion(
                        "made_blue_weeping",
                        BNCriterion.CONVERT_BY_LIGHTNING.match(Blocks.CRYING_OBSIDIAN)
                )
                .addCriterion(
                        "made_weeping",
                        BNCriterion.CONVERT_BY_LIGHTNING.match(NetherObsidianBlocks.WEEPING_OBSIDIAN)
                )
                .requireAll()
                .rewardXP(500)
                .build();

        Identifier city = AdvancementManager.Builder
                .create(BetterNether.C.id("city"))
                .parent(enterNether)
                .startDisplay(NetherMetalBlocks.CINCINNASITE_CARVED)
                .endDisplay()
                .addAtStructureCriterion("ncity", NetherStructures.CITY_STRUCTURE.getHolder(structureLookup))
                .requireOne()
                .build();


        Identifier rubyOre = AdvancementManager.Builder
                .create(BetterNether.C.id("ruby_ore"))
                .parent(enterNether)
                .startDisplay(NetherResourceItems.NETHER_RUBY)
                .endDisplay()
                .addInventoryChangedCriterion("ruby_ore", NetherResourceItems.NETHER_RUBY)
                .requireOne()
                .build();


        Identifier flames = AdvancementManager.Builder
                .create(BetterNether.C.id("flaming_ruby"))
                .parent(rubyOre)
                .startDisplay(NetherTemplates.FLAMING_RUBY_TEMPLATE)
                .goal()
                .endDisplay()
                .addInventoryChangedCriterion("flaming_ruby", NetherTemplates.FLAMING_RUBY_TEMPLATE)
                .requireAll()
                .build();

        Identifier flamingTools = NetherEquipmentItems.FLAMING_RUBY_SET
                .addToolSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("flaming_tools"))
                        .parent(flames)
                        .startDisplay(NetherEquipmentItems.FLAMING_RUBY_SET.get(ToolSlot.PICKAXE_SLOT))
                        .goal()
                        .endDisplay()
                ).requireAll()
                .build();

        Identifier flamingArmor = NetherEquipmentItems.FLAMING_RUBY_SET
                .addArmorSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("flaming_armor"))
                        .parent(flames)
                        .startDisplay(NetherEquipmentItems.FLAMING_RUBY_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                        .goal()
                        .endDisplay()
                ).requireAll()
                .build();

        Identifier rubyTools = NetherEquipmentItems.NETHER_RUBY_SET
                .addToolSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("ruby_tools"))
                        .parent(rubyOre)
                        .startDisplay(NetherEquipmentItems.NETHER_RUBY_SET.get(ToolSlot.PICKAXE_SLOT))
                        .endDisplay()
                ).requireOne()
                .build();

        Identifier rubyGear = NetherEquipmentItems.NETHER_RUBY_SET
                .addArmorSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("ruby_gear"))
                        .parent(rubyTools)
                        .startDisplay(NetherEquipmentItems.NETHER_RUBY_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                        .endDisplay()
                ).requireAll()
                .build();

        Identifier cincinnasiteOre = AdvancementManager.Builder
                .create(BetterNether.C.id("cincinnasite_ore"))
                .parent(enterNether)
                .startDisplay(NetherResourceItems.CINCINNASITE_INGOT)
                .endDisplay()
                .addInventoryChangedCriterion("cincinnasite_ore", NetherResourceItems.CINCINNASITE_INGOT)
                .requireOne()
                .build();

        Identifier cincinnasiteTools = NetherEquipmentItems.CINCINNASITE_SET
                .addToolSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("cincinnasite_tools"))
                        .parent(cincinnasiteOre)
                        .startDisplay(NetherEquipmentItems.CINCINNASITE_SET.get(ToolSlot.PICKAXE_SLOT))
                        .endDisplay()
                ).requireOne()
                .build();

        Identifier cincinnasiteGear = NetherEquipmentItems.CINCINNASITE_SET
                .addArmorSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("cincinnasite_gear"))
                        .parent(cincinnasiteTools)
                        .startDisplay(NetherEquipmentItems.CINCINNASITE_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                        .endDisplay()
                ).requireAll()
                .build();

        Identifier cincinnasiteDiamond = AdvancementManager.Builder
                .create(BetterNether.C.id("cincinnasite_diamond"))
                .parent(cincinnasiteTools)
                .startDisplay(NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE)
                .goal()
                .endDisplay()
                .addInventoryChangedCriterion("cincinnasite_diamond", NetherTemplates.CINCINNASITE_DIAMOND_TEMPLATE)
                .requireAll()
                .build();

        Identifier cincinnasiteDiamondTools = NetherEquipmentItems.CINCINNASITE_DIAMOND_SET
                .addToolSetCriterion(AdvancementManager.Builder
                        .create(BetterNether.C.id("cincinnasite_diamond_tools"))
                        .parent(cincinnasiteDiamond)
                        .startDisplay(NetherEquipmentItems.CINCINNASITE_DIAMOND_SET.get(ToolSlot.PICKAXE_SLOT))
                        .goal()
                        .endDisplay()
                ).requireAll()
                .build();

        Identifier forge = AdvancementManager.Builder
                .create(BetterNether.C.id("cincinnasite_forge"))
                .parent(cincinnasiteOre)
                .startDisplay(NetherFunctionalBlocks.CINCINNASITE_FORGE)
                .goal()
                .endDisplay()
                .addCriterion("use_forge", BNCriterion.USED_FORGE_ANY_CRITERION)
                .requireOne()
                .build();

        Identifier netherWood = AdvancementManager.Builder
                .create(BetterNether.C.id("nether_wood"))
                .parent(enterNether)
                .startDisplay(NetherWoodBlocks.MAT_WILLOW.getLog())
                .endDisplay()
                .addWoodCriterion(NetherWoodBlocks.MAT_WILLOW)
                .addWoodCriterion(NetherWoodBlocks.MAT_STALAGNATE)
                .addWoodCriterion(NetherWoodBlocks.MAT_RUBEUS)
                .addWoodCriterion(NetherWoodBlocks.MAT_WART)
                .addWoodCriterion(NetherWoodBlocks.MAT_MUSHROOM_FIR)
                .addWoodCriterion(NetherWoodBlocks.MAT_ANCHOR_TREE)
                .addWoodCriterion(NetherWoodBlocks.MAT_NETHER_SAKURA)
                .addInventoryChangedAnyCriterion(
                        "got_nether_reed",
                        NetherWoodBlocks.MAT_REED.getStem(),
                        NetherWoodBlocks.MAT_REED.getPlanks()
                )
                .addInventoryChangedAnyCriterion(
                        "got_nether_mushroom",
                        NetherWoodBlocks.MAT_NETHER_MUSHROOM.getStem(),
                        NetherWoodBlocks.MAT_NETHER_MUSHROOM.getPlanks()
                )
                .addInventoryChangedAnyCriterion(
                        "got_crimson",
                        Blocks.CRIMSON_STEM,
                        Blocks.CRIMSON_HYPHAE,
                        Blocks.CRIMSON_PLANKS
                )
                .addInventoryChangedAnyCriterion(
                        "got_warped",
                        Blocks.WARPED_STEM,
                        Blocks.WARPED_HYPHAE,
                        Blocks.WARPED_PLANKS
                )
                .requireAll()
                .build();

        Identifier disturbedWisp = AdvancementManager.Builder
                .create(BetterNether.C.id("disturbed_wisp"))
                .parent(enterNether)
                .startDisplay(NetherVineBlocks.GLOOMWISP_VINE)
                .endDisplay()
                .addCriterion("disturbed_wisp", BNCriterion.DISTURBED_WISP_CRITERION)
                .requireOne()
                .build();

        Identifier wispExperience = AdvancementManager.Builder
                .create(BetterNether.C.id("wisp_shed_experience"))
                .parent(disturbedWisp)
                .startDisplay(Items.EXPERIENCE_BOTTLE)
                .endDisplay()
                .addCriterion("wisp_shed_experience", BNCriterion.WISP_SHED_EXPERIENCE_CRITERION)
                .requireOne()
                .build();

        Identifier burnedCrystal = AdvancementManager.Builder
                .create(BetterNether.C.id("burned_gloomsculk_crystal"))
                .parent(disturbedWisp)
                .startDisplay(NetherTerrainBlocks.GLOOMSCULK_GEODE_CRYSTAL)
                .goal()
                .endDisplay()
                .addCriterion("burned_crystal", BNCriterion.BURNED_GLOOMSCULK_CRYSTAL_CRITERION)
                .requireOne()
                .build();


        final var biomes = biomeLookup
                .listElementIds()
                .filter(id -> id.identifier().getNamespace().equals(BetterNether.C.modId))
                .toList();

        if (!biomes.isEmpty()) {
            Identifier allTheBiomes = AdvancementManager.Builder
                    .create(BetterNether.C.id("all_the_biomes"))
                    .parent(city)
                    .startDisplay(NetherEquipmentItems.NETHER_RUBY_SET.get(ArmorSlot.BOOTS_SLOT))
                    .challenge()
                    .endDisplay()
                    .addVisitBiomesCriterion(biomes
                            .stream()
                            .sorted(Comparator.comparing(ResourceKey::identifier))
                            .map(key -> (Holder<Biome>) biomeLookup.get(key).orElseThrow())
                            .toList()
                    )
                    .requireAll()
                    .rewardXP(1500)
                    .build();
        }
    }
}
