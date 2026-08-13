package org.betterx.betternether.blocks.complex.slots;

import org.betterx.bclib.trait.block.CompostableBlockTrait;
import org.betterx.bclib.trait.block.PlantBlockTrait;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.bclib.trait.block.SaplingBlockTrait;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betternether.blocks.NetherRender;
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
import org.jetbrains.annotations.NotNull;

/**
 * The sapling of a Nether tree. Parameterized with the concrete block factory (a
 * {@code Foo(BlockBehaviour.Properties)} constructor). Replaces bclib's {@code AbstractSaplingSlot}.
 * Uses a hand-authored (external) cross model.
 */
public class Sapling extends SlotFromDefinition {
    private final Function<BlockBehaviour.Properties, Block> maker;
    private final List<BlockTrait<?, ?>> survival;

    protected Sapling(Function<BlockBehaviour.Properties, Block> maker, List<BlockTrait<?, ?>> survival) {
        super(NetherSlots.SAPLING);
        this.maker = maker;
        this.survival = survival;
    }

    public static Sapling create(Function<BlockBehaviour.Properties, Block> maker, List<BlockTrait<?, ?>> survival) {
        return new Sapling(maker, survival);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // The sapling's properties, built here rather than inherited from the set. A wood set describes a
        // plank, and until this trait was added these saplings were planks: strength 2.0, solid, occluding,
        // BASS on a note block, NETHER_WOOD underfoot - a sapling a player collided with and needed two
        // seconds to break. This is the same core BetterNether's standalone saplings get from
        // NetherMaterial.sapling(): instabreak, no collision, no occlusion, pushReaction(DESTROY) and the
        // CROP sound, with no XZ offset (a sapling stands where it was planted).
        // <p>
        // MapColor.PLANT is a placeholder, not a decision: NetherWoodenMaterial.addCommonBlockDefinitions
        // runs after this method (see SlotFromDefinition) and its mapColor(woodColor) is the last write, so
        // each sapling still takes its own species colour. That is the one thing a sapling does want from
        // its wood set.
        def.addTrait(PlantBlockTrait.withColor(
                MapColor.PLANT, false, BlockBehaviour.OffsetType.NONE, SoundType.CROP, false
        ));
        def.addTrait(survival);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
        // Cutout, like every other sapling. These blocks extend bclib's FeatureSaplingBlock rather than
        // BetterNether's BlockBase, so they were never IRenderTypeable and the old
        // BetterNetherClient.registerRenderLayers() walk never saw them - they have rendered their cross
        // model as SOLID (transparent pixels drawn opaque) since long before the trait migration.
        // bclib's SaplingBlockTrait, which this slot replaces, bundles the same cutout layer.
        def.addTrait(NetherRender.cutout());
        // Real composting. BehaviourSapling (which every sapling block here implements) extends
        // BehaviourCompostable, but that marker only ever produced the c:compostable item tag - it never
        // registered a composter entry, so these were tagged compostable yet would not compost.
        def.addTrait(CompostableBlockTrait.withDefault());
        // The rest of what the retired BehaviourSapling marker contributed: the creative nature-tab marker
        // (BehaviourPlantLike), mineable/hoe (AddMineableHoe), and the c:saplings/minecraft:saplings
        // block+item tags (BehaviourSaplingLike). Compostable is added above, so the PlantLikeBlockTrait
        // bundle is not used here (it would add compostable a second time).
        def.addTrait(PlantLikeBlockTrait.withDefault());
        def.addTrait(BlockTraits.MINEABLE_WITH.needsHoe());
        // SaplingBlockTrait.ticking() rather than the bare VegetationTagTrait.sapling() this used to add:
        // it contributes the same four sapling block/item tags plus the random ticks the block class needs
        // to grow at all. Without them FeatureSaplingBlock's randomTick() is never called and every sapling
        // in this slot (gloomwood, willow, rubeus, mushroom fir, nether sakura, anchor tree) only ever grew
        // from bone meal. The standalone saplings kept random ticks through NetherMaterial.sapling(); this
        // slot, which hand-assembles the pieces of bclib's sapling bundle instead of taking it whole, is
        // where the property was dropped.
        // ticking() and NOT SaplingBlockTrait.withColor(...): the full bundle carries
        // FLAMMABLE.withDefault(), and nothing burns in the Nether (see NetherWoodenMaterial, which drops
        // the same trait from the wood set for this reason). ticking() is the sapling's own behaviour
        // only - random ticks, tags, light - with no fire.
        def.addTrait(SaplingBlockTrait.ticking());
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
        return ModelTraitLibrary.crossPlant();
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a plant), so it must not inherit the set material's sulfur cube archetype - a
        // cube renders what it swallowed as a block model, and a sapling inside one reads as a bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
