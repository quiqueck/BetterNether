package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.trait.block.VegetationTagTrait;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The plantable "seed" of a Nether tree. Parameterized with the concrete block factory
 * (a {@code Foo(BlockBehaviour.Properties)} constructor). Uses a hand-authored (external) model by default, or a
 * caller-supplied {@link BlockModelTrait} (e.g. {@code WeightedCrossModelTrait}) when the seed's parent-based
 * blockstate is generated in code instead.
 */
public class AbstractSeed extends SlotFromDefinition {
    public static final String SEED_SUFFIX = "seed";

    private final Function<BlockBehaviour.Properties, Block> maker;
    private final List<BlockTrait<?, ?>> survival;
    @Nullable
    private final Supplier<BlockModelTrait> modelTrait;

    private AbstractSeed(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival,
            @Nullable Supplier<BlockModelTrait> modelTrait
    ) {
        super(NetherSlots.SEED);
        this.maker = maker;
        this.survival = survival;
        this.modelTrait = modelTrait;
    }

    public static AbstractSeed create(Function<BlockBehaviour.Properties, Block> maker, List<BlockTrait<?, ?>> survival) {
        return new AbstractSeed(maker, survival, null);
    }

    /**
     * Like {@link #create(Function, List)} but with a code-generated model trait (its parent-based blockstate,
     * variant models and item are generated instead of hand-authored). {@code modelTrait} must be a supplier so
     * the client-only trait is resolved lazily inside {@link #buildModel} rather than at slot construction.
     */
    public static AbstractSeed create(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival,
            Supplier<BlockModelTrait> modelTrait
    ) {
        return new AbstractSeed(maker, survival, modelTrait);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(survival);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
        // See Sapling: the BehaviourCompostable marker these blocks carry never registered a composter entry.
        def.addTrait(CompostableBlockTrait.withDefault());
        // The blocks in this slot (BlockStalagnateSeed, BlockWartSeed) are named "seed" but actually
        // implement BehaviourSapling, not BehaviourSeed - so they carry the sapling tags, the nature-tab
        // marker and mineable/hoe, exactly as the retired marker contributed. Compostable is added above,
        // so the PlantLikeBlockTrait.sapling() bundle (which re-adds compostable) is not used here.
        def.addTrait(PlantLikeBlockTrait.withDefault());
        def.addTrait(BlockTraits.MINEABLE_WITH.needsHoe());
        def.addTrait(VegetationTagTrait.sapling());
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, def -> maker.apply(def.getProperties()));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return modelTrait != null ? modelTrait.get() : ModelTraitLibrary.externalModel();
    }
}
