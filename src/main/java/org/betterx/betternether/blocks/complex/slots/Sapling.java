package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * The sapling of a Nether tree. Parameterized with the concrete block factory (a
 * {@code Foo(BlockBehaviour.Properties)} constructor). Replaces bclib's {@code AbstractSaplingSlot}.
 * Uses a hand-authored (external) cross model.
 */
public class Sapling extends SlotFromDefinition {
    private final Function<BlockBehaviour.Properties, Block> maker;

    private Sapling(Function<BlockBehaviour.Properties, Block> maker) {
        super(NetherSlots.SAPLING);
        this.maker = maker;
    }

    public static Sapling create(Function<BlockBehaviour.Properties, Block> maker) {
        return new Sapling(maker);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
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
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.externalModel();
    }
}
