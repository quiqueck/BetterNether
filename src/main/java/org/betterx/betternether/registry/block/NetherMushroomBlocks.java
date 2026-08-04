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

public class NetherMushroomBlocks {

    // stays inline: the ctor chains destroyTime(0.5F) after where the netherPlant() preset's instabreak()
    // used to sit. NetherMaterial.plant()'s trait forces instabreak() unconditionally in its configure(),
    // which always runs after any chained setter - folding would zero destroyTime back to 0. This is the
    // category-traits proposal's canonical ordering-hazard example (section 6).
    public static final Block ORANGE_MUSHROOM = NetherBlocks.defineBlock("orange_mushroom", BlockOrangeMushroom::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherMycelium())
            // Former Materials.netherPlant() preset, inlined at the registration site (WP3.1).
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
            .noCollission()
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
            .randomTicks()
            .destroyTime(0.5F)
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Former Materials.makeNetherGrass() preset, folded (category-traits Batch 3) onto PlantBlockTrait
    // directly (not the NetherMaterial.grass() GRASS-sound alias) - this block's sound(CROP) deviation must
    // be baked into the trait call itself, since a trait's configure() always wins over any chained setter.
    public static final Block RED_MOLD = NetherBlocks.defineBlock("red_mold", BlockRedMold::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherMycelium())
            .addTrait(NetherLoot.netherGrass())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.TERRACOTTA_RED, false, BlockBehaviour.OffsetType.XZ, SoundType.CROP, true
            ))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block GRAY_MOLD = NetherBlocks.defineBlock("gray_mold", BlockGrayMold::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherMycelium())
            .addTrait(NetherLoot.netherGrass())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_GRAY, false, BlockBehaviour.OffsetType.XZ, SoundType.CROP, true
            ))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // BlockLucisSpore (WP6.12): always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // Former Materials.netherSapling() preset, folded onto NetherMaterial.sapling().
    public static final Block LUCIS_SPORE = NetherBlocks.defineBlock("lucis_spore", BlockLucisSpore::new)
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_LIGHT_GREEN))
            .lightLevel(bs -> 7)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.seed())
            .buildAndRegister();
    // Former Materials.walkablePlant(COLOR_YELLOW) preset (+ the ctor's own requiresCorrectToolForDrops()/
    // lightLevel()/sound()/strength() overrides), inlined at the registration site (WP3.6: walkablePlant()
    // was never assigned to a WP1-3.5 preset bucket - only these two blocks call it directly).
    // walkablePlant()'s own instabreak()/sound(GRASS) are immediately overridden by the ctor's final
    // sound(WOOD)/strength(1F), so per R7 only the final values are reproduced here.
    public static final Block GIANT_LUCIS = NetherBlocks.defineBlock("giant_lucis", BlockGiantLucis::new)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .addTrait(NetherLoot.giantLucis())
            .mapColor(MapColor.COLOR_YELLOW)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
            .requiresCorrectToolForDrops()
            .lightLevel(bs -> 15)
            .sound(SoundType.WOOD)
            .strength(1F)
            .buildAndRegister();
    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant().
    public static final Block BONE_MUSHROOM = NetherBlocks.defineBlock("bone_mushroom", BlockBoneMushroom::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.boneBlocks())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_LIGHT_GREEN))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // BlockSmoker (WP6.12): always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // Former Materials.makeNetherWood(COLOR_BROWN) preset, folded onto NetherMaterial.mushroomStem() - no
    // ctor overrides here, so the full expansion is reproduced verbatim.
    public static final Block SMOKER = NetherBlocks.defineBlock("smoker", BlockSmoker::new)
            .addTrait(NetherSurvival.netherGround())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .addTrait(NetherMaterial.mushroomStem(MapColor.COLOR_BROWN))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // Former Materials.makeNetherGrass() preset, folded onto PlantBlockTrait directly (sound(CROP)
    // deviation baked into the trait call, same as RED_MOLD/GRAY_MOLD above).
    public static final Block HOOK_MUSHROOM = NetherBlocks.defineBlock("hook_mushroom", BlockHookMushroom::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrack())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_PINK, false, BlockBehaviour.OffsetType.XZ, SoundType.CROP, true
            ))
            .lightLevel(s -> 13)
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Large & Small Mushrooms //
    // Former Materials.makeNetherWood(COLOR_RED) preset, folded onto NetherMaterial.mushroomStem() - the
    // trailing noOcclusion() stays chained (the preset never touches it, no ordering hazard).
    public static final Block RED_LARGE_MUSHROOM = NetherBlocks.defineBlock("red_large_mushroom", BlockRedLargeMushroom::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .addTrait(NetherMaterial.mushroomStem(MapColor.COLOR_RED))
            .noOcclusion()
            .buildAndRegister();
    // stays inline: this block's final strength is 1, but NetherMaterial.mushroomStem()'s trait forces
    // strength(2.0F) unconditionally in its configure(), which always runs after any chained setter -
    // folding would clobber destroyTime/resistance back to 2.0. Category-traits Batch 3 ordering hazard.
    public static final Block BROWN_LARGE_MUSHROOM = NetherBlocks.defineBlock("brown_large_mushroom", BlockBrownLargeMushroom::new)
            .withBlockItem((d, b) -> null)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_BROWN)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .strength(1)
            .noOcclusion()
            .buildAndRegister();
    // Lucis //
    // Former Materials.walkablePlant(COLOR_YELLOW) preset (+ the ctor's own lightLevel()/
    // requiresCorrectToolForDrops()/sound()/strength() overrides), inlined at the registration site (WP3.6:
    // walkablePlant() was never assigned to a WP1-3.5 preset bucket). Per R7 only the final post-override
    // values are reproduced (walkablePlant()'s own instabreak()/sound(GRASS) are immediately replaced by
    // sound(WOOD)/strength(1F)).
    public static final Block LUCIS_MUSHROOM = NetherBlocks.defineBlock("lucis_mushroom", BlockLucisMushroom::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherLoot.lucisMushroom())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_YELLOW)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
            .lightLevel(bs -> 15)
            .requiresCorrectToolForDrops()
            .sound(SoundType.WOOD)
            .strength(1F)
            .buildAndRegister();
    // Giant Mold //
    // stays inline: same ordering hazard as BROWN_LARGE_MUSHROOM - final strength is 1, not
    // mushroomStem()'s forced 2.0F.
    public static final Block GIANT_MOLD = NetherBlocks.defineBlock("giant_mold", BlockGiantMold::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_GRAY)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .strength(1)
            .buildAndRegister();
    // Sodium renders the translucent variant with artefacts, so under Sodium this falls back to cutout.
    // The conditional moved here verbatim from the old BlockJellyfishMushroom constructor.
    // stays inline: this block's final sound(FUNGUS)/strength(1) both diverge from
    // mushroomStem()'s forced sound(WOOD)/strength(2.0F) - same ordering hazard family as
    // BROWN_LARGE_MUSHROOM/GIANT_MOLD, doubled up. R5/dead-argument flag: the ctor chained
    // .destroyTime(0.1F) BEFORE .strength(1) - since strength(float) sets both destroyTime and resistance,
    // that later call fully overwrote the destroyTime(0.1F) value, making it dead. The golden line
    // (destroyTime=1.0, resistance=1.0) confirms strength(1) is what actually took effect; destroyTime(0.1F)
    // is correctly NOT reproduced here.
    public static final Block JELLYFISH_MUSHROOM = NetherBlocks.defineBlock("jellyfish_mushroom", BlockJellyfishMushroom::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherLoot.jellyfishMushroom())
            .addTrait(FabricLoader.getInstance().isModLoaded("sodium") ? NetherRender.cutout() : NetherRender.translucent())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_CYAN)
            .instrument(NoteBlockInstrument.BASS)
            .requiresCorrectToolForDrops()
            .lightLevel(s -> 13)
            .sound(SoundType.FUNGUS)
            .strength(1)
            .noOcclusion()
            .buildAndRegister();

    public static void ensureLoaded() {}
}
