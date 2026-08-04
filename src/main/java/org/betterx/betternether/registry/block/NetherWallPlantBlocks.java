package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.PlantBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnSolidTrait;
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

public class NetherWallPlantBlocks {

    // Wall plants
    // BlockPlantWall (WP6.12): all four always dropped themselves unconditionally via BlockBase's inherited
    // getDrops() override (no loot table json was generated for any of them), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion() on each.
    // Former Materials.makeNetherGrass() preset, folded (category-traits Batch 3) onto BCLib
    // PlantBlockTrait's Shape-A overload directly (not the NetherMaterial.grass() alias, which hardcodes
    // the XZ ground-offset) - these three keep the ctor's own offsetType(NONE) override, and since a
    // trait's configure() always runs after any inline chain, NetherMaterial.grass()'s baked-in XZ would
    // have won regardless of chain position.
    public static final Block WALL_MOSS = NetherBlocks.defineBlock("wall_moss", BlockPlantWall::new)
            .addTrait(NetherRender.cutout())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_RED, false, BlockBehaviour.OffsetType.NONE, SoundType.GRASS, true
            ))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .buildAndRegister();
    public static final Block WALL_MUSHROOM_BROWN = NetherBlocks.defineBlock("wall_mushroom_brown", BlockPlantWall::new)
            .addTrait(NetherRender.cutout())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_BROWN, false, BlockBehaviour.OffsetType.NONE, SoundType.GRASS, true
            ))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .buildAndRegister();
    public static final Block WALL_MUSHROOM_RED = NetherBlocks.defineBlock("wall_mushroom_red", BlockPlantWall::new)
            .addTrait(NetherRender.cutout())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_RED, false, BlockBehaviour.OffsetType.NONE, SoundType.GRASS, true
            ))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
