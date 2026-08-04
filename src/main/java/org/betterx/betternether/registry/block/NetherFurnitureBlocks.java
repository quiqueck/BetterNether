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

public class NetherFurnitureBlocks {

    // Dispatch-helper branch resolution (WP5.10): CINCINNASITE_SLAB's instrument is IRON_XYLOPHONE (not
    // BASEDRUM), so the old dispatch's isStone(source) was false; isMetal(source) was frozen false too
    // (see the retired dispatch helper's own javadoc) - so every one of these three furniture blocks always
    // resolved to the Wood variant, confirmed against the committed datapack (taburet_cincinnasite/
    // chair_cincinnasite/bar_stool_cincinnasite all show class=Wood). The .Wood factory is used directly
    // instead of the dispatch call.
    public static final Block TABURET_CINCINNASITE = NetherBlocks.defineBlock(
            "taburet_cincinnasite",
            p -> new BaseTaburet.Wood(NetherMetalBlocks.CINCINNASITE_SLAB, p)
    )
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_SLAB)
            .addTags(BlockTags.MINEABLE_WITH_AXE)
            .addTrait(FurnitureTraits.taburet(NetherMetalBlocks.CINCINNASITE_SLAB, NetherModels.taburetCincinnasite()))
            .buildAndRegister();
    public static final Block CHAIR_CINCINNASITE = NetherBlocks.defineBlock(
            "chair_cincinnasite",
            p -> new BaseChair.Wood(NetherMetalBlocks.CINCINNASITE_SLAB, NetherStoneBlocks.NETHER_BRICK_TILE_LARGE, p)
    )
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_SLAB)
            .addTags(BlockTags.MINEABLE_WITH_AXE)
            .addTrait(FurnitureTraits.chair(NetherMetalBlocks.CINCINNASITE_SLAB, NetherModels.chairCincinnasite()))
            .buildAndRegister();
    public static final Block BAR_STOOL_CINCINNASITE = NetherBlocks.defineBlock(
            "bar_stool_cincinnasite",
            p -> new BaseBarStool.Wood(NetherMetalBlocks.CINCINNASITE_SLAB, NetherStoneBlocks.NETHER_BRICK_TILE_LARGE, p)
    )
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_SLAB)
            .addTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE)
            .addTrait(FurnitureTraits.barStool(NetherMetalBlocks.CINCINNASITE_SLAB, NetherModels.barStoolCincinnasite()))
            .buildAndRegister();
    // Storage
    public static final Block CHEST_OF_DRAWERS = NetherBlocks.registerBlock(
            "chest_of_drawers",
            NetherMetalBlocks.CINCINNASITE_BLOCK,
            NetherMaterial.metal(),
            BlockChestOfDrawers::new
    );
    // Former registerTrimmedChest bundle, inlined at the registration site (WP5.11): wood + chest
    // classification, the shapeless trim-chest recipe, and the chest model built from the planks, via
    // NetherCompositeTraits.trimmedChest(...) (WP4.9). Keeps WOOD_BLOCK.withDefault() (flammable)
    // unchanged, per WP4.9's note - the decision-6 nether-wood fire policy is a separate REVIEWED step.
    public static final Block TRIMMED_MUSHROOM_FIR_CHEST = NetherBlocks.defineBlock(
            "mushroom_fir_trimmed_chest",
            p -> new ChestBlock(() -> BlockEntityType.CHEST, p)
    )
            .replacePropertiesWithCopy(NetherWoodBlocks.MAT_MUSHROOM_FIR.getPlanks())
            .addTrait(NetherCompositeTraits.trimmedChest(
                    NetherWoodBlocks.MAT_MUSHROOM_FIR.getPlanks(),
                    NetherWoodBlocks.MAT_MUSHROOM_FIR.getBlock(SlotType.CHEST),
                    NetherWoodBlocks.MAT_MUSHROOM_FIR.getStrippedLog()
            ))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
