package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.trait.block.TemplateModelTrait;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The wover wood slots whose blockstate, block model and item model BetterNether hand-authors in
 * {@code src/main/resources} rather than generating.
 * <p>
 * Each only swaps the slot's {@code buildModel} for {@link ModelTraitLibrary#externalModel()}, which
 * excludes the block from blockstate validation and points its item-model definition at the conventional
 * {@code item/<name>} model - exactly what the hand-authored assets provide. This replaces the central
 * {@code addMaterialOverrides} {@code .ignore(...)} list in {@code NetherModelProvider}: the "this block's
 * model is static" fact now travels with the slot instead of being restated per material by block identity.
 * <p>
 * {@code buildModel} is only ever called behind {@code SlotFromDefinition}'s {@code ModCore.isClient()}
 * guard, and its body holds no lambda, so annotating it {@code @Environment(CLIENT)} is safe here (the
 * stripper trap only bites lambdas - see {@code NetherModels}).
 */
public class NetherWoodSlots {
    /** The shared, hand-authored ladder template every BetterNether ladder child model parents from. */
    public static final net.minecraft.resources.ResourceLocation LADDER_TEMPLATE =
            BetterNether.C.mk("block/nether_reed_ladder");

    /**
     * A ladder whose child model/blockstate/item are generated from the shared {@link #LADDER_TEMPLATE}
     * template parent. Used for every wood set except nether_reed itself, whose own ladder model
     * <em>is</em> the template (see {@code LadderExternal}).
     */
    public static class Ladder extends org.betterx.wover.sets.api.blocks.types.Ladder {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return TemplateModelTrait.ladder(LADDER_TEMPLATE);
        }
    }

    /**
     * A ladder with a fully hand-authored model - for nether_reed, whose {@code nether_reed_ladder} model
     * doubles as the shared ladder template ({@link #LADDER_TEMPLATE}) and so cannot be generated as a child
     * of itself.
     */
    public static class LadderExternal extends org.betterx.wover.sets.api.blocks.types.Ladder {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /**
     * The shared, hand-authored trapdoor template (the no-{@code #side} shape) that {@link TrapdoorTemplate}
     * children parent from. stalagnate's own {@code stalagnate_trapdoor} model doubles as this template.
     */
    public static final net.minecraft.resources.ResourceLocation TRAPDOOR_TEMPLATE =
            BetterNether.C.mk("block/stalagnate_trapdoor");

    /**
     * A trapdoor with a fully hand-authored model. Most BetterNether wood sets have a genuinely bespoke
     * trapdoor mesh (each with its own element geometry/UVs), which a template parent cannot reproduce, so
     * they stay hand-authored. The no-{@code #side} sets (mushroom_fir/anchor_tree/nether_reed) instead use
     * {@link TrapdoorTemplate}, which shares stalagnate's mesh via {@link #TRAPDOOR_TEMPLATE}.
     */
    public static class Trapdoor extends org.betterx.wover.sets.api.blocks.types.Trapdoor {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /**
     * A trapdoor whose child model/blockstate/item are generated from the shared {@link #TRAPDOOR_TEMPLATE}
     * (stalagnate's no-{@code #side} trapdoor mesh). Used by the wood sets whose trapdoor is exactly that
     * shared shape (mushroom_fir, anchor_tree, nether_reed).
     */
    public static class TrapdoorTemplate extends org.betterx.wover.sets.api.blocks.types.Trapdoor {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return TemplateModelTrait.trapdoor(TRAPDOOR_TEMPLATE, false);
        }
    }

    /** A fence gate with hand-authored models. */
    public static class Gate extends org.betterx.wover.sets.api.blocks.types.Gate {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /** A fence with hand-authored models. */
    public static class Fence extends org.betterx.wover.sets.api.blocks.types.Fence {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /** A slab with hand-authored models. */
    public static class Slab extends org.betterx.wover.sets.api.blocks.types.Slab {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /** A log (or stripped log, for {@code stripable == false}) with hand-authored models. */
    public static class Log extends org.betterx.wover.sets.api.blocks.types.Log {
        public Log(boolean stripable) {
            super(stripable);
        }

        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /** A bark (or stripped bark, for {@code stripable == false}) with hand-authored models. */
    public static class Bark extends org.betterx.wover.sets.api.blocks.types.Bark {
        public Bark(boolean stripable) {
            super(stripable);
        }

        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }
}
