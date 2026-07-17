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
 * The wover material traits ({@code BlockTraits.STONE_BLOCK}, {@code METAL_BLOCK}, {@code OBSIDIAN_BLOCK})
 * are deliberately NOT used to reproduce the {@code BehaviourStone}/{@code BehaviourMetal} markers. Those
 * markers were empty (they contributed only the {@code #minecraft:mineable/pickaxe} tag and forced no block
 * property), whereas {@code STONE_BLOCK.withDefault()}/{@code METAL_BLOCK.withDefault()} force
 * {@code strength}, {@code instrument}, {@code requiresCorrectToolForDrops} (and, for metal, {@code sound}).
 * Property call-order is now honoured (WorldWeaver 5dcd992), so those forced values <em>can</em> be chained
 * back afterwards - but BetterNether's stone/metal blocks diverge wholesale from the material defaults
 * (netherrack 0.4, basalt 1.25/4.2, bone XYLOPHONE, cincinnasite 3/10, netherite 50/1200, and
 * {@code glowstone_stalactite} is not even {@code requiresCorrectToolForDrops}). Adopting a material trait
 * would force then immediately override every one of those, which is pure churn, and {@code METAL_BLOCK}'s
 * {@code sound(IRON)} is not captured by the {@code block_properties.txt} audit. The property-free pickaxe
 * tag below is the faithful, audit-clean reproduction of an empty classification marker.
 * <p>
 * These are methods rather than constants because the builders return {@code null} outside datagen; a
 * constant would capture that null once, at class-init.
 */
public class NetherMaterial {
    /** The old {@code BehaviourStone}: mineable with a pickaxe. */
    public static List<BlockTrait<?, ?>> stone() {
        return NetherTraits.of(BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    /** The old {@code BehaviourMetal}: mineable with a pickaxe. */
    public static List<BlockTrait<?, ?>> metal() {
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

    /** The old {@code BehaviourOre}: mineable with a pickaxe, plus {@link CommonBlockTags#ORES}. */
    public static List<BlockTrait<?, ?>> ore() {
        return NetherTraits.of(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.BLOCK_TAG.with(CommonBlockTags.ORES)
        );
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
