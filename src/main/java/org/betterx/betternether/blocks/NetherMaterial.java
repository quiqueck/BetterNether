package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.PlantBlockTrait;
import org.betterx.betternether.BetterNether;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The material-derived tags a block used to inherit from bclib's {@code Behaviour*} marker interfaces, as
 * explicit traits to hand to a block definition at registration.
 * <p>
 * Each method reproduces exactly what its marker contributed through bclib's {@code BCLAutoBlockTagProvider}
 * - the tool tag and, where the marker had one, its extra block tags - and nothing else.
 * <p>
 * The stone/metal blocks now adopt the full wover material classification:
 * {@link #stone()} = {@code BlockTraits.STONE_BLOCK.withDefault()} and {@link #metal()} =
 * {@code BlockTraits.METAL_BLOCK.withDefault()}. {@code STONE_BLOCK} forces {@code instrument(BASEDRUM)},
 * {@code requiresCorrectToolForDrops}, {@code strength(2,6)} and the pickaxe tag (no sound - the copied
 * source's sound is kept); {@code METAL_BLOCK} forces {@code instrument(IRON_XYLOPHONE)},
 * {@code requiresCorrectToolForDrops}, {@code strength(5,6)}, {@code sound(METAL)} and the pickaxe tag.
 * <p>
 * Property call-order is honoured (WorldWeaver 5dcd992): every {@code register*} helper applies
 * {@code replacePropertiesWithCopy(source)} (eager, during the chain) BEFORE adding the material trait, so
 * the material's forced values win over the copied source while the source's {@code mapColor}/{@code sound}
 * survive. A block whose constructor sets its own strength/sound/mapColor (via {@code Materials.*}) still
 * wins over the trait, because the block factory runs last in {@code BlockDefinition.build()} - for those
 * blocks the material trait only contributes the classification and the pickaxe tag.
 * <p>
 * A few blocks keep the property-free pickaxe tag ({@link #stoneTagOnly()}/{@link #metalTagOnly()}) instead
 * of the full material default, because the material strength would nerf an intentionally tough block; they
 * are listed for task #32: the eight obsidian brick/tile stairs and slabs (obsidian 50/1200) and the two
 * netherite fire bowls (netherite 50/1200).
 * <p>
 * These are methods rather than constants because the builders return {@code null} outside datagen; a
 * constant would capture that null once, at class-init.
 */
public class NetherMaterial {
    /** The stone classification: {@code STONE_BLOCK.withDefault()} (instrument BASEDRUM, strength 2/6, reqTool, pickaxe). */
    public static List<BlockTrait<?, ?>> stone() {
        return BlockTraits.STONE_BLOCK.withDefault();
    }

    /** The metal classification: {@code METAL_BLOCK.withDefault()} (instrument IRON_XYLOPHONE, strength 5/6, reqTool, sound METAL, pickaxe). */
    public static List<BlockTrait<?, ?>> metal() {
        return BlockTraits.METAL_BLOCK.withDefault();
    }

    /**
     * Cincinnasite: METAL_BLOCK classification. Base BlockCincinnasite cubes get their own 3.0F/10.0F
     * strength and {@code SoundType.IRON} sound from their constructor; this is for the copy-based
     * decorative variants whose factory does not set strength, where METAL_BLOCK's default 5/6 (and generic
     * METAL sound) would otherwise apply.
     * <p>
     * WP8.3 (REVIEWED): the family sound was aligned to {@code SoundType.IRON} - METAL_BLOCK's generic
     * {@code METAL} sound made the whole cincinnasite family read slightly different from vanilla iron
     * (iron_block/iron_bars/iron_door use {@code IRON}); the vanilla-alignment review recommended aligning
     * since the family's instrument (IRON_XYLOPHONE, from METAL_BLOCK) already matches iron.
     * <p>
     * Cleanup: this used to bundle the strength/sound override as {@code NetherProps} traits (call order:
     * the override ran after METAL_BLOCK). {@code NetherProps} is dissolved, so {@code cincinnasite()} is
     * now just {@link #metal()} - every call site chains its own {@code .strength(3.0F, 10.0F)}
     * {@code .sound(SoundType.IRON)} explicitly, right after {@code .addTrait(NetherMaterial.cincinnasite())},
     * the same call-order position the NetherProps traits used to occupy.
     */
    public static List<BlockTrait<?, ?>> cincinnasite() {
        return metal();
    }

    /**
     * The old pickaxe-tag-only compensation (no forced property), for a stone block whose full material
     * strength would be a regression. Deferred to task #32 - the obsidian brick/tile stairs and slabs.
     */
    public static List<BlockTrait<?, ?>> stoneTagOnly() {
        return TraitLists.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }


    /**
     * The old pickaxe-tag-only compensation (no forced property), for a metal block whose full material
     * strength would be a regression. Deferred to task #32 - the netherite fire bowls.
     */
    public static List<BlockTrait<?, ?>> metalTagOnly() {
        return TraitLists.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /** The old {@code BehaviourGlass}: mineable with a pickaxe. */
    public static List<BlockTrait<?, ?>> glass() {
        return TraitLists.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /** The old {@code BehaviourWood}: mineable with an axe. */
    public static List<BlockTrait<?, ?>> wood() {
        return TraitLists.of(BlockTraits.MINEABLE_WITH.needsAxe());
    }

    /** The old {@code BehaviourSand}: mineable with a shovel. */
    public static List<BlockTrait<?, ?>> sand() {
        return TraitLists.of(BlockTraits.MINEABLE_WITH.needsShovel());
    }

    /**
     * A nether ore: {@code BlockTraits.ORE_BLOCK.dropping(drop, min, max)} (instrument BASEDRUM, strength 3/9,
     * reqTool, sound STONE, pickaxe tag, {@code c:ores}, and the vanilla ore-drop loot trait) plus the
     * netherrack ore block tags {@link CommonBlockTags#NETHERRACK} and {@link CommonBlockTags#NETHER_ORES}
     * (which the {@code BlockOre}/{@code RedstoneOreBlock} classes used to add through the retired
     * {@code BlockTagProvider} interface). The forced properties are still overridden by the ore block's own
     * constructor (Materials.stone(...).strength(3,5).sound(NETHERRACK)), which runs last.
     */
    public static List<BlockTrait<?, ?>> ore(Supplier<Item> drop, int min, int max) {
        return TraitLists.and(
                BlockTraits.ORE_BLOCK.dropping(drop, min, max),
                BlockTraits.BLOCK_TAG.with(List.of(
                        CommonBlockTags.NETHERRACK,
                        CommonBlockTags.NETHER_ORES
                ))
        );
    }

    /**
     * The netherrack terrain tags a {@code BlockTerrain} used to add through the retired
     * {@code BlockTagProvider} interface: {@link CommonBlockTags#NETHERRACK},
     * {@link CommonBlockTags#NETHER_STONES} and - since nylium-like nether terrain is plantable ground -
     * {@link CommonBlockTags#SOIL}. Returns a single trait (null off-datagen) so it can be dropped straight
     * into a {@link NetherTraits#and(List, BlockTrait[])} call alongside the block's other traits.
     */
    public static BlockTrait<?, ?> netherTerrainTags() {
        return BlockTraits.BLOCK_TAG.with(List.of(
                CommonBlockTags.NETHERRACK,
                CommonBlockTags.NETHER_STONES,
                CommonBlockTags.SOIL
        ));
    }

    /** The old {@code BehaviourImmobile}: {@link CommonBlockTags#IMMOBILE} + {@link BlockTags#DRAGON_IMMUNE}. */
    public static List<BlockTrait<?, ?>> immobile() {
        return TraitLists.of(BlockTraits.BLOCK_TAG.with(List.of(
                CommonBlockTags.IMMOBILE,
                BlockTags.DRAGON_IMMUNE
        )));
    }


    /** The old {@code BehaviourPortalFrame}: {@link CommonBlockTags#NETHER_PORTAL_FRAME}. */
    public static List<BlockTrait<?, ?>> portalFrame() {
        return TraitLists.of(BlockTraits.BLOCK_TAG.with(CommonBlockTags.NETHER_PORTAL_FRAME));
    }

    /**
     * The one call every obsidian-derived block makes: a pickaxe, {@link CommonBlockTags#IS_OBSIDIAN},
     * {@link BlockTags#NEEDS_DIAMOND_TOOL} and - from the old {@code BehaviourImmobile} parent -
     * {@link CommonBlockTags#IMMOBILE} and {@link BlockTags#DRAGON_IMMUNE}.
     * <p>
     * This is the single source of truth for "this block is obsidian". Downstream data keys off
     * {@link CommonBlockTags#IS_OBSIDIAN} rather than enumerating blocks - the Obsidian Breaker
     * enchantment's mineable tag, for one - so a block that is obsidian but does not route through here
     * silently falls out of all of it. Anything carved from obsidian (bricks, tiles, stairs, slabs,
     * glass, ...) must call this or {@link #obsidianPortalFrame()}; if a variant needs different tags on
     * top, compose them here rather than hand-rolling a parallel list.
     */
    public static List<BlockTrait<?, ?>> obsidian() {
        return obsidianWith(List.of());
    }

    /** {@link #obsidian()} plus {@link CommonBlockTags#NETHER_PORTAL_FRAME}. */
    public static List<BlockTrait<?, ?>> obsidianPortalFrame() {
        return obsidianWith(List.of(CommonBlockTags.NETHER_PORTAL_FRAME));
    }

    private static List<BlockTrait<?, ?>> obsidianWith(List<TagKey<Block>> extraTags) {
        final List<TagKey<Block>> tags = new ArrayList<>(List.of(
                CommonBlockTags.IS_OBSIDIAN,
                CommonBlockTags.IMMOBILE,
                BlockTags.DRAGON_IMMUNE,
                BlockTags.NEEDS_DIAMOND_TOOL
        ));
        tags.addAll(extraTags);

        return TraitLists.of(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.BLOCK_TAG.with(tags)
        );
    }

    // --- Plant-family property presets (category-traits Batch 2) -----------------------------------
    //
    // The former Materials.netherPlant()/makeNetherGrass()/netherSapling()/staticVine()/cactus()/
    // makeNetherWood() presets, reconstructed from the inline property chains at BN's ~62 plant-family
    // registration sites (see the category-traits proposal, section 3). Two shapes, per the plan's
    // requirement 8 (shared home) vs risk trade-off:
    //  - plant()/grass()/sapling() map cleanly onto BCLib's PlantBlockTrait (Shape A: the shared trait,
    //    extended in Batch 0 with a SoundType + validSpawnAlways overload) - no BN-only property class
    //    needed for those.
    //  - staticVine()/cactus()/mushroomStem() use nether-only sounds/behaviour that don't generalize
    //    (VINE/WOOL sounds, wood-strength note-block instrument) and, critically, do NOT force
    //    instabreak() the way PlantBlockTrait always does - composing them from PlantBlockTrait would
    //    clobber the several vines/cactuses that chain their own strength() after the preset. These stay
    //    Shape B: small mod-local property-only traits, the same "named bundle carrying property-ops"
    //    pattern as the stone/metal/ore bundles above, not a revival of the deleted generic NetherProps.

    /**
     * Former {@code Materials.netherPlant()} preset (Shape A): BCLib {@link PlantBlockTrait}'s core with
     * the nether-specific {@link SoundType#CROP} sound and no XZ offset - {@code mapColor(color)},
     * {@code noOcclusion}, {@code noCollission}, {@code instabreak}, {@code sound(CROP)},
     * {@code pushReaction(DESTROY)}. Per-block extras (`randomTicks`, `offsetType(XZ)`) stay chained.
     */
    public static List<BlockTrait<?, ?>> plant(MapColor color) {
        return TraitLists.of(PlantBlockTrait.withColor(
                color, false, BlockBehaviour.OffsetType.NONE, SoundType.CROP, false
        ));
    }

    /**
     * Former {@code Materials.makeNetherGrass(color)} preset (Shape A): BCLib {@link PlantBlockTrait}'s core
     * with the default {@link SoundType#GRASS} sound, the XZ ground-offset, and the preset's own forced
     * {@code isValidSpawn(-> true)} (every mob may spawn on nether grass, matching the preset's original
     * intent - not part of vanilla {@code PlantBlockTrait} before Batch 0's {@code validSpawnAlways} flag).
     */
    public static List<BlockTrait<?, ?>> grass(MapColor color) {
        return TraitLists.of(PlantBlockTrait.withColor(
                color, false, BlockBehaviour.OffsetType.XZ, SoundType.GRASS, true
        ));
    }

    /**
     * Former {@code Materials.netherSapling()} preset (Shape A): {@link #plant(MapColor)}'s core plus the
     * preset's own unconditional {@code randomTicks()} (every one of the 8 former call sites had it, unlike
     * {@code netherPlant()} where random ticks was a per-block addition).
     */
    public static List<BlockTrait<?, ?>> sapling(MapColor color) {
        return TraitLists.and(plant(color), RandomTicksTrait.INSTANCE);
    }

    /**
     * Former {@code Materials.staticVine(color)} preset (Shape B): {@code mapColor(color)},
     * {@code noOcclusion}, {@code noCollission}, {@code sound(VINE)}, {@code pushReaction(DESTROY)},
     * {@code replaceable}. Deliberately carries <b>no</b> {@code instabreak} - unlike
     * {@link PlantBlockTrait}, which always forces one - because several vines chain their own
     * {@code instabreak()}/{@code strength(0.2f)} <em>after</em> this preset and must still win.
     */
    public static List<BlockTrait<?, ?>> staticVine(MapColor color) {
        return TraitLists.of(new StaticVineTrait(color));
    }

    /**
     * Former {@code Materials.cactus(color)} preset (Shape B), the preset's common subset (see the
     * category-traits proposal section 6 for the per-block deviations that must stay inline/trailing):
     * {@code mapColor(color)}, {@code randomTicks}, {@code sound(WOOL)}, {@code pushReaction(DESTROY)},
     * {@code noOcclusion}, {@code requiresCorrectToolForDrops}, {@code noCollission}, {@code instabreak}.
     */
    public static List<BlockTrait<?, ?>> cactus(MapColor color) {
        return TraitLists.of(new CactusTrait(color));
    }

    /**
     * Former {@code Materials.makeNetherWood(color)} preset (Shape B), used by the mushroom-stem family
     * (large mushrooms, smoker, giant mold, ...): {@code mapColor(color)},
     * {@code instrument(NoteBlockInstrument.BASS)}, {@code strength(2.0F)}, {@code sound(WOOD)},
     * {@code requiresCorrectToolForDrops}.
     */
    public static List<BlockTrait<?, ?>> mushroomStem(MapColor color) {
        return TraitLists.of(new MushroomStemTrait(color));
    }

    /** Adds {@code randomTicks()} and nothing else - shared by {@link #sapling(MapColor)} and {@link #cactus(MapColor)}. */
    private static final class RandomTicksTrait extends BlockTraitImpl.Generic {
        private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterNether.C, "material_random_ticks");
        private static final RandomTicksTrait INSTANCE = new RandomTicksTrait();

        @Override
        public BlockTraitKey key() {
            return KEY;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            super.configure(definition);
            definition.randomTicks();
        }
    }

    /** The {@link #staticVine(MapColor)} property core - see that method's javadoc for the exact setter list. */
    private static final class StaticVineTrait extends BlockTraitImpl.Generic {
        private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterNether.C, "material_static_vine");
        private final MapColor color;

        private StaticVineTrait(MapColor color) {
            this.color = color;
        }

        @Override
        public BlockTraitKey key() {
            return KEY;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            super.configure(definition);
            definition.mapColor(color)
                      .noOcclusion()
                      .noCollission()
                      .sound(SoundType.VINE)
                      .pushReaction(PushReaction.DESTROY)
                      .replaceable();
        }
    }

    /** The {@link #cactus(MapColor)} property core - see that method's javadoc for the exact setter list. */
    private static final class CactusTrait extends BlockTraitImpl.Generic {
        private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterNether.C, "material_cactus");
        private final MapColor color;

        private CactusTrait(MapColor color) {
            this.color = color;
        }

        @Override
        public BlockTraitKey key() {
            return KEY;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            super.configure(definition);
            definition.mapColor(color)
                      .randomTicks()
                      .sound(SoundType.WOOL)
                      .pushReaction(PushReaction.DESTROY)
                      .noOcclusion()
                      .requiresCorrectToolForDrops()
                      .noCollission()
                      .instabreak();
        }
    }

    /** The {@link #mushroomStem(MapColor)} property core - see that method's javadoc for the exact setter list. */
    private static final class MushroomStemTrait extends BlockTraitImpl.Generic {
        private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterNether.C, "material_mushroom_stem");
        private final MapColor color;

        private MushroomStemTrait(MapColor color) {
            this.color = color;
        }

        @Override
        public BlockTraitKey key() {
            return KEY;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            super.configure(definition);
            definition.mapColor(color)
                      .instrument(NoteBlockInstrument.BASS)
                      .strength(2.0F)
                      .sound(SoundType.WOOD)
                      .requiresCorrectToolForDrops();
        }
    }
}
