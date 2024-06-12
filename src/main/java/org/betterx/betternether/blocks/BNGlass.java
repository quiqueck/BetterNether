package org.betterx.betternether.blocks;

import org.betterx.bclib.blocks.BaseGlassBlock;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

public class BNGlass extends BaseGlassBlock {
    public BNGlass(Block block) {
        super(block, 0.3f);
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
                            loc
                    )
            );
        } else {
            generators.modelFor(TexturedModel.CUBE.get(this)).createFullBlock(this);
        }

        if (resource.getPath().equals("block/quartz_glass")) {
            final var mapping = WoverBlockModelGenerators.textureMappingOf(TextureSlot.ALL, BetterNether.C.mk("item/quartz_glass"));
            final var template = new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(this)), Optional.empty(), TextureSlot.ALL);

            template.create(ModelLocationUtils.getModelLocation(this.asItem()), mapping, generators.vanillaGenerator.modelOutput);
        }
    }
}