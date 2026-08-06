package org.betterx.betternether.registry.block;

import org.betterx.betternether.registry.item.NetherResourceItems;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.blocks.*;
import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.DescriptionBlockTrait;
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
import de.ambertation.wover.block.api.trait.behaviour.FuelBlockTrait;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.item.api.VanillaBlockItemDefinition;
import de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait;
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
import org.betterx.betternether.registry.NetherTags;

public class NetherTerrainBlocks {
    /** Vanilla's burn times, in ticks. */
    private static final int COAL_BLOCK_BURN_TICKS = 16000;
    private static final int COAL_BURN_TICKS = 1600;

    /**
     * Says the crystals burn, without saying for how long: a furnace shows the burn time itself, and a
     * line that named it here would be a second copy of the number handed to {@link FuelBlockTrait} -
     * free to drift the moment either crystal is re-tuned. Shared by both, since what a player needs off
     * the item is that it is fuel at all; which of the two burns longer is a thing they find out by
     * using them.
     */
    private static final DescriptionBlockTrait FUEL_DESCRIPTION =
            DescriptionBlockTrait.of("tooltip.betternether.fuel");


    // Terrain //
    public static final BlockTerrain NETHERRACK_MOSS = NetherBlocks.registerBlock(
            "netherrack_moss",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            // netherTerrainTags() carries the SOIL tag, so this is also plantable ground - and so pottable
            // soil, mirroring bleached_gloomsculk below.
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockNetherMycelium NETHER_MYCELIUM = NetherBlocks.registerBlock(
            "nether_mycelium",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(),
                           // As above: netherrack underneath, so netherrack's archetype.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockNetherMycelium::new,
            CommonBlockTags.MYCELIUM,
            CommonBlockTags.NETHER_MYCELIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK,
            de.ambertation.wover.tag.api.predefined.CommonBlockTags.NETHER_MYCELIUM,
            // Nether mycelium is plantable ground - small plants may grow on it.
            CommonBlockTags.SOIL
    );
    public static final BlockTerrain JUNGLE_GRASS = NetherBlocks.registerBlock(
            "jungle_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain MUSHROOM_GRASS = NetherBlocks.registerBlock(
            "mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SEPIA_MUSHROOM_GRASS = NetherBlocks.registerBlock(
            "sepia_mushroom_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    public static final BlockTerrain SWAMPLAND_GRASS = NetherBlocks.registerBlock(
            "swampland_grass",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                           PottableSoilBlockTrait.DEFAULT),
            BlockTerrain::new,
            BlockTags.NYLIUM,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // BlockFarmland (a BlockBase subclass, kept as its own class: BlocksHelper.isFertile() does an
    // `instanceof BlockFarmland` check) always dropped itself unconditionally via the inherited getDrops()
    // override - no loot table json was generated for it. Reproduced explicitly (WP6.11) as
    // NetherLoot.dropSelfNoExplosion(); the class itself moves off BlockBase onto vanilla Block.
    // Former Materials.makeNetherWood(TERRACOTTA_LIGHT_GREEN) preset, inlined at the registration site
    // (WP3.8) - no ctor overrides here, full expansion reproduced verbatim as chained setters (cleanup).
    public static final Block FARMLAND = NetherBlocks.defineBlock("farmland", BlockFarmland::new)
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTags(CommonBlockTags.SOUL_GROUND, CommonBlockTags.NETHERRACK, NetherTags.NETHER_FARMLAND)
            .buildAndRegister();
    public static final BlockTerrain CEILING_MUSHROOMS = NetherBlocks.registerBlock(
            "ceiling_mushrooms",
            Blocks.NETHERRACK,
            // #32: nylium-like ground copies netherrack (0.4); STONE_BLOCK's 2/6 would over-harden natural
            // ground (cf. BetterEnd terrain, which keeps its base end_stone hardness rather than 2/6).
            TraitLists.and(NetherMaterial.stoneTagOnly(), NetherLoot.terrain(), NetherMaterial.netherTerrainTags(),
                           // Netherrack with a cover grown on it, so it takes netherrack's archetype -
                           // vanilla does the same for crimson_nylium and warped_nylium.
                           BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy()),
            BlockTerrain::new,
            BCLBlockTags.BONEMEAL_SOURCE_NETHERRACK
    );
    // Gloomsculk - the sculk-derived ground of the gloomwood. Both variants copy sculk rather than
    // netherrack for their properties, so they take sculk's hardness and sound, and both are tagged
    // SCULK_LIKE so the plants that already accept sculk as ground accept these too. Models come from
    // NetherModels.terrainCover(): the older terrain blocks above still carry hand-authored json, but
    // there is a generator for exactly this top/side/bottom shape and new blocks should use it.
    //
    // They also carry NETHER_TERRAIN in their own right. SCULK_LIKE is not nested into NETHER_TERRAIN
    // (it is a material family, and holds vanilla sculk and decorative pieces like the geode), so a
    // sculk-like block that genuinely is ground has to say so itself - see BlockTagProvider.
    public static final BlockTerrain BLEACHED_GLOOMSCULK = NetherBlocks
            .defineBlock("bleached_gloomsculk", BlockTerrain::new)
            .replacePropertiesWithCopy(Blocks.SCULK)
            .addTrait(NetherMaterial.stoneTagOnly())
            .addTrait(NetherLoot.terrain(Blocks.SCULK))
            // Three texture variants rather than one: the glimmers are animated, and a single sprite would
            // have every block on the floor flare at the same instant. Each variant lights different pixels
            // on a different phase.
            .addTrait(NetherModels.terrainCoverVariants(
                    "bleached_gloomsculk",
                    Identifier.withDefaultNamespace("block/sculk"),
                    List.of("", "_b", "_c")
            ))
            .addTags(
                    CommonBlockTags.SCULK_LIKE,
                    CommonBlockTags.NETHER_TERRAIN,
                    // bleached gloomsculk is plantable ground - the pale flora grows on it
                    CommonBlockTags.SOIL
            )
            // Plantable ground, so it can also serve as the base soil in a flower pot -
            // matches the SOIL tag above.
            .addTrait(PottableSoilBlockTrait.DEFAULT)
            .buildAndRegister();
    // The fissures glow, so the block carries a little light of its own - not enough to light a room,
    // enough to pick the block out of an unlit gloomwood floor.
    public static final BlockMoltenGloomsculk MOLTEN_GLOOMSCULK = NetherBlocks
            .defineBlock("molten_gloomsculk", BlockMoltenGloomsculk::new)
            .replacePropertiesWithCopy(Blocks.SCULK)
            .addTrait(NetherMaterial.stoneTagOnly())
            // Same reasoning as the geode: what a sulfur cube ends up carrying is the lava in the fissures,
            // not the sculk around it.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.hot())
            .addTrait(NetherLoot.terrain(Blocks.SCULK))
            .addTrait(NetherModels.terrainCover(() -> Blocks.SCULK))
            .lightLevel(bs -> 5)
            // The same trade as the molten log: redstone worked into plain sculk.
            .addTrait(BlockTraits.RECIPE.with((key, block, context) ->
                    RecipeBuilder.crafting(key.identifier(), block)
                                 .shapeless()
                                 .addMaterial('s', Blocks.SCULK)
                                 .addMaterial('r', Items.REDSTONE)
                                 .category(RecipeCategory.BUILDING_BLOCKS)
                                 .build(context)
            ))
            .addTags(CommonBlockTags.SCULK_LIKE, CommonBlockTags.NETHER_TERRAIN)
            .buildAndRegister();

    /**
     * The layer between the gloomwood's sculk floor and the netherrack under it.
     * <p>
     * Sculk laid straight onto netherrack leaves a hard stripe wherever the terrain is cut open - a lava
     * pit, a cliff, a cave mouth - and that edge is what this block exists to break up. Built like a
     * grass block: sculk on top, netherrack underneath, and one side face carrying the whole change with
     * an interlocking edge. An even blend through the entire cube was the first attempt and read as
     * neither material.
     * <p>
     * Because the transition lives inside the block, <b>only one layer of it may ever be placed</b>.
     * Two stacked would put the netherrack bottom of the upper against the sculk top of the lower - the
     * exact seam this removes. The biome's surface rule places it at depth 1; see
     * {@link org.betterx.betternether.world.biomes.Gloomwood#surface}.
     * <p>
     * Properties come from netherrack rather than sculk: it is the rock the layer is mostly made of, and
     * a band of sculk-soft blocks under the floor would be oddly quick to dig through.
     */
    public static final BlockTerrain VEINED_GLOOMSCULK = NetherBlocks
            .defineBlock("veined_gloomsculk", BlockTerrain::new)
            .replacePropertiesWithCopy(Blocks.NETHERRACK)
            .addTrait(NetherMaterial.stoneTagOnly())
            .addTrait(NetherMaterial.netherTerrainTags())
            .addTrait(NetherLoot.terrain())
            .addTrait(NetherModels.terrainCover(() -> Blocks.NETHERRACK))
            .addTags(CommonBlockTags.SCULK_LIKE, CommonBlockTags.NETHER_TERRAIN)
            .buildAndRegister();

    /**
     * A sculk geode: a shell cracked open over a frosted cap, with lava caught inside it.
     * <p>
     * Two cubes like vanilla's slime block - an inner core seen through a translucent outer - which is
     * why it must render on the translucent layer. The frosted half of the shell is genuinely under
     * full alpha; on the cutout layer it would simply turn opaque and the core would never be seen.
     * <p>
     * Model and blockstate are hand-authored: the two-cube arrangement is a mesh, and no generator
     * expresses "one cube inside another".
     */
    public static final Block GLOOMSCULK_GEODE = NetherBlocks
            .defineBlock("gloomsculk_geode", BlockGloomsculkGeode::new)
            .replacePropertiesWithCopy(Blocks.SCULK)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.stoneTagOnly())
            // A sulfur cube that swallows one is carrying the lava too, so this is the hot archetype
            // rather than the slow_bouncy any other sculk-like shell would get. Not derivable from the
            // material: it is the lava behind the cap, which only this block knows about.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.hot())
            // Silk touch takes the geode whole; anything else lets the lava out, which the block class
            // places. Deliberately no non-silk drop - what you get for breaking it is the lava.
            .addTrait(BlockTraits.LOOT_TABLE.silkTouchSelf())
            // A silk-touched geode is a portable crystal farm, not a building block - stack it like
            // the other things vanilla does not want carried by the shulker box (eggs, ender pearls).
            .withBlockItem((d, b) -> new VanillaBlockItemDefinition(d, b).stacksTo(16))
            .lightLevel(bs -> 10)
            .noOcclusion()
            .randomTicks()
            .addTags(CommonBlockTags.SCULK_LIKE)
            .buildAndRegister();

    /**
     * The crystal a geode buds. Frost at the base shading into the lava at the tip, so it reads as the
     * frosted cap having grown outwards rather than as a separate mineral stuck on.
     * <p>
     * Unlike {@link #GLOOMSCULK_CRYSTAL} this drops normally rather than needing silk touch - the point
     * of budding is that you can farm it, and a silk-touch gate would defeat that. It burns as long as
     * a block of coal.
     */
    public static final Block GLOOMSCULK_GEODE_CRYSTAL = NetherBlocks
            .defineBlock("gloomsculk_geode_crystal", BlockGloomsculkCrystal::new)
            .replacePropertiesWithCopy(Blocks.AMETHYST_CLUSTER)
            .addTrait(NetherRender.cutout())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(FuelBlockTrait.withTicks(COAL_BLOCK_BURN_TICKS))
            .addTrait(FUEL_DESCRIPTION)
            .mapColor(MapColor.COLOR_ORANGE)
            .lightLevel(bs -> 8)
            .noOcclusion()
            .noCollission()
            .buildAndRegister();

    /**
     * A geode crafted into a lamp: the same shell, but the lava inside driven white-hot and the light
     * turned all the way up.
     */
    public static final Block GLOOMSCULK_LAMP = NetherBlocks
            .defineBlock("gloomsculk_lamp", Block::new)
            .replacePropertiesWithCopy(Blocks.SCULK)
            .addTrait(NetherRender.translucent())
            .addTrait(NetherMaterial.stoneTagOnly())
            .addTrait(NetherLoot.dropSelfNoExplosion())
            // Costs a geode to craft, so it carries the geode's stack limit with it.
            .withBlockItem((d, b) -> new VanillaBlockItemDefinition(d, b).stacksTo(16))
            .lightLevel(bs -> 15)
            .noOcclusion()
            .addTrait(BlockTraits.RECIPE.with((key, block, context) ->
                    RecipeBuilder.crafting(key.identifier(), block)
                                 .shapeless()
                                 .addMaterial('g', GLOOMSCULK_GEODE)
                                 .addMaterial('c', GLOOMSCULK_GEODE_CRYSTAL)
                                 .addMaterial('d', GLOOMSCULK_GEODE_CRYSTAL)
                                 .category(RecipeCategory.BUILDING_BLOCKS)
                                 .build(context)
            ))
            // Molten gloom content is hot, like the geode and the molten log it is made from.
            .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.hot())
            .buildAndRegister();

    /**
     * Shards grown out of a geode, on any face - so they stud the biome's floor and ceiling alike.
     * <p>
     * Silk touch or nothing, the same bargain as the geode they come from: these are the visible sign
     * that there is lava in the rock, and letting them be farmed with a pickaxe would make the geodes
     * pointless to find.
     */
    public static final Block GLOOMSCULK_CRYSTAL = NetherBlocks
            .defineBlock("gloomsculk_crystal", BlockGloomsculkCrystal::new)
            .replacePropertiesWithCopy(Blocks.AMETHYST_CLUSTER)
            .addTrait(NetherRender.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.silkTouchSelf())
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            // Burns, but a tier below the budded crystal: this one has to be silk-touched out of the
            // ground and cannot be farmed, so it is worth one coal rather than a block of it.
            .addTrait(FuelBlockTrait.withTicks(COAL_BURN_TICKS))
            .addTrait(FUEL_DESCRIPTION)
            .mapColor(MapColor.COLOR_ORANGE)
            .lightLevel(bs -> 7)
            .noOcclusion()
            .noCollission()
            .buildAndRegister();

    // The loot was the hand-authored loot_table/blocks/veined_sand.json (drops plain soul sand); block
    // loot is trait-only here.
    public static final Block VEINED_SAND = NetherBlocks.registerBlockNI(
            "veined_sand",
            Blocks.SAND,
            TraitLists.of(
                    BlockTraits.MINEABLE_WITH.needsShovel(),
                    NetherLoot.dropOther(() -> Blocks.SOUL_SAND)
            ),
            BlockVeinedSand::new,
            NetherTags.NETHER_SAND,
            CommonBlockTags.SOUL_GROUND
    );

    public static void ensureLoaded() {}
}
