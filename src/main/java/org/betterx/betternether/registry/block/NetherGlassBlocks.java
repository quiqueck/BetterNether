package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.GlassBlockTrait;
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

public class NetherGlassBlocks {

    // Quartz Glass //
    // BaseGlassBlock used to drop itself only when silk-touched through the retired
    // BlockLootProvider interface; that loot is now a wover trait. noOcclusion()/explosionResistance(0.3)/
    // isSuffocating/isViewBlocking moved out of the BaseGlassBlock constructor (R1) into GlassBlockTrait,
    // which also adds the isValidSpawn/isRedstoneConductor calls the constructor always missed.
    public static final Block QUARTZ_GLASS = NetherBlocks.registerBlock("quartz_glass", Blocks.GLASS, TraitLists.of(ClientBlockTraits.RENDER_LAYER.translucent(), NetherModels.quartzGlass(), BlockTraits.LOOT_TABLE.silkTouchSelf(), BlockTraits.MINEABLE_WITH.needsPickAxe(), GlassBlockTrait.glass(0.3f)), p -> new BaseGlassBlock(p));
    // Copies its properties from CINCINNASITE_BLOCK, an opaque stone-like block, so before GlassBlockTrait
    // it inherited that parent's redstone conduction and mob spawning - which is exactly why this block was
    // wrong while QUARTZ_GLASS above (copied from Blocks.GLASS, which sets both to never) looked fine.
    public static final Block QUARTZ_GLASS_FRAMED = NetherBlocks.registerBlock(
            "quartz_glass_framed",
            NetherMetalBlocks.CINCINNASITE_BLOCK,
            TraitLists.of(ClientBlockTraits.RENDER_LAYER.translucent(), NetherModels.quartzGlass(), BlockTraits.LOOT_TABLE.silkTouchSelf(), BlockTraits.MINEABLE_WITH.needsPickAxe(), GlassBlockTrait.glass(0.3f)),
            p -> new BaseGlassBlock(p)
    );
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed",
            QUARTZ_GLASS_FRAMED
    );
    // BNPane's dropSelf=true forced a self-drop via getDrops(), unconditionally, with no committed loot
    // table json - reproduced explicitly (WP6.10) as NetherLoot.dropSelfNoExplosion(). BNPane.Glass
    // dissolved to plain BNPane (WP6.14 sweep); strength(0.3, 0.3)/noOcclusion() moved out of its
    // constructor (R1) to here, after the material trait so the override still wins. class= changes
    // Glass -> BNPane (CLASS-ONLY) for this block and every colored variant below.
    public static final Block QUARTZ_GLASS_PANE = NetherBlocks.defineBlock("quartz_glass_pane", p -> new BNPane(p))
            .replacePropertiesWithCopy(QUARTZ_GLASS)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.glass())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .strength(0.3F, 0.3F)
            .noOcclusion()
            .buildAndRegister();
    public static final ColoredGlassMaterial QUARTZ_GLASS_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_pane",
            QUARTZ_GLASS_PANE,
            p -> new BNPane(p),
            // These panes drop only when silk-touched (BNPane(dropSelf=false) reads the loot table);
            // the silk-touch-only table is now generated by the wover trait instead of hand-authored.
            TraitLists.and(NetherMaterial.glass(), BlockTraits.LOOT_TABLE.silkTouchSelf()),
            def -> {
                def.strength(0.3F, 0.3F);
                def.noOcclusion();
            }
    );
    // BNPane's dropSelf=true forced a self-drop via getDrops(), unconditionally, with no committed loot
    // table json (base and every colored variant) - reproduced explicitly (WP6.10) as
    // NetherLoot.dropSelfNoExplosion(). BNPane.Metal dissolved to plain BNPane (WP6.14 sweep);
    // strength(0.3, 0.3)/noOcclusion() moved out of its constructor (R1) to here. class= changes
    // Metal -> BNPane (CLASS-ONLY) for this block and every colored variant below.
    public static final Block QUARTZ_GLASS_FRAMED_PANE = NetherBlocks.defineBlock("quartz_glass_framed_pane", p -> new BNPane(p))
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.metal())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .strength(0.3F, 0.3F)
            .noOcclusion()
            .buildAndRegister();
    public static final ColoredGlassMaterial QUARTZ_GLASS_FRAMED_PANE_COLORED = new ColoredGlassMaterial(
            "quartz_glass_framed_pane",
            QUARTZ_GLASS_FRAMED_PANE,
            p -> new BNPane(p),
            TraitLists.and(NetherMaterial.metal(), NetherLoot.dropSelfNoExplosion()),
            def -> {
                def.strength(0.3F, 0.3F);
                def.noOcclusion();
            }
    );
    // Quartz Glass Colored //
    public static final ColoredGlassMaterial QUARTZ_GLASS_COLORED = new ColoredGlassMaterial(
            "quartz_glass",
            QUARTZ_GLASS
    );

    public static void ensureLoaded() {}
}
