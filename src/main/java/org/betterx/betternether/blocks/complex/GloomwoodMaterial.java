package org.betterx.betternether.blocks.complex;

import org.betterx.betternether.blocks.BlockGloomwoodSapling;
import org.betterx.betternether.blocks.NetherSurvival;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;
import org.betterx.betternether.blocks.complex.slots.Sapling;
import org.betterx.betternether.registry.block.NetherStoneBlocks;

import org.betterx.bclib.trait.TraitLists;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.types.Bark;
import de.ambertation.wover.sets.api.blocks.types.Log;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * The light gloomwood set - the still-living upper trunk, bone-crusted and shaded with the gradient off the
 * bottom of {@code minecraft:sculk_catalyst}'s side.
 * <p>
 * The lower trunk is a separate material ({@link GloomwoodDarkMaterial}) rather than a variant of this one,
 * and the two are bridged by the transition log in
 * {@link org.betterx.betternether.registry.block.NetherWoodBlocks}.
 */
public class GloomwoodMaterial extends NetherWoodenMaterial<GloomwoodMaterial> {

    public GloomwoodMaterial() {
        super("gloomwood", MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_WHITE);
        this.setFurnitureCloth(NetherStoneBlocks.NETHER_BRICK_TILE_LARGE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        // NetherWoodenMaterial swaps log/bark/trapdoor for the hand-authored-model slots, because the older
        // wood sets ship their blockstates and models in src/main/resources. Gloomwood has no hand-authored
        // assets: it uses the standard <name>_log_side / <name>_log_top texture layout, so put the generating
        // wover slots back and let datagen produce the models. Same reasoning as anchor_tree.
        return super.createDefaultDefinitions()
                    .add(Sapling.create(
                            BlockGloomwoodSapling::new,
                            // Pottable on the same sculk-like floor it grows on in the wild.
                            TraitLists.and(
                                    NetherSurvival.netherGroundAndSculk(),
                                    PottablePlantBlockTrait.withSoils(CommonBlockTags.SCULK_LIKE)
                            )
                    ))
                    .replace(new Log(true))
                    .replace(new Log(false))
                    .replace(new Bark(true))
                    .replace(new Bark(false))
                    // the gloomwood trapdoor is exactly stalagnate's shared (no-#side) mesh
                    .replace(new NetherWoodSlots.TrapdoorTemplate());
    }

    public Block getSapling() {
        return getBlock(NetherSlots.SAPLING);
    }
}
