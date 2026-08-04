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

public class NetherDecorBlocks {

    // BlockCincinnasiteFrame (WP6.12): noOcclusion() moved out of its constructor (R1) and now comes from
    // GlassBlockTrait; same dropSelfNoExplosion() reproduction as the pedestal above.
    // The frame is a genuine see-through full-collision cube - cincinnasite_panel.png carries a tRNS chunk
    // with ten fully transparent palette entries, and the class has the glass rendering trio (shade
    // brightness 1.0, propagatesSkylightDown, skipRendering against itself) - so mobs spawning on it read
    // as the same oversight the glass blocks had. Its deleted BlockBaseNotFull base declared
    // allowsSpawning=false, which never ran (no @Override, no matching BlockBehaviour method) but records
    // the original intent. Conduction is kept on purpose: this is a solid metal lattice, not glass, so it
    // takes seeThroughConductor() rather than glass(). The no-resistance overload keeps strength(3, 10).
    public static final Block CINCINNASITE_FRAME = NetherBlocks.defineBlock("cincinnasite_frame", BlockCincinnasiteFrame::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(GlassBlockTrait.seeThroughConductor())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BlockBNPot (WP6.12): noOcclusion() moved out of its constructor (R1); both always dropped themselves
    // unconditionally via BlockBase's inherited getDrops() override (no loot table json was generated for
    // either), reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    public static final Block CINCINNASITE_POT = NetherBlocks.defineBlock("cincinnasite_pot", p -> new BlockBNPot.Metal(p))
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_BLOCK)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    public static final Block BRICK_POT = NetherBlocks.defineBlock("brick_pot", p -> new BlockBNPot.Stone(p))
            .replacePropertiesWithCopy(Blocks.NETHER_BRICKS)
            .addTrait(NetherMaterial.stone())
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // BlockGeyser (WP6.12): lightLevel(10)/noOcclusion() moved out of its constructor (R1); it always
    // dropped itself unconditionally via BlockBase's inherited getDrops() override (no loot table json was
    // generated for it), reproduced explicitly as NetherLoot.dropSelfNoExplosion().
    public static final Block GEYSER = NetherBlocks.defineBlock("geyser", BlockGeyser::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(NetherMaterial.stone())
            .lightLevel(state -> 10)
            .noOcclusion()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .buildAndRegister();
    // Roofs //
    // Former registerRoof bundle, inlined at the registration site (WP5.11): self-drop loot plus the
    // roof-tile recipe, via NetherCompositeTraits.roof(source) (WP4.9). The old addFuel(source, roof) side
    // effect is dropped here (and at ROOF_TILE_CINCINNASITE below): both nether_bricks and
    // cincinnasite_forged have ignitedByLava=false in the committed golden, so the guard was always false -
    // no fuel was ever actually registered for either roof.
    public static final Block ROOF_TILE_NETHER_BRICKS = NetherBlocks.defineBlock("roof_tile_nether_bricks", Block::new)
            .replacePropertiesWithCopy(Blocks.NETHER_BRICKS)
            .addTrait(NetherMaterial.stone())
            .addTrait(NetherCompositeTraits.roof(Blocks.NETHER_BRICKS))
            .buildAndRegister();
    public static final Block ROOF_TILE_NETHER_BRICKS_STAIRS = NetherBlocks.defineBlock(
            "roof_tile_nether_bricks_stairs",
            p -> new StairBlock(ROOF_TILE_NETHER_BRICKS.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(ROOF_TILE_NETHER_BRICKS)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> ROOF_TILE_NETHER_BRICKS))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.stairsFrom(ROOF_TILE_NETHER_BRICKS))
            .buildAndRegister();
    public static final Block ROOF_TILE_NETHER_BRICKS_SLAB = NetherBlocks.defineBlock("roof_tile_nether_bricks_slab", SlabBlock::new)
            .replacePropertiesWithCopy(ROOF_TILE_NETHER_BRICKS)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> ROOF_TILE_NETHER_BRICKS))
            .addTrait(NetherMaterial.stone())
            .addTrait(RecipeTraits.slabFrom(ROOF_TILE_NETHER_BRICKS))
            .buildAndRegister();
    public static final Block ROOF_TILE_CINCINNASITE = NetherBlocks.defineBlock("roof_tile_cincinnasite", Block::new)
            .replacePropertiesWithCopy(NetherMetalBlocks.CINCINNASITE_FORGED)
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(NetherCompositeTraits.roof(NetherMetalBlocks.CINCINNASITE_FORGED))
            .buildAndRegister();
    public static final Block ROOF_TILE_CINCINNASITE_STAIRS = NetherBlocks.defineBlock(
            "roof_tile_cincinnasite_stairs",
            p -> new StairBlock(ROOF_TILE_CINCINNASITE.defaultBlockState(), p)
    )
            .replacePropertiesWithCopy(ROOF_TILE_CINCINNASITE)
            .addTrait(BlockTraits.STAIR_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.stairs(() -> ROOF_TILE_CINCINNASITE))
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(RecipeTraits.stairsFrom(ROOF_TILE_CINCINNASITE))
            .buildAndRegister();
    public static final Block ROOF_TILE_CINCINNASITE_SLAB = NetherBlocks.defineBlock("roof_tile_cincinnasite_slab", SlabBlock::new)
            .replacePropertiesWithCopy(ROOF_TILE_CINCINNASITE)
            .addTrait(BlockTraits.SLAB_BLOCK.withDefault())
            .addTrait(ModelTraitLibrary.slab(() -> ROOF_TILE_CINCINNASITE))
            .addTrait(NetherMaterial.cincinnasite())
            .strength(3.0F, 10.0F)
            .sound(SoundType.IRON)
            .addTrait(RecipeTraits.slabFrom(ROOF_TILE_CINCINNASITE))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
