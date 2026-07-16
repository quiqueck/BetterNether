package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * The bulky "trunk" of a Nether tree. Parameterized with the concrete block factory (a
 * {@code Foo(BlockBehaviour.Properties)} constructor). Uses a hand-authored (external) model.
 */
public class TrunkSlot extends SlotFromDefinition {
    public static final String TRUNK_SUFFIX = "trunk";

    private final Function<BlockBehaviour.Properties, Block> maker;
    private final boolean climbable;
    private final List<BlockTrait<?, ?>> extraTraits;

    private TrunkSlot(
            Function<BlockBehaviour.Properties, Block> maker,
            boolean climbable,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        super(NetherSlots.TRUNK);
        this.maker = maker;
        this.climbable = climbable;
        this.extraTraits = extraTraits;
    }

    public static TrunkSlot create(Function<BlockBehaviour.Properties, Block> maker) {
        return create(maker, List.of());
    }

    /**
     * @param extraTraits e.g. a {@link org.betterx.betternether.blocks.NetherRender} layer - not every
     *                    trunk is non-solid ({@code willow_trunk} renders solid)
     */
    public static TrunkSlot create(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        return new TrunkSlot(maker, false, extraTraits);
    }

    public static TrunkSlot createClimbable(Function<BlockBehaviour.Properties, Block> maker) {
        return createClimbable(maker, List.of());
    }

    public static TrunkSlot createClimbable(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        return new TrunkSlot(maker, true, extraTraits);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, def -> maker.apply(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(extraTraits);
        if (climbable) {
            def.addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.CLIMBABLE);
        } else {
            def.addTags(BlockTags.MINEABLE_WITH_AXE);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.externalModel();
    }
}
