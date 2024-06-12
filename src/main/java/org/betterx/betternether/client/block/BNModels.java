package org.betterx.betternether.client.block;

import org.betterx.bclib.util.Pair;
import org.betterx.betternether.BetterNether;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.*;

public class BNModels {
    private static final Map<String, ModelTemplate> TEMPLATES = new HashMap<>();

    public static final ResourceLocation CROP_BLOCK_MODEL_LOCATION = BetterNether.C.mk("block/crop_block");
    public static final ResourceLocation JUNGLE_PLANT_MODEL_LOCATION = BetterNether.C.mk("block/jungle_plant");
    public static final ResourceLocation GRASS_FAN_MODEL_LOCATION = BetterNether.C.mk("block/grass_fan");
    public static final ResourceLocation STAIRS_WITH_TOP_MODEL_LOCATION = BetterNether.C.mk("block/stairs_with_top");
    public static final ModelTemplate STAIRS_WITH_TOP_MODEL = new ModelTemplate(
            Optional.of(STAIRS_WITH_TOP_MODEL_LOCATION),
            Optional.empty(),
            TextureSlot.TOP, TextureSlot.BOTTOM
    );


    public record TextureSource(TextureSlot slot, ResourceLocation texture) {
        public static TextureSource of(TextureSlot slot, ResourceLocation texture) {
            return new TextureSource(slot, texture);
        }
    }

    public interface VariantSupplier {
        Variant apply(ResourceLocation id, List<ResourceLocation> all);
    }

    public record ModelSource(ResourceLocation parent, String suffix,
                              List<VariantSupplier> variants,
                              List<TextureSource> textures) {

        public static ModelSource of(
                ResourceLocation parent,
                String suffix,
                List<VariantSupplier> variants,
                TextureSource... textures
        ) {
            return new ModelSource(parent, suffix, variants, Arrays.asList(textures));
        }
    }

    public static void createComplex(WoverBlockModelGenerators generators, Block bl, List<ModelSource> sources) {
        List<Pair<ModelSource, ResourceLocation>> models = sources.stream().map(s -> {
            Optional<ResourceLocation> parent = s.parent() == null ? Optional.empty() : Optional.of(s.parent());
            Optional<String> suffix = (s.suffix() == null || s.suffix().trim().isEmpty())
                    ? Optional.empty()
                    : Optional.of(s.suffix());
            TextureSlot[] slots = s.textures().stream().map(TextureSource::slot).toArray(TextureSlot[]::new);

            final var mapping = new TextureMapping();
            s.textures.forEach(t -> mapping.put(t.slot(), t.texture()));

            ModelTemplate template = new ModelTemplate(parent, suffix, slots);
            return new Pair<>(s, template.create(bl, mapping, generators.vanillaGenerator.modelOutput));
        }).toList();

        List<ResourceLocation> allModels = models.stream().map(p -> p.second).toList();
        final Variant[] variants = models
                .stream()
                .flatMap(m -> m.first.variants().stream().map(f -> f.apply(m.second, allModels)))
                .toArray(Variant[]::new);

        generators.acceptBlockState(MultiVariantGenerator.multiVariant(bl, variants));

        Item item = bl.asItem();
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(sources.get(0).textures.get(0).texture), generators.vanillaGenerator.modelOutput);
    }

    public static ModelTemplate getCropBlockModelTemplate(String suffix) {
        return TEMPLATES.computeIfAbsent(CROP_BLOCK_MODEL_LOCATION.toString() + suffix, s -> new ModelTemplate(
                Optional.of(CROP_BLOCK_MODEL_LOCATION),
                (suffix == null || suffix.trim().isEmpty()) ? Optional.empty() : Optional.of(suffix),
                TextureSlot.TEXTURE
        ));
    }

    public static ModelTemplate getCropCrossBlockModelTemplate(String suffix) {
        return TEMPLATES.computeIfAbsent(WoverBlockModelGenerators.CROSS.toString() + suffix, s -> new ModelTemplate(
                Optional.of(WoverBlockModelGenerators.CROSS),
                (suffix == null || suffix.trim().isEmpty()) ? Optional.empty() : Optional.of(suffix),
                TextureSlot.CROSS
        ));
    }

    @Environment(EnvType.CLIENT)
    public static void provideGrassBlockModels(
            WoverBlockModelGenerators generators,
            Block bl,
            String baseName,
            int count
    ) {
        //crate a range of numbers from 0 to count and map each number to a string
        final var variants = new ArrayList<BNModels.ModelSource>(2 * count);
        for (int i = 1; i <= count; i++) {
            final ResourceLocation texture = BetterNether.C.mk("block/" + baseName + "_" + i);
            variants.add(
                    BNModels.ModelSource.of(
                            WoverBlockModelGenerators.CROSS,
                            "_" + i,
                            List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                            BNModels.TextureSource.of(TextureSlot.CROSS, texture)
                    )
            );
            variants.add(
                    BNModels.ModelSource.of(
                            BNModels.CROP_BLOCK_MODEL_LOCATION,
                            "_" + (count + i),
                            List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                            BNModels.TextureSource.of(TextureSlot.TEXTURE, texture)
                    )
            );
        }

        BNModels.createComplex(generators, bl, variants);
    }

    @Environment(EnvType.CLIENT)
    public static void provideSimpleMultiStateBlock(
            WoverBlockModelGenerators generators,
            Block bl,
            String... suffixes
    ) {
        final String baseName = ModelLocationUtils.getModelLocation(bl).getPath();

        final var variants = new ArrayList<BNModels.ModelSource>(suffixes.length);
        for (int i = 1; i <= suffixes.length; i++) {
            final ResourceLocation texture = BetterNether.C.mk(baseName + suffixes[i - 1]);
            variants.add(
                    BNModels.ModelSource.of(
                            WoverBlockModelGenerators.CUBE_ALL,
                            suffixes[i - 1],
                            List.of((id, all) -> Variant.variant().with(VariantProperties.MODEL, id)),
                            BNModels.TextureSource.of(TextureSlot.ALL, texture)
                    )
            );
        }

        BNModels.createComplex(generators, bl, variants);
    }
}
