package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;

/**
 * A generic single-block slot (with or without a block item), using a hand-authored (external) model.
 * The maker receives the owning {@link BlockSet} so it can reference sibling slots (e.g. a bowl that needs
 * its trunk). Replaces bclib's {@code SimpleMaterialSlot}/{@code SimpleBlockOnlyMaterialSlot}.
 */
public class SimpleBlockSlot extends SlotFromDefinition {
    private final BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker;
    private final boolean blockOnly;

    private SimpleBlockSlot(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            boolean blockOnly
    ) {
        super(type);
        this.maker = maker;
        this.blockOnly = blockOnly;
    }

    public static SimpleBlockSlot blockOnly(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker
    ) {
        return new SimpleBlockSlot(type, maker, true);
    }

    public static SimpleBlockSlot withItem(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker
    ) {
        return new SimpleBlockSlot(type, maker, false);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        BlockDefinition<?, ?> def = registry.defineDefaultBlock(name, d -> maker.apply(set, d.getProperties()));
        if (blockOnly) {
            def.withBlockItem((d, b) -> null);
        }
        return def;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.externalModel();
    }
}
