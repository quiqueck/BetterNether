package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    private final Consumer<BlockDefinition<?, ?>> extraProperties;

    private SimpleBlockSlot(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            boolean blockOnly,
            List<BlockTrait<?, ?>> extraTraits,
            Consumer<BlockDefinition<?, ?>> extraProperties
    ) {
        super(type);
        this.maker = maker;
        this.blockOnly = blockOnly;
        this.extraTraits = extraTraits;
        this.extraProperties = extraProperties;
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
        return new SimpleBlockSlot(type, maker, true, extraTraits, null);
    }

    /**
     * @param extraTraits     e.g. a {@link org.betterx.betternether.blocks.NetherRender} layer
     * @param extraProperties chained property setters applied to this slot's definition (e.g.
     *                        {@code def -> def.noOcclusion()}) - the visible equivalent of a property
     *                        trait, for the (rare) slot that needs one: there is no single
     *                        {@code BlockDefinition} at this call site to chain a setter onto directly,
     *                        since the definition is built later, once per {@link BlockSet}
     */
    public static SimpleBlockSlot blockOnly(
            SlotType type,
            BiFunction<BlockSet<?>, BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> extraTraits,
            Consumer<BlockDefinition<?, ?>> extraProperties
    ) {
        return new SimpleBlockSlot(type, maker, true, extraTraits, extraProperties);
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
        return new SimpleBlockSlot(type, maker, false, extraTraits, null);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(extraTraits);
        if (extraProperties != null) extraProperties.accept(def);
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
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.externalModel();
    }
}
