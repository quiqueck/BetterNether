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

public class NetherWoodBlocks {


    // Reed //
    // BlockNetherReed (a BlockBase subclass) always dropped itself unconditionally via the inherited
    // getDrops() override - no loot table json was generated for it. Reproduced explicitly (WP6.9) as
    // NetherLoot.dropSelfNoExplosion(). The class keeps its real behaviour (growth/canSurvive/updateShape),
    // just off BlockBase now.
    // WP: mineable-audit §5 reconciliation: this is the sole wover:mineable/sword member in either mod, and
    // it is left as-is rather than folded into minecraft:sword_efficient. The two tags are different
    // mechanics - wover:mineable/sword (MINEABLE_WITH.needsSword()) marks a sword as a "correct tool" like
    // mineable/pickaxe|axe|hoe do, while sword_efficient is purely a mining-speed bonus that never gates
    // drops - so merging them would conflate an existing, deliberate "sword can interact with this reed"
    // classification with the new speed-bonus convention. nether_reed_stem is not part of the shared
    // VegetationTagTrait plant/vine family this WP extends, so it is untouched either way.
    public static final Block NETHER_REED_STEM = NetherBlocks.defineBlock("nether_reed_stem", BlockNetherReed::new)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(BlockTraits.MINEABLE_WITH.needsSword())
            .addTrait(WeightedCrossModelTrait.booleanDispatch(
                    BlockNetherReed.TOP,
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/nether_reed_stem"))),
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/reeds_top"))),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/nether_reed_stem"))
            ))
            // Former Materials.netherPlant() preset, inlined at the registration site (WP3.1).
            .mapColor(MapColor.COLOR_CYAN)
            .noOcclusion()
            .noCollission()
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
            .randomTicks()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final NetherReedMaterial MAT_REED = new NetherReedMaterial().init();

    // Stalagnate //
    public static final StalagnateMaterial MAT_STALAGNATE = new StalagnateMaterial().init();

    // Willow //
    public static final WillowMaterial MAT_WILLOW = new WillowMaterial().init();

    // Wart //
    public static final WartMaterial MAT_WART = new WartMaterial(
            "wart",
            MapColor.COLOR_RED,
            MapColor.COLOR_RED
    ).init();

    public static final VanillaNetherWood WARPED_WOOD = new VanillaNetherWood(
            "warped",
            Blocks.WARPED_PLANKS.defaultMapColor(),
            MapColor.WARPED_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaNetherWood CRIMSON_WOOD = new VanillaNetherWood(
            "crimson",
            Blocks.CRIMSON_PLANKS.defaultMapColor(),
            MapColor.CRIMSON_STEM
    ).setFurnitureCloth(Blocks.RED_WOOL).init();

    public static final VanillaWood OAK_WOOD = VanillaWood.create("oak", Blocks.RED_WOOL);
    public static final VanillaWood SPRUCE_WOOD = VanillaWood.create("spruce", Blocks.RED_WOOL);
    public static final VanillaWood BIRCH_WOOD = VanillaWood.create("birch", Blocks.RED_WOOL);
    public static final VanillaWood JUNGLE_WOOD = VanillaWood.create("jungle", Blocks.RED_WOOL);
    public static final VanillaWood ACACIA_WOOD = VanillaWood.create("acacia", Blocks.BLACK_WOOL);
    public static final VanillaWood DARK_OAK_WOOD = VanillaWood.create("dark_oak", Blocks.RED_WOOL);
    public static final VanillaWood CHERRY_WOOD = VanillaWood.create("cherry", Blocks.WHITE_WOOL);
    public static final VanillaWood BAMBOO_WOOD = VanillaWood.create("bamboo", Blocks.BROWN_WOOL);
    public static final VanillaWood MANGROVE_WOOD = VanillaWood.create("mangrove", Blocks.BLACK_WOOL);
    // Rubeus //
    public static final RubeusMaterial MAT_RUBEUS = new RubeusMaterial().init();
    // Mushroom Fir //
    public static final MushroomFirMaterial MAT_MUSHROOM_FIR = new MushroomFirMaterial().init();
    // Mushroom //
    public static final NetherMushroomMaterial MAT_NETHER_MUSHROOM = new NetherMushroomMaterial().init();
    // Anchor Tree
    public static final AnchorTreeMaterial MAT_ANCHOR_TREE = new AnchorTreeMaterial().init();
    // Nether Sakura
    public static final NetherSakuraMaterial MAT_NETHER_SAKURA = new NetherSakuraMaterial().init();

    public static void ensureLoaded() {}
}
