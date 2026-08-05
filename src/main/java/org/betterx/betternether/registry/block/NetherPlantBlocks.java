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

public class NetherPlantBlocks {

    // BlockEyeSeed (WP6.12): always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // Former Materials.netherSapling() preset, folded (category-traits Batch 3) onto NetherMaterial.sapling().
    public static final Block EYE_SEED = NetherBlocks.defineBlock("eye_seed", BlockEyeSeed::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrack())
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_RED))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.seed())
            .buildAndRegister();
    // Grass // Former Materials.makeNetherGrass(TERRACOTTA_GRAY) preset, folded onto NetherMaterial.grass().
    public static final Block NETHER_GRASS = NetherBlocks.defineBlock("nether_grass", BlockNetherGrass.NetherGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrackNyliumAndSculk())
            .addTrait(NetherModels.netherGrass())
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block SWAMP_GRASS = NetherBlocks.defineBlock("swamp_grass", BlockNetherGrass.SwampGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrackNyliumAndSculk())
            .addTrait(NetherModels.grass("swamp_grass", 3))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block SOUL_GRASS = NetherBlocks.defineBlock("soul_grass", BlockSoulGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.soilOrLogs())
            .addTrait(NetherModels.grass("soul_grass", 2))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block JUNGLE_PLANT = NetherBlocks.defineBlock("jungle_plant", BlockNetherGrass.JunglePlant::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrackNyliumAndSculk())
            .addTrait(NetherModels.junglePlant())
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block BONE_GRASS = NetherBlocks.defineBlock("bone_grass", BlockNetherGrass.BoneGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.soilOrLogs())
            .addTrait(NetherModels.grass("bone_grass", 3))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Pale gloomgrass: the bone-grass silhouette in the gloomwood's dark-to-bright transition palette.
    // Same block shape and traits as the bone grasses either side of it, so it reuses BoneGrass rather
    // than adding a class that would only differ by its textures.
    // Survives on anything solid rather than on a ground list: worldgen only ever puts it on the sculk
    // floor (its placed feature filters on SCULK_LIKE), so the rule here is purely about where a player
    // may replant one, and a decorative tuft is not worth a lookup table for that.
    public static final Block PALE_GLOOMGRASS = NetherBlocks.defineBlock("pale_gloomgrass", BlockNetherGrass.BoneGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(NetherModels.grass("pale_gloomgrass", 3))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_WHITE))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Gloomgrass: the same tuft on sculk's own palette, growing mixed in with the pale one rather
    // than anywhere of its own - see VEGETATION_GLOOMWOOD, where it takes the larger share.
    public static final Block GLOOMGRASS = NetherBlocks.defineBlock("gloomgrass", BlockNetherGrass.BoneGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(NetherModels.grass("gloomgrass", 3))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.COLOR_BLACK))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block SEPIA_BONE_GRASS = NetherBlocks.defineBlock("sepia_bone_grass", BlockNetherGrass.SepiaBoneGrass::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.soilOrLogs())
            .addTrait(NetherModels.grass("sepia_bone_grass", 3))
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.grass(MapColor.TERRACOTTA_GRAY))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant().
    public static final Block BLACK_BUSH = NetherBlocks.defineBlock("black_bush", BlockBlackBush::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_BLACK))
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    public static final Block INK_BUSH = NetherBlocks.defineBlock("ink_bush", BlockInkBush::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            // Former Materials.plant() preset, folded onto NetherMaterial.plant().
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_BLACK))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            // Was the hand-authored loot_table/blocks/ink_bush.json; block loot is trait-only here.
            .addTrait(NetherLoot.inkBush())
            .buildAndRegister();
    // BlockInkBushSeed (WP6.12): always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no committed loot table json). noLootTable() is removed - it and a LOOT_TABLE trait are
    // independent mechanisms (noLootTable() sets the vanilla Properties.drops field runtime code reads
    // directly; the trait only feeds datagen's json generation and has zero runtime effect), so keeping
    // noLootTable() here would leave the block with no drops at all now that the getDrops() override is
    // gone. Reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    // Former Materials.netherSapling() preset, folded onto NetherMaterial.sapling().
    public static final Block INK_BUSH_SEED = NetherBlocks.defineBlock("ink_bush_seed", BlockInkBushSeed::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherTraits.seed())
            .addTrait(WeightedCrossModelTrait.simple(
                    List.of(WeightedCrossModelTrait.cross(BetterNether.C.mk("block/ink_bush_seed"))),
                    WeightedCrossModelTrait.Item.flat(BetterNether.C.mk("item/ink_bush_seed"))
            ))
            .addTrait(NetherMaterial.sapling(MapColor.COLOR_RED))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant().
    public static final Block EGG_PLANT = NetherBlocks.defineBlock("egg_plant", BlockEggPlant::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(NetherLoot.netherGrass())
            .addTrait(NetherMaterial.plant(MapColor.TERRACOTTA_WHITE))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant().
    public static final Block MAGMA_FLOWER = NetherBlocks.defineBlock("magma_flower", BlockMagmaFlower::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.magmaBlockOrSand())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(NetherMaterial.plant(MapColor.TERRACOTTA_ORANGE))
            .randomTicks()
            // Was the hand-authored loot_table/blocks/magma_flower.json; block loot is trait-only here.
            .addTrait(NetherLoot.magmaFlower())
            .buildAndRegister();
    // Former Materials.netherPlant() preset, folded onto PlantBlockTrait directly (not the
    // NetherMaterial.plant() NONE-offset alias) since this block's own offsetType(XZ) override must
    // survive - and a trait's configure() always wins over any chained setter regardless of call-site
    // order, so the override has to be baked into the trait call itself.
    public static final Block FEATHER_FERN = NetherBlocks.defineBlock("feather_fern", BlockFeatherFern::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherGround())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_LIGHT_BLUE, false, BlockBehaviour.OffsetType.XZ, SoundType.CROP, false
            ))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            // Was the hand-authored loot_table/blocks/feather_fern.json; block loot is trait-only here.
            .addTrait(NetherLoot.featherFern())
            .buildAndRegister();
    // Former Materials.makeNetherGrass(COLOR_GREEN) preset, folded onto PlantBlockTrait directly (not the
    // NetherMaterial.grass() GRASS/XZ alias): the ctor's own sound(CROP)/offsetType(NONE) overrides replace
    // the preset's GRASS sound and XZ offset, and both are baked into the trait's own configure() so they
    // must be passed as the trait's parameters rather than left as separate chained calls.
    public static final Block MOSS_COVER = NetherBlocks.defineBlock("moss_cover", BlockMossCover::new)
            .addTrait(NetherRender.cutout())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_GREEN, false, BlockBehaviour.OffsetType.NONE, SoundType.CROP, true
            ))
            .randomTicks()
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Cutout: BlockNeonEquisetum extends bclib's BaseVineBlock rather than BlockBase, so it was never
    // IRenderTypeable and the old registerRenderLayers() walk never gave it a layer - its stem/leaf
    // textures have drawn their transparent pixels opaque since long before the trait migration.
    // Former Materials.netherPlant() preset, folded onto PlantBlockTrait directly (offsetType(XZ) override
    // must be baked into the trait call - see FEATHER_FERN above for why).
    public static final Block NEON_EQUISETUM = NetherBlocks.defineBlock("neon_equisetum", BlockNeonEquisetum::new)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouchOrHoeOrShears())
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.netherrack())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_GREEN, false, BlockBehaviour.OffsetType.XZ, SoundType.CROP, false
            ))
            .randomTicks()
            .lightLevel(s -> 15)
            .addTrait(NetherTraits.vine())
            .buildAndRegister();
    // Cactuses //
    // Former Materials.cactus(TERRACOTTA_ORANGE, false) preset (+ the ctor's own
    // requiresCorrectToolForDrops()/noCollission()/instabreak()/offsetType() overrides), inlined at the
    // registration site (WP3.9). R5/dead-argument flag: the ctor chained .destroyTime(0.4F) BEFORE
    // .instabreak() - instabreak() sets both destroyTime and resistance to 0, so it fully overwrote the
    // 0.4F value, making it dead; the golden line (destroyTime=0.0, instabreak=true) confirms instabreak()
    // is what actually took effect, so destroyTime(0.4F) is correctly NOT reproduced here. cactus()'s own
    // strength(0.4F) is likewise dropped for the same reason (R7).
    // Former Materials.cactus(TERRACOTTA_ORANGE, false) preset. WP: mineable-audit §E (REVIEWED): agave had
    // reqTool=true while its WOOL-sound cactus siblings (barrel_cactus/nether_cactus below) are reqTool=false
    // - agave sits in no vanilla mineable tag (only wover:mineable/shears), so reqTool=true made it
    // unharvestable. NetherMaterial.cactus()'s CactusTrait forces requiresCorrectToolForDrops()
    // unconditionally and agave was its only consumer, so - same ordering-hazard fix already applied to
    // barrel_cactus/nether_cactus - the preset is inlined here instead, dropping just that one call;
    // mapColor/randomTicks/sound/pushReaction/noOcclusion/noCollission/instabreak are reproduced unchanged.
    // The trailing offsetType(XZ) stays chained (the preset never touches offsetType, so no ordering hazard).
    public static final Block AGAVE = NetherBlocks.defineBlock("agave", BlockAgave::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherLoot.agave())
            .addTrait(NetherSurvival.gravel())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .randomTicks()
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion()
            .noCollission()
            .instabreak()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // stays inline: unlike AGAVE, this block's final chain replaces cactus()'s requiresCorrectToolForDrops/
    // noCollission/instabreak with strength(0.4F)/dynamicShape() instead - NetherMaterial.cactus()'s trait
    // forces those three unconditionally in its configure(), which always runs after any chained setter, so
    // folding would flip reqTool/hasCollision/destroyTime back to the preset's values. Category-traits
    // Batch 3 ordering hazard (proposal section 6, cactus family).
    public static final Block BARREL_CACTUS = NetherBlocks.defineBlock("barrel_cactus", BlockBarrelCactus::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherLoot.barrelCactus())
            .addTrait(NetherSurvival.gravel())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .randomTicks()
            .strength(0.4F)
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion()
            .dynamicShape()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // BlockNetherCactus (WP6.12): always dropped itself unconditionally via BlockBase's inherited getDrops()
    // override (no loot table json was generated for it), reproduced explicitly as
    // NetherLoot.dropSelfNoExplosion().
    // stays inline: same ordering hazard as BARREL_CACTUS - this block keeps strength(0.4F)/hasCollision=true
    // /reqTool=false instead of cactus()'s forced requiresCorrectToolForDrops/noCollission/instabreak.
    public static final Block NETHER_CACTUS = NetherBlocks.defineBlock("nether_cactus", BlockNetherCactus::new)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.gravel())
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .randomTicks()
            .strength(0.4F)
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Former Materials.makeNetherGrass() preset, folded onto PlantBlockTrait directly (offsetType(NONE)
    // override baked into the trait call - same recipe as WALL_MOSS/JUNGLE_MOSS's siblings in
    // NetherWallPlantBlocks).
    public static final Block JUNGLE_MOSS = NetherBlocks.defineBlock("jungle_moss", BlockPlantWall::new)
            .addTrait(NetherRender.cutout())
            .addTrait(PlantBlockTrait.withColor(
                    MapColor.COLOR_LIGHT_GREEN, false, BlockBehaviour.OffsetType.NONE, SoundType.GRASS, true
            ))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherTraits.plant())
            .buildAndRegister();
    // Soul lily //
    // stays inline: NetherMaterial.mushroomStem() forces strength(2.0F) unconditionally in its trait
    // configure(), which always runs after any chained setter - this block's final strength is 1, so
    // folding would clobber it back to 2.0F. Same ordering hazard as NetherCropBlocks' WHISPERING_GOURD.
    public static final Block SOUL_LILY = NetherBlocks.defineBlock("soul_lily", BlockSoulLily::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherLoot.soulLily())
            .addTrait(NetherRender.cutout())
            .addTrait(NetherSurvival.soulGround())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .strength(1)
            .noOcclusion()
            .randomTicks()
            .buildAndRegister();
    // Eyes //
    // stays inline: this pair once carried the netherPlant() preset too, but their ctor's own
    // sound(SLIME_BLOCK)/strength(0.5F, 0.5F) completely replace the preset's sound(CROP)/instabreak() -
    // NetherMaterial.plant()'s trait forces both unconditionally, so folding would clobber them back.
    public static final Block EYEBALL = NetherBlocks.defineBlock("eyeball", BlockEyeball::new)
            .withBlockItem((d, b) -> null)
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .mapColor(MapColor.COLOR_BROWN)
            .noOcclusion()
            .noCollission()
            .sound(SoundType.SLIME_BLOCK)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.5F, 0.5F)
            .randomTicks()
            // Was the hand-authored loot_table/blocks/eyeball.json; block loot is trait-only here.
            .addTrait(NetherLoot.eyeball())
            .buildAndRegister();
    // stays inline: same ordering hazard as EYEBALL above.
    public static final Block EYEBALL_SMALL = NetherBlocks.defineBlock("eyeball_small", BlockEyeballSmall::new)
            .withBlockItem((d, b) -> null)
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .mapColor(MapColor.COLOR_BROWN)
            .noOcclusion()
            .noCollission()
            .sound(SoundType.SLIME_BLOCK)
            .pushReaction(PushReaction.DESTROY)
            .strength(0.5F, 0.5F)
            // Was the hand-authored loot_table/blocks/eyeball_small.json; block loot is trait-only here.
            .addTrait(NetherLoot.eyeballSmall())
            .buildAndRegister();

    // Former Materials.netherPlant() preset, folded onto NetherMaterial.plant() - the trailing lightLevel
    // stays chained (untouched by the preset, no ordering hazard).
    public static final Block POTTED_PLANT = NetherBlocks.defineBlock("potted_plant", BlockPottedPlant::new)
            .withBlockItem((d, b) -> null)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(NetherMaterial.plant(MapColor.COLOR_BLACK))
            .addTrait(NetherLoot.pottedPlant())
            .lightLevel(BlockPottedPlant::getLuminance)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
