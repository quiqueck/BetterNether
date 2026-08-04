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

public class NetherSaplingBlocks {

    // BlockGiantMoldSapling (WP6.12): always dropped itself unconditionally via BlockBase's inherited
    // getDrops() override. noLootTable() is removed - it and a LOOT_TABLE trait are independent mechanisms
    // (noLootTable() sets the vanilla Properties.drops field runtime code reads directly; the trait only
    // feeds datagen's json generation and has zero runtime effect), so keeping noLootTable() would leave
    // this block with no drops at all now that the getDrops() override is gone. Reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // Former Materials.netherSapling() preset, folded (category-traits Batch 3) onto
    // NetherMaterial.sapling(color).
    public static final Block GIANT_MOLD_SAPLING = NetherBlocks.defineBlock("giant_mold_sapling", BlockGiantMoldSapling::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherMycelium())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_LIGHT_GREEN))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.sapling())
            .buildAndRegister();
    // BlockJellyfishMushroomSapling (WP6.12): always dropped itself unconditionally via BlockBase's
    // inherited getDrops() override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    public static final Block JELLYFISH_MUSHROOM_SAPLING = NetherBlocks.defineBlock("jellyfish_mushroom_sapling", BlockJellyfishMushroomSapling::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.nylium())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_CYAN))
            .lightLevel(bs -> 9)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.sapling())
            .buildAndRegister();
    public static final Block SOUL_LILY_SAPLING = NetherBlocks.defineBlock("soul_lily_sapling", BlockSoulLilySapling::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.soulGroundOrFarmland())
            .addTrait(NetherTraits.sapling())
            .addTrait(ModelTraitLibrary.crossPlant())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_ORANGE))
            .noLootTable()
            .buildAndRegister();

    public static void ensureLoaded() {}
}
