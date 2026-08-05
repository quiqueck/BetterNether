package org.betterx.betternether.registry;

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
import org.betterx.betternether.registry.block.NetherTerrainBlocks;
import org.betterx.betternether.registry.block.NetherStoneBlocks;
import org.betterx.betternether.registry.block.NetherMetalBlocks;
import org.betterx.betternether.registry.block.NetherOreBlocks;
import org.betterx.betternether.registry.block.NetherObsidianBlocks;
import org.betterx.betternether.registry.block.NetherGlassBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import org.betterx.betternether.registry.block.NetherLeavesBlocks;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherWallPlantBlocks;
import org.betterx.betternether.registry.block.NetherCropBlocks;
import org.betterx.betternether.registry.block.NetherSaplingBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherLightBlocks;
import org.betterx.betternether.registry.block.NetherFurnitureBlocks;
import org.betterx.betternether.registry.block.NetherFunctionalBlocks;
import org.betterx.betternether.registry.block.NetherDecorBlocks;

/**
 * Registry facade for BetterNether's blocks. The actual field declarations live in the per-category
 * classes under {@code org.betterx.betternether.registry.block} (see {@link #register()}
 * for the boot order); this class only keeps the shared registration forwarders and the driver
 * that loads every category class in the correct order.
 */
public class NetherBlocks {


    private static BlockRegistry BLOCKS_REGISTRY;

    private NetherBlocks() {

    }

    @NotNull
    public static BlockRegistry getBlockRegistry() {
        if (BLOCKS_REGISTRY == null) {
            BLOCKS_REGISTRY = BlockRegistry.forMod(BetterNether.C);
        }
        return BLOCKS_REGISTRY;
    }

    public static Stream<Block> getModBlocks() {
        return getBlockRegistry().allBlocks();
    }

    public static Stream<BlockItem> getModBlockItems() {
        return getBlockRegistry().allBlockItems();
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    public static <T extends Block> T registerBlock(
            String name,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    public static <T extends Block> T registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .addTags(tags)
                .buildAndRegister();
    }

    // Pure forwarder (R4): starts a definition chain with zero traits/properties of its own, mirroring BE's
    // EndBlocks.defineBlock. Used to inline the register* helpers that used to hide traits (Phase 5).
    public static <T extends Block> DefaultBlockDefinition<T> defineBlock(
            String name,
            Function<BlockBehaviour.Properties, T> blockF
    ) {
        return getBlockRegistry()
                .defineDefaultBlock(name, def -> blockF.apply(def.getProperties()));
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    public static <T extends Block> T registerBlock(
            String name,
            Block propertiesSource,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    public static <T extends Block> T registerBlock(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    /**
     * @param survival the ground this block can be placed on (see {@link NetherSurvival}); the traits are
     *                 OR-ed, and {@link SurvivesOnBlockTrait#survivesOn} reads them back in canSurvive
     */
    public static <T extends Block> T registerBlockNI(
            String name,
            List<BlockTrait<?, ?>> survival,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .withBlockItem((d, b) -> null)
                .addTags(tags);
        definition.addTrait(survival);
        return definition.buildAndRegister();
    }

    public static <T extends Block> T registerBlockNI(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .withBlockItem((d, b) -> null)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlockNI(
            String name,
            Block propertiesSource,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        return getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .withBlockItem((d, b) -> null)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlockNI(
            String name,
            Block propertiesSource,
            List<BlockTrait<?, ?>> traits,
            Function<BlockBehaviour.Properties, T> factory,
            TagKey<Block>... tags
    ) {
        final var definition = getBlockRegistry()
                .<T>defineDefaultBlock(name, def -> factory.apply(def.getProperties()))
                .replacePropertiesWithCopy(propertiesSource)
                .withBlockItem((d, b) -> null)
                .addTags(tags);
        definition.addTrait(traits);
        return definition.buildAndRegister();
    }

    @ApiStatus.Internal
    public static void register() {
        NetherTerrainBlocks.ensureLoaded();
        NetherStoneBlocks.ensureLoaded();
        NetherMetalBlocks.ensureLoaded();
        NetherOreBlocks.ensureLoaded();
        NetherObsidianBlocks.ensureLoaded();
        NetherGlassBlocks.ensureLoaded();
        NetherWoodBlocks.ensureLoaded();
        NetherLeavesBlocks.ensureLoaded();
        NetherPlantBlocks.ensureLoaded();
        NetherWallPlantBlocks.ensureLoaded();
        NetherCropBlocks.ensureLoaded();
        NetherSaplingBlocks.ensureLoaded();
        NetherVineBlocks.ensureLoaded();
        NetherMushroomBlocks.ensureLoaded();
        NetherLightBlocks.ensureLoaded();
        NetherFurnitureBlocks.ensureLoaded();
        NetherFunctionalBlocks.ensureLoaded();
        NetherDecorBlocks.ensureLoaded();
    }
}
