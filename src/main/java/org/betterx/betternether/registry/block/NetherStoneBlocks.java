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

public class NetherStoneBlocks {

    // BNNetherBrick (a BlockBase.Stone shim) always dropped itself unconditionally via the inherited
    // getDrops() override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion().
    public static final Block NETHER_BRICK_TILE_LARGE = NetherBlocks.registerBlock(
            "nether_brick_tile_large",
            Blocks.NETHER_BRICKS,
            TraitLists.and(NetherMaterial.stone(), ModelTraitLibrary.cube(), NetherLoot.dropSelfNoExplosion()),
            Block::new
    );
    // Bricks //
    // See NETHER_BRICK_TILE_LARGE above: BNNetherBrick's forced self-drop is now explicit (WP6.11).
    public static final Block NETHER_BRICK_TILE_SMALL = NetherBlocks.registerBlock(
            "nether_brick_tile_small",
            Blocks.NETHER_BRICKS,
            TraitLists.and(NetherMaterial.stone(), ModelTraitLibrary.cube(), NetherLoot.dropSelfNoExplosion()),
            Block::new
    );
    public static final Block NETHER_BRICK_WALL = NetherBlocks.defineBlock("nether_brick_wall", WallBlock::new)
            .replacePropertiesWithCopy(NETHER_BRICK_TILE_LARGE)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.wallFrom(NETHER_BRICK_TILE_LARGE))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block NETHER_BRICK_TILE_SLAB = NetherBlocks.defineBlock("nether_brick_tile_slab", SlabBlock::new)
            .replacePropertiesWithCopy(NETHER_BRICK_TILE_SMALL)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slabColumnDouble(
                    BetterNether.C.mk("block/nether_brick_tile_small"),
                    BetterNether.C.mk("block/nether_brick_tile_slab_side")
            ))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(NETHER_BRICK_TILE_SMALL))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block NETHER_BRICK_TILE_STAIRS = NetherBlocks.defineBlock(
            "nether_brick_tile_stairs",
            p -> new StairBlock(NETHER_BRICK_TILE_SMALL.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(NETHER_BRICK_TILE_SMALL)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> NETHER_BRICK_TILE_SMALL))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(NETHER_BRICK_TILE_SMALL))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // Bone //
    // BNBoneBlock's getDrops() override always dropped the block itself, unconditionally (no loot table
    // json was ever generated for it) - reproduced explicitly (WP6.10) as NetherLoot.dropSelfNoExplosion()
    // (a plain self-drop, no survives_explosion condition, matching what the override actually did).
    public static final Block BONE_BLOCK = NetherBlocks.registerBlock(
            "bone_block",
            Blocks.BONE_BLOCK,
            TraitLists.and(NetherMaterial.stone(), NetherLoot.dropSelfNoExplosion()),
            Block::new
    );
    public static final Block BONE_STAIRS = NetherBlocks.defineBlock("bone_stairs", p -> new StairBlock(BONE_BLOCK.defaultBlockState(), p))
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> BONE_BLOCK))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(BONE_BLOCK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BONE_SLAB = NetherBlocks.defineBlock("bone_slab", SlabBlock::new)
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slabColumnDouble(
                    BetterNether.C.mk("block/bone_slab_top"),
                    BetterNether.C.mk("block/bone_slab_side")))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(BONE_BLOCK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // registerButton never dispatched through the retired material-dispatch helper (unlike registerPlate
    // below) - it just built a plain ButtonBlock with the material list the call site passed explicitly,
    // so no branch resolution is needed here: this reproduces the same (BlockTraits.BUTTON_BLOCK trait not
    // used) unclassified behaviour byte-for-byte (bone_button is in neither minecraft:buttons nor
    // wooden_buttons today).
    public static final Block BONE_BUTTON = NetherBlocks.defineBlock(
            "bone_button",
            p -> new ButtonBlock(BlockSetType.CRIMSON, 30, p)
    )
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(NetherModels.button("block/bone_button"))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.buttonFrom(BONE_BLOCK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // Dispatch-helper branch resolution (WP5.6): BlockSetType.CRIMSON.soundType() == SoundType.NETHER_WOOD
    // (neither STONE nor METAL), so the old dispatch's isMetal(CRIMSON)/isStone(CRIMSON) were both false ->
    // registerPlate's `wooden` was true -> the wooden (MINEABLE_WITH_AXE + WOODEN_PRESSURE_PLATES) branch,
    // confirmed against the committed datapack (bone_plate is in both wooden_pressure_plates.json and
    // minecraft:mineable/axe). BehaviourHelper.from() retired (WP6.14): that resolution is now the explicit
    // choice below - the dispatch was never going to change (isMetal/isStone(BlockSetType) are pure, frozen
    // functions of CRIMSON).
    public static final Block BONE_PLATE = NetherBlocks.defineBlock(
            "bone_plate",
            p -> new BasePressurePlateBlock.Wood(BONE_BLOCK, p, BlockSetType.CRIMSON)
    )
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(NetherModels.pressurePlate("block/bone_block_plate"))
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTags(BlockTags.PRESSURE_PLATES)
            .addTags(BlockTags.WOODEN_PRESSURE_PLATES)
            .addTags(BlockTags.MINEABLE_WITH_AXE)
            .addTrait(RecipeTraits.plateFrom(BONE_BLOCK))
            .buildAndRegister();
    public static final Block BONE_WALL = NetherBlocks.defineBlock("bone_wall", WallBlock::new)
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.wallFrom(BONE_BLOCK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // See BONE_BLOCK above: BNBoneBlock's forced self-drop is now explicit (WP6.10).
    public static final Block BONE_TILE = NetherBlocks.registerBlock(
            "bone_tile",
            Blocks.BONE_BLOCK,
            TraitLists.and(NetherMaterial.stone(), NetherLoot.dropSelfNoExplosion()),
            Block::new
    );
    public static final Block BONE_REED_DOOR = NetherBlocks.defineBlock("bone_reed_door", p -> new BNWoodlikeDoor(p, WoodType.CRIMSON))
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(ModelTraitLibrary.door())
            // BNWoodlikeDoor carries no Behaviour* marker, so - like the slot blocks (see the stairs above) -
            // it needs its mineable tool tag spelled out or it drops nothing. Every door built here is wood.
            .addTrait(NetherMaterial.wood())
            .buildAndRegister();
    public static final Block BONE_CINCINNASITE_DOOR = NetherBlocks.defineBlock("bone_cincinnasite_door", p -> new BNWoodlikeDoor(p, WoodType.CRIMSON))
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(ModelTraitLibrary.door())
            .addTrait(NetherMaterial.wood())
            .buildAndRegister();
    // Soul Sandstone //
    // Former registerMakeable2X2Soul/registerMakeable2X2 bundle, inlined at the registration site (WP5.12):
    // the stone classification and soul-fire/soul-speed tags the helpers baked in are now
    // NetherCompositeTraits.soulBlock() (same bundle registerSoulBlock used, WP4.9/WP5.11); the "craft 4 out
    // of a 2x2 of recipeSource" recipe the helpers queued via RecipesHelper.makeSimpleRecipe2 is reproduced
    // via simple2x2Recipe(source, count, group, category) (mirrors WP5.9's stalactiteRecipe - no BCLib
    // RecipeTraits equivalent exists for this shape).
    // BlockSoulSandstone (a BlockBase subclass) always dropped itself unconditionally via the inherited
    // getDrops() override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion(). The class itself keeps its real behaviour (the UP blockstate
    // property + updateShape), just off BlockBase now.
    public static final Block SOUL_SANDSTONE = NetherBlocks.defineBlock("soul_sandstone", BlockSoulSandstone::new)
            .replacePropertiesWithCopy(Blocks.SANDSTONE)
            .addTags(CommonBlockTags.NETHER_TERRAIN)
            .addTrait(NetherCompositeTraits.soulBlock())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.simple2x2Recipe(Blocks.SOUL_SAND, 4, "soul_sandstone", RecipeCategory.BUILDING_BLOCKS))
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_CUT = NetherBlocks.defineBlock("soul_sandstone_cut", BlockSoulSandstone::new)
            .replacePropertiesWithCopy(Blocks.SANDSTONE)
            .addTags(CommonBlockTags.NETHER_TERRAIN)
            .addTrait(NetherCompositeTraits.soulBlock())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.simple2x2Recipe(SOUL_SANDSTONE, 4, "soul_sandstone", RecipeCategory.BUILDING_BLOCKS))
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_CUT_STAIRS = NetherBlocks.defineBlock(
            "soul_sandstone_cut_stairs",
            p -> new StairBlock(SOUL_SANDSTONE_CUT.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(SOUL_SANDSTONE_CUT)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTags(BlockTags.SOUL_SPEED_BLOCKS, BlockTags.SOUL_FIRE_BASE_BLOCKS)
            .addTrait(NetherModels.soulSandstoneCutStairs())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(SOUL_SANDSTONE_CUT))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_CUT_SLAB = NetherBlocks.defineBlock("soul_sandstone_cut_slab", SlabBlock::new)
            .replacePropertiesWithCopy(SOUL_SANDSTONE_CUT)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slab(
                    () -> SOUL_SANDSTONE_CUT,
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_cut_slabs")
            ))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(SOUL_SANDSTONE_CUT))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_WALL = NetherBlocks.defineBlock("soul_sandstone_wall", WallBlock::new)
            .replacePropertiesWithCopy(SOUL_SANDSTONE_CUT)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.wallFrom(SOUL_SANDSTONE_CUT))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // Former registerSoulBlock bundle, inlined at the registration site (WP5.11): the stone classification
    // plus the two soul-fire tags, via NetherCompositeTraits.soulBlock() (WP4.9).
    // BlockBase.Stone's inherited getDrops() override always dropped the block itself unconditionally - no
    // loot table json was generated for it. Reproduced explicitly (WP6.9) as NetherLoot.dropSelfNoExplosion().
    public static final Block SOUL_SANDSTONE_SMOOTH = NetherBlocks.defineBlock("soul_sandstone_smooth", Block::new)
            .replacePropertiesWithCopy(Blocks.SANDSTONE)
            .addTrait(NetherCompositeTraits.soulBlock())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_CHISELED = NetherBlocks.defineBlock("soul_sandstone_chiseled", Block::new)
            .replacePropertiesWithCopy(Blocks.SANDSTONE)
            .addTrait(NetherCompositeTraits.soulBlock())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.simple2x2Recipe(SOUL_SANDSTONE_SMOOTH, 4, "soul_sandstone", RecipeCategory.BUILDING_BLOCKS))
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_STAIRS = NetherBlocks.defineBlock(
            "soul_sandstone_stairs",
            p -> new StairBlock(SOUL_SANDSTONE.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(SOUL_SANDSTONE)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTags(BlockTags.SOUL_SPEED_BLOCKS, BlockTags.SOUL_FIRE_BASE_BLOCKS)
            .addTrait(NetherModels.soulSandstoneStairs())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(SOUL_SANDSTONE))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_SMOOTH_STAIRS = NetherBlocks.defineBlock(
            "soul_sandstone_smooth_stairs",
            p -> new StairBlock(SOUL_SANDSTONE_SMOOTH.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(SOUL_SANDSTONE_SMOOTH)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTags(BlockTags.SOUL_SPEED_BLOCKS, BlockTags.SOUL_FIRE_BASE_BLOCKS)
            .addTrait(NetherModels.soulSandstoneSmoothStairs())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(SOUL_SANDSTONE_SMOOTH))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_SLAB = NetherBlocks.defineBlock("soul_sandstone_slab", SlabBlock::new)
            .replacePropertiesWithCopy(SOUL_SANDSTONE)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slab(
                    () -> SOUL_SANDSTONE,
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_bottom"),
                    BetterNether.C.mk("block/soul_sandstone_slabs")
            ))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(SOUL_SANDSTONE))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block SOUL_SANDSTONE_SMOOTH_SLAB = NetherBlocks.defineBlock("soul_sandstone_smooth_slab", SlabBlock::new)
            .replacePropertiesWithCopy(SOUL_SANDSTONE_SMOOTH)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slab(
                    () -> SOUL_SANDSTONE_SMOOTH,
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_top"),
                    BetterNether.C.mk("block/soul_sandstone_top")
            ))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(SOUL_SANDSTONE_SMOOTH))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // Basalt Bricks //
    // BlockBase.Stone's inherited getDrops() override always dropped the block itself unconditionally - no
    // loot table json was generated for it. Reproduced explicitly (WP6.9) as NetherLoot.dropSelfNoExplosion().
    public static final Block BASALT_BRICKS = NetherBlocks.defineBlock("basalt_bricks", Block::new)
            .replacePropertiesWithCopy(Blocks.BASALT)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherModels.basaltBricks())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.simple2x2Recipe(Blocks.POLISHED_BASALT, 4, "basalt_bricks", RecipeCategory.BUILDING_BLOCKS))
            .buildAndRegister();
    public static final Block BASALT_BRICKS_STAIRS = NetherBlocks.defineBlock(
            "basalt_bricks_stairs",
            p -> new StairBlock(BASALT_BRICKS.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(BASALT_BRICKS)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> BASALT_BRICKS))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(BASALT_BRICKS))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BASALT_BRICKS_SLAB = NetherBlocks.defineBlock("basalt_bricks_slab", SlabBlock::new)
            .replacePropertiesWithCopy(BASALT_BRICKS)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(BASALT_BRICKS))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BASALT_BRICKS_WALL = NetherBlocks.defineBlock("basalt_bricks_wall", WallBlock::new)
            .replacePropertiesWithCopy(BASALT_BRICKS)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.wallFrom(BASALT_BRICKS))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BASALT_SLAB = NetherBlocks.defineBlock("basalt_slab", SlabBlock::new)
            .replacePropertiesWithCopy(Blocks.BASALT)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(NetherModels.slab(
                    () -> Blocks.BASALT,
                    Identifier.withDefaultNamespace("block/basalt_top"),
                    Identifier.withDefaultNamespace("block/basalt_top"),
                    Identifier.withDefaultNamespace("block/basalt_side")
            ))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(Blocks.BASALT))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    // Former registerStalactite bundle, inlined at the registration site (WP5.9): BlockStalactite dropped
    // its BehaviourStone marker, so the pickaxe tool tag is restored via NetherMaterial.stone() (tag only,
    // no forced property - a source like glowstone, which is not requiresCorrectToolForDrops, is
    // unchanged). BN's BlockStalactite (not BCLib's StalactiteBlock) is kept as-is; the class-reparenting
    // question is Phase 6's. The reverse-crafting recipe (2x2 of the stalactite -> 1 source block, "nether_
    // stalactite" group) is reproduced via stalactiteRecipe(source).
    public static final Block NETHERRACK_STALACTITE = NetherBlocks.defineBlock("netherrack_stalactite", BlockStalactite::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.stalactiteRecipe(Blocks.NETHERRACK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block GLOWSTONE_STALACTITE = NetherBlocks.defineBlock("glowstone_stalactite", BlockStalactite::new)
            .replacePropertiesWithCopy(Blocks.GLOWSTONE)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .strength(0.3F, 0.3F)
            .addTrait(NetherRecipeTraits.stalactiteRecipe(Blocks.GLOWSTONE))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BLACKSTONE_STALACTITE = NetherBlocks.defineBlock("blackstone_stalactite", BlockStalactite::new)
            .replacePropertiesWithCopy(Blocks.BLACKSTONE)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.stalactiteRecipe(Blocks.BLACKSTONE))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BASALT_STALACTITE = NetherBlocks.defineBlock("basalt_stalactite", BlockStalactite::new)
            .replacePropertiesWithCopy(Blocks.BASALT)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.stalactiteRecipe(Blocks.BASALT))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block BONE_STALACTITE = NetherBlocks.defineBlock("bone_stalactite", BlockStalactite::new)
            .replacePropertiesWithCopy(BONE_BLOCK)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(NetherRecipeTraits.stalactiteRecipe(BONE_BLOCK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();

    public static final Block NETHERRACK_SLAB = NetherBlocks.defineBlock("netherrack_slab", SlabBlock::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> Blocks.NETHERRACK))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(Blocks.NETHERRACK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block NETHERRACK_STAIR = NetherBlocks.defineBlock(
            "netherrack_stairs",
            p -> new StairBlock(Blocks.NETHERRACK.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> Blocks.NETHERRACK))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(Blocks.NETHERRACK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();
    public static final Block NETHERRACK_WALLS = NetherBlocks.defineBlock("netherrack_wall", WallBlock::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(BlockTraits.WALL_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.wall(() -> Blocks.NETHERRACK))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.wallFrom(Blocks.NETHERRACK))
            // Not a full cube, so no sulfur cube archetype - vanilla tags no block of this shape.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
