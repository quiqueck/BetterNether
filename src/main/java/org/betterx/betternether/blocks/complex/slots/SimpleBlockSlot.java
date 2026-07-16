package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
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
    private final List<BlockTrait<?, ?>> extraTraits;

    private SimpleBlockSlot(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            boolean blockOnly,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        super(type);
        this.maker = maker;
        this.blockOnly = blockOnly;
        this.extraTraits = extraTraits;
    }

    public static SimpleBlockSlot blockOnly(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker
    ) {
        return blockOnly(type, maker, List.of());
    }

    /**
     * @param extraTraits e.g. a {@link org.betterx.betternether.blocks.NetherRender} layer
     */
    public static SimpleBlockSlot blockOnly(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        return new SimpleBlockSlot(type, maker, true, extraTraits);
    }

    public static SimpleBlockSlot withItem(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker
    ) {
        return withItem(type, maker, List.of());
    }

    /**
     * @param extraTraits e.g. a {@link org.betterx.betternether.blocks.NetherRender} layer
     */
    public static SimpleBlockSlot withItem(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> extraTraits
    ) {
        return new SimpleBlockSlot(type, maker, false, extraTraits);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(extraTraits);
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
