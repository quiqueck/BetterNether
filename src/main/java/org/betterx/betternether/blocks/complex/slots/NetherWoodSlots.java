package org.betterx.betternether.blocks.complex.slots;

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
    /** A ladder with hand-authored models. */
    public static class Ladder extends org.betterx.wover.sets.api.blocks.types.Ladder {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
        }
    }

    /** A trapdoor with hand-authored models. */
    public static class Trapdoor extends org.betterx.wover.sets.api.blocks.types.Trapdoor {
        @Environment(EnvType.CLIENT)
        @Override
        protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
            return ModelTraitLibrary.externalModel();
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
