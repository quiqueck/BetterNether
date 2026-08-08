package org.betterx.betternether.blocks;

import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherTags;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * The ground a plant can be placed on, as {@link SurvivesOnBlockTrait}s to hand to a block's definition at
 * registration. Replaces the old {@code SurvivesOn*} interface hierarchy: the rule now lives on the block
 * definition rather than in the class hierarchy, and {@link SurvivesOnBlockTrait#survivesOn} reads it back.
 * <p>
 * A block may carry several of these - {@code survivesOn} accepts the ground if ANY attached trait does - so
 * a rule spanning multiple tags is just a list, with no aggregate tag needed.
 * <p>
 * These are methods rather than constants on purpose: the block-based rules reference BetterNether's own
 * blocks, and a constant would capture them during {@link NetherBlocks}' static init, before they are
 * assigned. wover caches trait instances per tag/block-list, so calling these repeatedly is free.
 */
public class NetherSurvival {
    /**
     * Nether ground in the broad sense - the old {@code SurvivesOnNetherGround}, which tested
     * {@code BlocksHelper.isNetherGround(state) || RED_SAND || SAND}, with isNetherGround expanding to
     * NETHER_STONES, SOUL_GROUND (isSoulSand), NETHER_MYCELIUM and NYLIUM.
     * <p>
     * This is the trait-side mirror of {@link org.betterx.betternether.BlocksHelper#isNetherGround}: whatever
     * counts as ground for a feature counts as ground for a plant, so the two lists are kept identical.
     * <p>
     * SCULK_LIKE is deliberately not part of it. This mirrors what counts as <em>terrain</em>, and sculk is
     * a material family rather than a terrain one - it holds vanilla sculk and decorative pieces such as the
     * gloomsculk geode. Vegetation wants a wider rule than terrain does and takes
     * {@link #netherGroundAndSculk()} instead; this one is the terrain half of it.
     */
    public static List<BlockTrait<?, ?>> netherGround() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_STONES),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_MYCELIUM),
                SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM),
                SurvivesOnBlockTrait.withBlocks(Blocks.RED_SAND, Blocks.SAND)
        );
    }

    /**
     * The gloomwood's floor: the gloomsculk blocks and the vanilla sculk they are derived from.
     * <p>
     * Meant to be combined with another rule rather than used alone - {@code survivesOn} accepts the ground
     * if any attached trait does, so {@code TraitLists.concat(netherGround(), sculkLike())} reads as
     * "ordinary nether ground, or the sculk floor". That particular pair is {@link #netherGroundAndSculk()}.
     */
    public static List<BlockTrait<?, ?>> sculkLike() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.SCULK_LIKE));
    }

    /**
     * What the gloomwood's sapling grows on: {@link #netherGround()} plus {@link #sculkLike()}.
     * <p>
     * Sculk is not nether terrain - it is out of {@code NETHER_TERRAIN} and out of
     * {@link org.betterx.betternether.BlocksHelper#isNetherGround}, so nothing that shapes the world treats
     * the gloomwood's floor as ground to build on. A plant native to that floor is the exception: it merely
     * stands on the stuff, and it has to accept both the floor it grew up on and ordinary nether ground for
     * a player who digs one up and replants it elsewhere.
     * <p>
     * Deliberately only the gloomwood species. The nether's other vegetation keeps the rule it had -
     * a swamp grass gains nothing from being plantable in a biome it never appears in, and every plant
     * accepting every floor is how ground rules stop meaning anything.
     * <p>
     * The gloomwood's ground cover - the two gloomgrasses and the gloomwisp - has since moved off this
     * rule onto {@link org.betterx.bclib.trait.block.SurvivesOnSolidTrait}: they are decoration a player
     * places anywhere, and their worldgen is pinned to the sculk floor by the placed feature rather than
     * by what the block will tolerate. A tree is not, so the sapling keeps a real ground list.
     */
    public static List<BlockTrait<?, ?>> netherGroundAndSculk() {
        return TraitLists.concat(netherGround(), sculkLike());
    }

    public static List<BlockTrait<?, ?>> netherrack() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHERRACK));
    }

    public static List<BlockTrait<?, ?>> netherrackNyliumAndSculk() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHERRACK),
                SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SCULK_LIKE)
        );
    }

    public static List<BlockTrait<?, ?>> nylium() {
        return List.of(SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM));
    }

    public static List<BlockTrait<?, ?>> netherMycelium() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_MYCELIUM));
    }

    public static List<BlockTrait<?, ?>> netherSand() {
        return List.of(SurvivesOnBlockTrait.withTag(NetherTags.NETHER_SAND));
    }

    public static List<BlockTrait<?, ?>> soulGround() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND));
    }

    public static List<BlockTrait<?, ?>> soulGroundOrFarmland() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND),
                SurvivesOnBlockTrait.withTag(NetherTags.NETHER_FARMLAND)
        );
    }

    public static List<BlockTrait<?, ?>> soilOrLogs() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOIL_OR_LOGS));
    }

    public static List<BlockTrait<?, ?>> soulSand() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.SOUL_SAND));
    }

    public static List<BlockTrait<?, ?>> gravel() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.GRAVEL));
    }

    /**
     * The magma flower's ground: hot rock and sand, plus the whole sculk family.
     * <p>
     * Vanilla sculk was already on the list on its own; widening that one entry to
     * {@link CommonBlockTags#SCULK_LIKE} is what lets the flower stand on the gloomwood's own floor
     * blocks - most of all on molten gloomsculk, which is sculk with lava in its fissures and about the
     * most obvious ground a magma flower could ask for. The mega lava lake's shore is built out of those
     * blocks and needs a warm plant that will actually live on them.
     */
    public static List<BlockTrait<?, ?>> magmaSandOrSculk() {
        return List.of(
                SurvivesOnBlockTrait.withBlocks(Blocks.MAGMA_BLOCK, Blocks.RED_SAND, Blocks.SAND),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SCULK_LIKE)
        );
    }

    public static List<BlockTrait<?, ?>> boneBlocks() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.BONE_BLOCK, NetherStoneBlocks.BONE_BLOCK));
    }
}
