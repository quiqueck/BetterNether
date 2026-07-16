package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherTags;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

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

    public static List<BlockTrait<?, ?>> magmaBlockOrSand() {
        return List.of(SurvivesOnBlockTrait.withBlocks(
                Blocks.MAGMA_BLOCK,
                Blocks.RED_SAND,
                Blocks.SAND,
                Blocks.SCULK
        ));
    }

    public static List<BlockTrait<?, ?>> boneBlocks() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.BONE_BLOCK, NetherBlocks.BONE_BLOCK));
    }
}
