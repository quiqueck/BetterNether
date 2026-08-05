package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.FurnitureTraits;
import org.betterx.bclib.trait.block.LeavesBlockTrait;
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
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
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

public class NetherLeavesBlocks {

    // Former registerLeaves bundle, inlined at the registration site (WP5.8): vanilla-style
    // sapling/stick drop, compostable, plant-like, shears/hoe mineable, leaves tags.
    // Batch 1 (category-traits fold): the semantic bundle (PlantLikeBlockTrait marker + shears/hoe +
    // compostable(0.3) + VegetationTagTrait.leaves()) and the property core (former
    // Materials.staticLeaves(color, false) preset) are both now reproduced by a single
    // LeavesBlockTrait.withColor(...) call - generateModel=false (these keep their hand-authored
    // resources/models), flammable=false (nether leaves don't burn - staticLeaves() never carried
    // FLAMMABLE), cutout=false (golden is renderLayer=SOLID, not the vanilla-leaves cutout layer).
    public static final Block WILLOW_LEAVES = NetherBlocks.defineBlock(
            "willow_leaves",
            p -> new BlockWillowLeaves(NetherWoodBlocks.MAT_WILLOW.getSapling(), p)
    )
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.TERRACOTTA_RED, 0, false, -1, NetherWoodBlocks.MAT_WILLOW.getSapling(),
                    false, true, false, false
            ))
            .buildAndRegister();
    public static final Block RUBEUS_LEAVES = NetherBlocks.defineBlock(
            "rubeus_leaves",
            p -> new BlockRubeusLeaves(NetherWoodBlocks.MAT_RUBEUS.getSapling(), p)
    )
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.COLOR_LIGHT_BLUE, 0, false, -1, NetherWoodBlocks.MAT_RUBEUS.getSapling(),
                    false, true, false, false
            ))
            .buildAndRegister();
    public static final Block ANCHOR_TREE_LEAVES = NetherBlocks.defineBlock(
            "anchor_tree_leaves",
            p -> new BNLeaves(NetherWoodBlocks.MAT_ANCHOR_TREE.getSapling(), p)
    )
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.COLOR_GREEN, 0, false, -1, NetherWoodBlocks.MAT_ANCHOR_TREE.getSapling(),
                    false, true, false, false
            ))
            .buildAndRegister();
    public static final Block NETHER_SAKURA_LEAVES = NetherBlocks.defineBlock(
            "nether_sakura_leaves",
            p -> new BlockNetherSakuraLeaves(NetherWoodBlocks.MAT_NETHER_SAKURA.getSapling(), p)
    )
            // lightLevel=13 (the ctor's former standalone .lightLevel(state -> 13) call) is now the
            // trait's own lightLevel param - the trait's configure() is the only thing that sets it, so
            // ordering is a non-issue here.
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.COLOR_PINK, 13, false, -1, NetherWoodBlocks.MAT_NETHER_SAKURA.getSapling(),
                    false, true, false, false
            ))
            .addTrait(NetherModels.netherSakuraLeavesModelTrait())
            .buildAndRegister();

    // --- gloomwood -------------------------------------------------------------------------------
    // Unlike every leaf block above, these two are plain TintedParticleLeavesBlocks rather than
    // BNLeaves. That is the point: BNLeaves overrides isRandomlyTicking to false and empties both
    // randomTick and tick, which switches vanilla leaf decay off entirely - the reason no BetterNether
    // canopy has ever decayed. The gloomwood tree is built through
    // org.betterx.betternether.world.tree, which guarantees every leaf it places is inside the vanilla
    // decay budget, so these blocks can keep the vanilla behaviour and behave like real leaves when the
    // tree is felled.
    public static final Block GLOOMWOOD_LEAVES = NetherBlocks.defineBlock(
            "gloomwood_leaves",
            p -> new TintedParticleLeavesBlock(0.01F, p)
    )
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.TERRACOTTA_WHITE, 0, false, -1, NetherWoodBlocks.MAT_GLOOMWOOD.getSapling(),
                    true, true, false, true
            ))
            .buildAndRegister();

    /**
     * The pale variant, used by the gloomwood canopy for the "eyes" of the ghost silhouette. A separate
     * block rather than a model variant of {@link #GLOOMWOOD_LEAVES} so that it drops, stacks and builds
     * on its own - the marking is a material, not a random texture roll.
     */
    public static final Block GLOOMWOOD_BLEACHED_LEAVES = NetherBlocks.defineBlock(
            "gloomwood_bleached_leaves",
            p -> new TintedParticleLeavesBlock(0.02F, p)
    )
            .addTrait(LeavesBlockTrait.withColor(
                    MapColor.TERRACOTTA_WHITE, 7, false, -1, NetherWoodBlocks.MAT_GLOOMWOOD.getSapling(),
                    true, true, false, true
            ))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
