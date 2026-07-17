package org.betterx.betternether.blocks;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;

import java.util.List;

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
     * Cincinnasite: METAL_BLOCK classification, but weaker to mine (3) and more blast-resistant (10) than
     * the metal default 5/6 - the strength override runs after the trait (call order). Base BlockCincinnasite
     * cubes get the same 3/10 from their constructor; this is for the copy-based decorative variants whose
     * factory does not set strength, where METAL_BLOCK would otherwise drive them to 5/6.
     */
    public static List<BlockTrait<?, ?>> cincinnasite() {
        return NetherTraits.and(metal(), NetherProps.strength(3.0F, 10.0F));
    }

    /**
     * The old pickaxe-tag-only compensation (no forced property), for a stone block whose full material
     * strength would be a regression. Deferred to task #32 - the obsidian brick/tile stairs and slabs.
     */
    public static List<BlockTrait<?, ?>> stoneTagOnly() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /**
     * The old pickaxe-tag-only compensation (no forced property), for a metal block whose full material
     * strength would be a regression. Deferred to task #32 - the netherite fire bowls.
     */
    public static List<BlockTrait<?, ?>> metalTagOnly() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /** The old {@code BehaviourGlass}: mineable with a pickaxe. */
    public static List<BlockTrait<?, ?>> glass() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /** The old {@code BehaviourWood}: mineable with an axe. */
    public static List<BlockTrait<?, ?>> wood() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsAxe());
    }

    /** The old {@code BehaviourSand}: mineable with a shovel. */
    public static List<BlockTrait<?, ?>> sand() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsShovel());
    }

    /**
     * The ore classification: {@code ORE_BLOCK.withDefault()} (instrument BASEDRUM, strength 3/9, reqTool,
     * sound STONE, pickaxe tag and {@link CommonBlockTags#ORES}). Reproduces the old {@code BehaviourOre}
     * marker's tags (pickaxe + c:ores); its forced properties are overridden by the ore block's own
     * constructor ({@code Materials.stone(...).strength(3,5).sound(NETHERRACK)}), which runs last.
     */
    public static List<BlockTrait<?, ?>> ore() {
        return BlockTraits.ORE_BLOCK.withDefault();
    }

    /** The old {@code BehaviourImmobile}: {@link CommonBlockTags#IMMOBILE} + {@link BlockTags#DRAGON_IMMUNE}. */
    public static List<BlockTrait<?, ?>> immobile() {
        return NetherTraits.of(BlockTraits.BLOCK_TAG.with(List.of(
                CommonBlockTags.IMMOBILE,
                BlockTags.DRAGON_IMMUNE
        )));
    }

    /**
     * {@code BlockObsidianGlass}' old {@code BehaviourImmobile, BehaviourPortalFrame, BehaviourGlass}: a
     * pickaxe plus {@link CommonBlockTags#IMMOBILE}, {@link BlockTags#DRAGON_IMMUNE} and
     * {@link CommonBlockTags#NETHER_PORTAL_FRAME}. Despite the name it was never {@code BehaviourObsidian},
     * so it gets neither {@link CommonBlockTags#IS_OBSIDIAN} nor {@link BlockTags#NEEDS_DIAMOND_TOOL}.
     */
    public static List<BlockTrait<?, ?>> obsidianGlass() {
        return NetherTraits.of(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.BLOCK_TAG.with(List.of(
                        CommonBlockTags.IMMOBILE,
                        BlockTags.DRAGON_IMMUNE,
                        CommonBlockTags.NETHER_PORTAL_FRAME
                ))
        );
    }

    /** The old {@code BehaviourPortalFrame}: {@link CommonBlockTags#NETHER_PORTAL_FRAME}. */
    public static List<BlockTrait<?, ?>> portalFrame() {
        return NetherTraits.of(BlockTraits.BLOCK_TAG.with(CommonBlockTags.NETHER_PORTAL_FRAME));
    }

    /**
     * The old {@code BehaviourObsidian}: a pickaxe, {@link CommonBlockTags#IS_OBSIDIAN},
     * {@link BlockTags#NEEDS_DIAMOND_TOOL} and - through its {@code BehaviourImmobile} parent -
     * {@link CommonBlockTags#IMMOBILE} and {@link BlockTags#DRAGON_IMMUNE}.
     */
    public static List<BlockTrait<?, ?>> obsidian() {
        return NetherTraits.of(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.BLOCK_TAG.with(List.of(
                        CommonBlockTags.IS_OBSIDIAN,
                        CommonBlockTags.IMMOBILE,
                        BlockTags.DRAGON_IMMUNE,
                        BlockTags.NEEDS_DIAMOND_TOOL
                ))
        );
    }

    /** The old {@code BehaviourObsidianPortalFrame}: {@link #obsidian()} plus {@link #portalFrame()}. */
    public static List<BlockTrait<?, ?>> obsidianPortalFrame() {
        return NetherTraits.of(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.BLOCK_TAG.with(List.of(
                        CommonBlockTags.IS_OBSIDIAN,
                        CommonBlockTags.IMMOBILE,
                        BlockTags.DRAGON_IMMUNE,
                        BlockTags.NEEDS_DIAMOND_TOOL,
                        CommonBlockTags.NETHER_PORTAL_FRAME
                ))
        );
    }
}
