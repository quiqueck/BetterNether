package org.betterx.betternether.blocks.complex.slots;

import org.betterx.betternether.blocks.BlockAnchorTreeSapling;
import org.betterx.betternether.blocks.NetherModelKeys;
import de.ambertation.wover.block.api.client.model.ClientBlockModelRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait.ModelFactory;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelKey;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.sets.api.blocks.BlockSet;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_180;

/**
 * The anchor tree's sapling slot: the ordinary {@link Sapling} plus a blockstate that renders the sapling
 * upside down when it is standing on the floor.
 * <p>
 * The shared {@code crossPlant()} model emits a single variant with no properties, which is correct for
 * every other sapling here but cannot express the two orientations this one has. The sapling's texture
 * hangs downward from its attachment point, so a floor-planted one has to be flipped, and only a
 * blockstate variant can do that.
 */
public class AnchorTreeSaplingSlot extends Sapling {
    private AnchorTreeSaplingSlot(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival
    ) {
        super(maker, survival);
    }

    public static AnchorTreeSaplingSlot create(
            Function<BlockBehaviour.Properties, Block> maker,
            List<BlockTrait<?, ?>> survival
    ) {
        return new AnchorTreeSaplingSlot(maker, survival);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockModelBinding.of(MODEL, null);
    }

    private static final ModelKey<Void> MODEL = NetherModelKeys.key("anchor_tree_sapling_slot");

    /**
     * Registers the client generators behind this class's model keys. Called from the mod's
     * {@code wover.client.traits} entrypoint.
     */
    @Environment(EnvType.CLIENT)
    public static void registerClientModels() {
        ClientModel.register();
    }

    /**
     * Kept in its own class rather than inline in {@link #buildModel}, following
     * {@code StalactiteBlockTrait} and {@code PathBlockTrait}: a lambda body's synthetic method does not
     * inherit {@code @Environment(CLIENT)} from its enclosing method, so an inline one would strand
     * client-only types in a class file the dedicated server has to verify.
     */
    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static void register() {
            ClientBlockModelRegistry.register(
                    MODEL,
                    (key, block, generator, payload) -> build().provideBlockModels(key, block, generator)
            );
        }


        private static ModelFactory build() {
            return (key, block, generator) -> {
                final Identifier texture = TextureMapping.getBlockTexture(block).sprite();
                final Identifier model = ModelTemplates.CROSS.create(
                        block, TextureMapping.cross(block), generator.modelOutput()
                );
                final var variant = BlockModelGenerators.plainVariant(model);
                final var props = PropertyDispatch.initial(BlockAnchorTreeSapling.HANGING);
                props.select(true, variant);
                props.select(false, variant.with(X_ROT_180));

                generator.acceptBlockState(MultiVariantGenerator.dispatch(block).with(props));
                generator.createFlatItem(block, texture);
            };
        }
    }
}
