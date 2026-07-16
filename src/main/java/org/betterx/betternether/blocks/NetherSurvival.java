package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
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
    public static List<SurvivesOnBlockTrait> netherGround() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_STONES),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_MYCELIUM),
                SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM),
                SurvivesOnBlockTrait.withBlocks(Blocks.RED_SAND, Blocks.SAND)
        );
    }

    public static List<SurvivesOnBlockTrait> netherrack() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHERRACK));
    }

    public static List<SurvivesOnBlockTrait> netherrackNyliumAndSculk() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHERRACK),
                SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM),
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SCULK_LIKE)
        );
    }

    public static List<SurvivesOnBlockTrait> nylium() {
        return List.of(SurvivesOnBlockTrait.withTag(BlockTags.NYLIUM));
    }

    public static List<SurvivesOnBlockTrait> netherMycelium() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.NETHER_MYCELIUM));
    }

    public static List<SurvivesOnBlockTrait> netherSand() {
        return List.of(SurvivesOnBlockTrait.withTag(NetherTags.NETHER_SAND));
    }

    public static List<SurvivesOnBlockTrait> soulGround() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND));
    }

    public static List<SurvivesOnBlockTrait> soulGroundOrFarmland() {
        return List.of(
                SurvivesOnBlockTrait.withTag(CommonBlockTags.SOUL_GROUND),
                SurvivesOnBlockTrait.withTag(NetherTags.NETHER_FARMLAND)
        );
    }

    public static List<SurvivesOnBlockTrait> soilOrLogs() {
        return List.of(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOIL_OR_LOGS));
    }

    public static List<SurvivesOnBlockTrait> soulSand() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.SOUL_SAND));
    }

    public static List<SurvivesOnBlockTrait> gravel() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.GRAVEL));
    }

    public static List<SurvivesOnBlockTrait> magmaBlockOrSand() {
        return List.of(SurvivesOnBlockTrait.withBlocks(
                Blocks.MAGMA_BLOCK,
                Blocks.RED_SAND,
                Blocks.SAND,
                Blocks.SCULK
        ));
    }

    public static List<SurvivesOnBlockTrait> boneBlocks() {
        return List.of(SurvivesOnBlockTrait.withBlocks(Blocks.BONE_BLOCK, NetherBlocks.BONE_BLOCK));
    }
}
