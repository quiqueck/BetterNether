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

public class NetherCropBlocks {

    // Former Materials.netherPlant() preset, folded (category-traits Batch 3) onto NetherMaterial.plant().
    public static final Block BLACK_APPLE = NetherBlocks.defineBlock("black_apple", BlockBlackApple::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherMaterial.plant(MapColor.TERRACOTTA_ORANGE))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            // Was the hand-authored loot_table/blocks/black_apple.json; block loot is trait-only here.
            .addTrait(NetherLoot.blackApple())
            .buildAndRegister();
    // BlockBlackAppleSeed (WP6.12): same reproduction as BlockInkBushSeed above.
    // Former Materials.netherSapling() preset, folded onto NetherMaterial.sapling().
    public static final Block BLACK_APPLE_SEED = NetherBlocks.defineBlock("black_apple_seed", BlockBlackAppleSeed::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_ORANGE))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.seed())
            .buildAndRegister();
    // BlockWhisperingGourd (a BlockBase subclass with no other overrides) always dropped itself
    // unconditionally via the inherited getDrops() override - no loot table json was generated for it.
    // Reproduced explicitly (WP6.11) as NetherLoot.dropSelfNoExplosion(); the class had no other behaviour,
    // so it is dissolved in favour of vanilla Block.
    // Former Materials.makeNetherWood(COLOR_BLUE) preset (+ the ctor's own strength() override), inlined at
    // the registration site (WP3.8).
    // stays inline: NetherMaterial.mushroomStem() forces strength(2.0F) in its trait configure(), which
    // always runs after any chained setter regardless of call-site order - this block's final strength is
    // 0.5f, so folding would clobber it back to 2.0F. Category-traits Batch 3 ordering hazard.
    public static final Block WHISPERING_GOURD = NetherBlocks.defineBlock("whispering_gourd", Block::new)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_BLUE)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .strength(0.5f)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            // Vanilla's soft organic cubes (moss, sponge, dried_kelp, melon, pumpkin) are fast_flat.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.fastFlat())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
