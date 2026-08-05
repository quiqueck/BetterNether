package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import org.betterx.bclib.furniture.block.BaseBarStool;
import org.betterx.bclib.furniture.block.BaseChair;
import org.betterx.bclib.furniture.block.BaseTaburet;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.*;
import org.betterx.betternether.blocks.complex.*;
import org.betterx.betternether.blocks.complex.slots.VanillaNetherWood;
import org.betterx.betternether.blocks.complex.slots.VanillaWood;
import org.betterx.betternether.registry.features.configured.NetherVines;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.betterx.betternether.registry.NetherBlocks;

public class NetherFunctionalBlocks {

    // BlockCincinnasitePedestal (WP6.12): noOcclusion() moved out of its constructor (R1); it always
    // dropped itself unconditionally via BlockBase's inherited getDrops() override (no loot table json was
    // generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    public static final Block CINCINNASITE_PEDESTAL = NetherBlocks.defineBlock("cincinnasite_pedestal", BlockCincinnasitePedestal::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // Decorations //
    // BlockStatueRespawner (WP6.12): lightLevel(15)/noOcclusion() moved out of its constructor (R1); it
    // called setDropItself(false), i.e. it falls through to the vanilla loot-table-driven getDrops().
    // (The earlier claim here that no loot table json existed for it was wrong - a hand-authored
    // loot_table/blocks/pig_statue_respawner.json did exist; it is now NetherLoot.pigStatueRespawner().)
    public static final Block PIG_STATUE_RESPAWNER = NetherBlocks.defineBlock("pig_statue_respawner", BlockStatueRespawner::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.metal())
            .lightLevel(state -> 15)
            .noOcclusion()
            .addTrait(NetherLoot.pigStatueRespawner())
            .buildAndRegister();
    // Craft Stations //
    // Former registerFurnace bundle, inlined at the registration site (WP5.11): BlockNetherFurnace dropped
    // its BehaviourStone marker, so the pickaxe tool tag is restored via NetherMaterial.stone() (tag only,
    // no forced property); the plain self-drop loot (no survives_explosion) it used to get through the
    // retired DropSelfLootProvider interface is now NetherLoot.dropSelfNoExplosion(); the "make a furnace
    // out of source in a ring" recipe is reproduced via furnaceRecipe(source), the mod-local RECIPE-trait
    // wrapper around RecipeBuilder.Templates.makeRoundRecipe (no BCLib RecipeTraits equivalent exists for
    // this shape). No composite exists for this bundle (WP5.11 leaves it fully inline).
    public static final Block BLACKSTONE_FURNACE = NetherBlocks.defineBlock("blackstone_furnace", BlockNetherFurnace::new)
            .replacePropertiesWithCopy(Blocks.BLACKSTONE)
            .addTags(CommonPoiTags.ARMORER_WORKSTATION)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.furnaceRecipe(Blocks.BLACKSTONE))
            .buildAndRegister();
    public static final Block BASALT_FURNACE = NetherBlocks.defineBlock("basalt_furnace", BlockNetherFurnace::new)
            .replacePropertiesWithCopy(Blocks.BASALT)
            .addTags(CommonPoiTags.ARMORER_WORKSTATION)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.furnaceRecipe(Blocks.BASALT))
            .buildAndRegister();
    public static final Block NETHERRACK_FURNACE = NetherBlocks.defineBlock("netherrack_furnace", BlockNetherFurnace::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTags(CommonPoiTags.ARMORER_WORKSTATION)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.furnaceRecipe(Blocks.NETHERRACK))
            .buildAndRegister();
    public static final Block CINCINNASITE_FORGE = NetherBlocks.defineBlock("cincinnasite_forge", BlockCincinnasiteForge::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelfCopyName())
            .buildAndRegister();
    public static final Block NETHER_BREWING_STAND = NetherBlocks.registerBlock(
            "nether_brewing_stand",
            Blocks.NETHER_BRICKS,
            TraitLists.and(
                    TraitLists.concat(NetherRender.cutout(), NetherMaterial.stone()),
                    BlockTraits.LOOT_TABLE.dropSelfCopyName()
            ),
            BNBrewingStand::new,
            CommonPoiTags.CLERIC_WORKSTATION
    );
    public static final Block CINCINNASITE_ANVIL = NetherBlocks.defineBlock("cincinnasite_anvil", BlockCincinnasiteAnvil::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherLoot.cincinnasiteAnvil())
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTags(BlockTags.ANVIL)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
