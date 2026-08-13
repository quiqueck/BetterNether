package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.PlantBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.SaplingBlockTrait;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

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
    private final boolean growsOnRandomTick;

    private AbstractSeed(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival,
            @Nullable Supplier<BlockModelTrait> modelTrait,
            boolean growsOnRandomTick
    ) {
        super(NetherSlots.SEED);
        this.maker = maker;
        this.survival = survival;
        this.modelTrait = modelTrait;
        this.growsOnRandomTick = growsOnRandomTick;
    }

    public static AbstractSeed create(Function<BlockBehaviour.Properties, Block> maker, List<BlockTrait<?, ?>> survival) {
        return new AbstractSeed(maker, survival, null, false);
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
        return new AbstractSeed(maker, survival, modelTrait, false);
    }

    /**
     * Like {@link #create(Function, List, Supplier)} but for a seed that grows into a tree by itself - a
     * {@code FeatureSaplingBlock} in a seed-shaped slot (the stalagnate).
     * <p>
     * Whether the seed grows on its own is a per-block property, not a slot-wide one, which is why it is a
     * separate factory rather than a default: the other block in this slot ({@code BlockWartSeed}) is a
     * plain {@code Block} that grows only from bone meal and would gain nothing but a wasted random-tick
     * slot. See {@link SaplingBlockTrait#ticking()} for what "grows by itself" actually needs.
     */
    public static AbstractSeed createGrowing(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival,
            Supplier<BlockModelTrait> modelTrait
    ) {
        return new AbstractSeed(maker, survival, modelTrait, true);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Plant properties, for the same reason the Sapling slot has them: a wood set describes a plank, and
        // these seeds were inheriting one - strength 2.0, solid, occluding, BASS, NETHER_WOOD - where every
        // standalone seed in the mod (ink bush, black apple, lumabus) is an instabreak plant with no
        // collision. MapColor.PLANT is a placeholder; the set's mapColor(woodColor) runs after this method
        // and wins. See the Sapling slot for the full note.
        def.addTrait(PlantBlockTrait.withColor(
                MapColor.PLANT, false, BlockBehaviour.OffsetType.NONE, SoundType.CROP, false
        ));
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
        if (growsOnRandomTick) {
            // Same four sapling tags as VegetationTagTrait.sapling(), plus the random ticks the block class
            // grows in - see SaplingBlockTrait.ticking() and the Sapling slot, which had lost the same
            // property. Without it the stalagnate seed only ever grew from bone meal.
            def.addTrait(SaplingBlockTrait.ticking());
        } else {
            def.addTrait(VegetationTagTrait.sapling());
        }
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

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a plant), so it must not inherit the set material's sulfur cube archetype - a
        // cube renders what it swallowed as a block model, and a seed inside one reads as a bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
