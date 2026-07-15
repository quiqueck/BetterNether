package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseGlassBlock;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

public class BNGlass extends BaseGlassBlock implements BlockModelProvider {
    public BNGlass(Block block) {
        super(block, 0.3f);
    }

    public BNGlass(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        super(settings, 0.3f);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void provideBlockModels(WoverBlockModelGenerators generators) {
        var resource = TextureMapping.getBlockTexture(this);
        if (!resource.getPath().equals("block/quartz_glass") && !resource
                .getPath()
                .equals("block/quartz_glass_framed")) {
            final var model = TexturedModel.CUBE.get(this);
            final var mapping = WoverBlockModelGenerators.textureMappingOf(
                    TextureSlot.ALL,
                    ResourceLocation.fromNamespaceAndPath(
                            resource.getNamespace(),
                            resource
                                    .getPath()
                                    .replace("quartz_glass_", "quartz_stained_glass_")
                    )
            );

            final var loc = model.getTemplate()
                                 .create(this, mapping, generators.vanillaGenerator.modelOutput);

            generators.acceptBlockState(
                    BlockModelGenerators.createSimpleBlock(
                            this,
                            BlockModelGenerators.plainVariant(loc)
                    )
            );
        } else {
            generators.modelFor(TexturedModel.CUBE.get(this)).createFullBlock(this);
        }

        if (resource.getPath().equals("block/quartz_glass")) {
            final var mapping = WoverBlockModelGenerators.textureMappingOf(TextureSlot.ALL, BetterNether.C.mk("item/quartz_glass"));
            final var template = new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(this)), Optional.empty(), TextureSlot.ALL);

            final var itemModel = ModelLocationUtils.getModelLocation(this.asItem());
            template.create(itemModel, mapping, generators.vanillaGenerator.modelOutput);
            // The template.create above only writes the item MODEL (models/item/quartz_glass.json)
            // through modelOutput. Register the item-model DEFINITION (items/quartz_glass.json) pointing
            // at it via delegateItemModel, which also marks this block as having its item model provided
            // so the flat-item fallback in NetherModelProvider.bootstrapItemModels doesn't re-generate
            // (and collide with) this model.
            generators.delegateItemModel(this, itemModel);
        }
    }
}