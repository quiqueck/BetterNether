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
import org.betterx.betternether.registry.item.NetherResourceItems;

public class NetherLightBlocks {

    // BlockCincinnasiteLantern (a BlockBase subclass) used to set lightLevel(state -> 15) in its own
    // constructor (R1 violation - moved to the registration chain, WP6.11) and always dropped itself
    // unconditionally via the inherited getDrops() override (no loot table json was generated for it,
    // reproduced explicitly as NetherLoot.dropSelfNoExplosion()). The class had no other behaviour, so it
    // is dissolved in favour of vanilla Block.
    public static final Block CINCINNASITE_LANTERN = NetherBlocks.defineBlock("cincinnasite_lantern", Block::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(ModelTraitLibrary.cube())
            .lightLevel(state -> 15)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BlockSmallLantern (WP6.12): noOcclusion() moved out of its constructor (R1); always dropped itself
    // unconditionally via BlockBase's inherited getDrops() override (no loot table json was generated for
    // it), reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    public static final Block CINCINNASITE_LANTERN_SMALL = NetherBlocks.defineBlock("cincinnasite_lantern_small", BlockSmallLantern.Metal::new)
            .replacePropertiesWithCopy(CINCINNASITE_LANTERN)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // Plant-derived light block, shroomlight-archetype (REVIEWED): a gourd-derived lantern is closer to
    // shroomlight than to a nether-wood preset - was wrongly axe-mineable/tool-gated (Materials.makeNetherWood
    // preset, WP3.8) at wood strength (2.0/2.0) instead of shroomlight's soft 1.0/1.0, hoe-mineable, no
    // required tool. mapColor stays COLOR_BLUE explicitly (this block's own cosmetic identity).
    public static final Block WHISPERING_GOURD_LANTERN = NetherBlocks.defineBlock("whispering_gourd_lantern", BlockWhisperingGourdLantern::new)
            .addTrait(BlockTraits.PLANT_LIGHT_BLOCK.withDefault())
            .addTrait(CompostableBlockTrait.withChance(0.65f))
            .mapColor(MapColor.COLOR_BLUE)
            .buildAndRegister();
    // Fire Bowls
    // Former registerFireBowl bundle, inlined at the registration site (WP5.11): cutout render layer plus
    // the fire-bowl crafting recipe (skipped for the netherite bowls, matching the old !isNetherite guard),
    // via NetherCompositeTraits.fireBowl(...) (WP4.9). The material bundle and factory stay at the site.
    // BlockFireBowl (WP6.12): noOcclusion()/lightLevel(getLuminance) moved out of its constructor (R1); all
    // six always dropped themselves unconditionally via BlockBase's inherited getDrops() override (no loot
    // table json was generated for any of them), reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    public static final Block CINCINNASITE_FIRE_BOWL = NetherBlocks.defineBlock("cincinnasite_fire_bowl", BlockFireBowl.Metal::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_FORGED)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(NetherCompositeTraits.fireBowl(NetherMetalBlocks.CINCINNASITE_FORGED, Blocks.NETHERRACK, NetherResourceItems.CINCINNASITE_INGOT, true))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block BRICKS_FIRE_BOWL = NetherBlocks.defineBlock("bricks_fire_bowl", BlockFireBowl.Stone::new)
            .replacePropertiesWithCopy(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherCompositeTraits.fireBowl(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE, Blocks.NETHERRACK, Items.NETHER_BRICK, true))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block NETHERITE_FIRE_BOWL = NetherBlocks.defineBlock("netherite_fire_bowl", BlockFireBowl.Metal::new)
            .replacePropertiesWithCopy(Blocks.NETHERITE_BLOCK)
            // #32: netherite toughness (50/1200); material 5/6 would nerf it
            .addTrait(NetherMaterial.metalTagOnly())
            .addTrait(NetherCompositeTraits.fireBowl(Blocks.NETHERITE_BLOCK, Blocks.NETHERRACK, Items.NETHERITE_INGOT, false))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block CINCINNASITE_FIRE_BOWL_SOUL = NetherBlocks.defineBlock("cincinnasite_fire_bowl_soul", BlockFireBowl.Metal::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_FORGED)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(NetherCompositeTraits.fireBowl(NetherMetalBlocks.CINCINNASITE_FORGED, Blocks.SOUL_SAND, NetherResourceItems.CINCINNASITE_INGOT, true))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block BRICKS_FIRE_BOWL_SOUL = NetherBlocks.defineBlock("bricks_fire_bowl_soul", BlockFireBowl.Stone::new)
            .replacePropertiesWithCopy(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherCompositeTraits.fireBowl(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE, Blocks.SOUL_SAND, Items.NETHER_BRICK, true))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block NETHERITE_FIRE_BOWL_SOUL = NetherBlocks.defineBlock("netherite_fire_bowl_soul", BlockFireBowl.Metal::new)
            .replacePropertiesWithCopy(Blocks.NETHERITE_BLOCK)
            // #32: netherite toughness (50/1200); material 5/6 would nerf it
            .addTrait(NetherMaterial.metalTagOnly())
            .addTrait(NetherCompositeTraits.fireBowl(Blocks.NETHERITE_BLOCK, Blocks.SOUL_SAND, Items.NETHERITE_INGOT, false))
            .noOcclusion()
            .lightLevel(BlockFireBowl::getLuminance)
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
