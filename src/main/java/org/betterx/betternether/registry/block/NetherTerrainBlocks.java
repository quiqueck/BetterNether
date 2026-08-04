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
import net.minecraft.resources.ResourceLocation;
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
import org.betterx.betternether.registry.NetherTags;

public class NetherTerrainBlocks {

    // Terrain //
    public static final BlockTerrain NETHERRACK_MOSS = NetherBlocks.registerBlock(
            "netherrack_moss",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockNetherMycelium NETHER_MYCELIUM = NetherBlocks.registerBlock(
            "nether_mycelium",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain()),
            BlockNetherMycelium::new,
            CommonBlockTags.MYCELIUM,
            CommonBlockTags.NETHER_MYCELIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK,
            de.ambertation.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM,
            // Nether mycelium is plantable ground - small plants may grow on it.
            CommonBlockTags.SOIL
    );
    public static final BlockTerrain JUNGLE_GRASS = NetherBlocks.registerBlock(
            "jungle_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain MUSHROOM_GRASS = NetherBlocks.registerBlock(
            "mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SEPIA_MUSHROOM_GRASS = NetherBlocks.registerBlock(
            "sepia_mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SWAMPLAND_GRASS = NetherBlocks.registerBlock(
            "swampland_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // BlockFarmland (a BlockBase subclass, kept as its own class: BlocksHelper.isFertile() does an
    // `instanceof BlockFarmland` check) always dropped itself unconditionally via the inherited getDrops()
    // override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion(); the class itself moves off BlockBase onto vanilla Block.
    // Former Materials.makeNetherWood(TERRACOTTA_LIGHT_GREEN) preset, inlined at the registration site
    // (WP3.8) - no ctor overrides here, full expansion reproduced verbatim as chained setters (cleanup).
    public static final Block FARMLAND = NetherBlocks.defineBlock("farmland", BlockFarmland::new)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTags(CommonBlockTags.SOUL_GROUND, CommonBlockTags.NETHERRACK, NetherTags.NETHER_FARMLAND)
            .buildAndRegister();
    public static final BlockTerrain CEILING_MUSHROOMS = NetherBlocks.registerBlock(
            "ceiling_mushrooms",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final Block VEINED_SAND = NetherBlocks.registerBlockNI(
            "veined_sand",
            Blocks.SAND,
            TraitLists.of(BlockTraits.MINEABLE_WITH.needsShovel()),
            BlockVeinedSand::new,
            NetherTags.NETHER_SAND,
            CommonBlockTags.SOUL_GROUND
    );

    public static void ensureLoaded() {}
}
