package org.betterx.datagen.betternether;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.item.NetherEquipmentItems;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.EquipmentSet;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverModelProvider;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.client.trait.ClientItemTraits;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.resources.Identifier;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class NetherModelProvider extends WoverModelProvider {
    /**
     * Set by {@link #bootstrapBlockStateModels} (which always runs first - vanilla generates block-state
     * models before item models) and read back by {@link #bootstrapItemModels} to know which blocks
     * already generated their own item model via {@link WoverBlockModelGenerators#hasItemModel}, so the
     * flat-item fallback there doesn't step on them.
     */
    private WoverBlockModelGenerators generator;

    /**
     * The hand-authored resource root (src/main/resources), derived from the datagen output directory
     * (src/main/generated) that build.gradle passes as {@code fabric-api.datagen.output-dir}.
     */
    private static final Path HAND_AUTHORED_ROOT = resolveHandAuthoredRoot();

    private static Path resolveHandAuthoredRoot() {
        final String outputDir = System.getProperty("fabric-api.datagen.output-dir");
        if (outputDir == null) return null;
        return Path.of(outputDir).getParent().resolve("resources");
    }

    /**
     * Whether the mod already ships a hand-authored model at {@code model}, in which case datagen must not
     * write one on top of it. A minimal stand-in for Forge's {@code ExistingFileHelper}, which Fabric has
     * no equivalent of; the question cannot be answered from the generators, because
     * {@link ItemModelGenerators} will happily emit a model over a hand-authored file of the same name and
     * only the resource-pack merge (where source-dir order decides the winner) would notice.
     */
    private static boolean shipsHandAuthoredModel(Identifier model) {
        if (HAND_AUTHORED_ROOT == null) {
            throw new IllegalStateException(
                    "fabric-api.datagen.output-dir is not set, so hand-authored models cannot be detected " +
                            "and datagen would silently overwrite them. Run datagen via the runDatagenClient task."
            );
        }
        return Files.isRegularFile(HAND_AUTHORED_ROOT.resolve(
                "assets/%s/models/%s.json".formatted(model.getNamespace(), model.getPath())
        ));
    }

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {
        ItemModelTrait.bootstrapModels(modCore, itemModelGenerator);

        // Every registered item needs an item-model definition (assets/betternether/items/*.json).
        // Block items normally get theirs as a side effect of their block's model generation - skip
        // those here (checked via generator.hasItemModel()). Items carrying an explicit ItemModelTrait
        // are already handled by bootstrapModels() above.
        ItemRegistry.forMod(BetterNether.C).allEntries().forEach(entry -> {
            var item = entry.getValue();
            if (item instanceof BlockItem blockItem && generator.hasItemModel(blockItem.getBlock())) return;
            if (ClientItemTraits.MODEL.getRuntimeTraits(item) != null) return;

            final Identifier model = ModelLocationUtils.getModelLocation(item);
            if (generateTrimmableArmor(itemModelGenerator, item, model)) return;
            if (!shipsHandAuthoredModel(model) && item instanceof BlockItem blockItem) {
                // A block item whose block generated (or hand-authors) a model, but did not register an item
                // model for it - e.g. the blocks handled by a ModelOverides entry. Delegate to the block
                // model, which is what these shipped pre-migration; a flat icon would look for an
                // item/<name> texture that does not exist for a block.
                itemModelGenerator.itemModelOutput.accept(
                        item,
                        ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(blockItem.getBlock()))
                );
            } else if (shipsHandAuthoredModel(model)) {
                // BetterNether hand-authors most of its item models. Only wire the definition to them:
                // generateFlatItem() would ALSO write a flat model (ItemModelGenerators#generateFlatItem
                // -> createFlatItemModel) on top of the hand-authored file, and the generated copy is
                // usually the worse one - a flat icon where a block model belongs (anchor_tree_log,
                // anchor_tree_slab), or a layer0 guess pointing at a texture that does not exist at all
                // (agave, whose hand-authored model correctly uses item/agave_seed).
                itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            } else {
                itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        });
    }

    /**
     * Humanoid armor is in {@code #minecraft:trimmable_armor} (through {@code #minecraft:head_armor} and
     * friends), so it can be trimmed, and the worn trim renders from vanilla's generic trim atlas. The
     * inventory icon, however, only shows the trim if the item-model definition selects on
     * {@code minecraft:trim_material} like vanilla's own armor does (see
     * {@link ItemModelGenerators#generateTrimmableItem}). This is that method, except that the untrimmed
     * case points at the hand-authored model instead of overwriting it.
     *
     * @return {@code true} if {@code item} is BetterNether humanoid armor and its item model was generated here
     */
    private boolean generateTrimmableArmor(ItemModelGenerators itemModelGenerator, Item item, Identifier model) {
        // Item components are not bound during datagen, so the EQUIPPABLE slot cannot be read off the
        // item; take it from the equipment set that registered it instead.
        for (EquipmentSet set : List.of(
                NetherEquipmentItems.CINCINNASITE_SET,
                NetherEquipmentItems.NETHER_RUBY_SET,
                NetherEquipmentItems.FLAMING_RUBY_SET
        )) {
            if (set.armorTier == null) continue;
            for (ArmorSlot slot : List.of(ArmorSlot.HELMET_SLOT, ArmorSlot.CHESTPLATE_SLOT, ArmorSlot.LEGGINGS_SLOT, ArmorSlot.BOOTS_SLOT)) {
                if (set.get(slot) != item) continue;
                final Identifier slotTrimPrefix = ItemModelGenerators.prefixForSlotTrim(slot.name);
                generateTrimmableArmor(itemModelGenerator, item, model, slotTrimPrefix);
                return true;
            }
        }
        return false;
    }

    private void generateTrimmableArmor(
            ItemModelGenerators itemModelGenerator,
            Item item,
            Identifier model,
            Identifier slotTrimPrefix
    ) {
        final Material itemTexture = TextureMapping.getItemTexture(item);
        final List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>();
        for (ItemModelGenerators.TrimMaterialData material : ItemModelGenerators.TRIM_MATERIAL_MODELS) {
            // 26.3 replaced the per-equipment-asset MaterialAssetGroup with a flat Palette; the "darker"
            // variant vanilla picks for armor of the trim's own material is a per-call override map, which
            // never applies to BetterNether's own equipment assets - so the palette is used as is.
            final Identifier trimModel = model.withSuffix("_" + material.palette().suffix() + "_trim");
            final Material trimOverlay = new Material(
                    slotTrimPrefix.withSuffix("_" + material.palette().suffix())
            );
            itemModelGenerator.generateLayeredItem(trimModel, itemTexture, trimOverlay);
            cases.add(ItemModelUtils.when(material.materialKey(), ItemModelUtils.plainModel(trimModel)));
        }

        if (!shipsHandAuthoredModel(model)) {
            ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(itemTexture), itemModelGenerator.modelOutput);
        }
        itemModelGenerator.itemModelOutput.accept(
                item,
                ItemModelUtils.select(new TrimMaterialProperty(), ItemModelUtils.plainModel(model), cases)
        );
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.generator = generator;
        // Every BetterNether block now declares its model as a model trait (BlockModelBinding) at
        // registration (ModelTraitLibrary / NetherModels / its wood slot), so there is nothing left to
        // override centrally by block identity. The map stays only to feed the two filters below.
        final ModelOverides overrides = ModelOverides.create();

        final BlockRegistry registry = BlockRegistry.forMod(BetterNether.C);

        // The blocks and wover block sets express their models as model traits, which
        // only this call honours - without it none of them (planks, chests, doors, furniture, ...) get a
        // blockstate at all. The filter keeps the contract that anything re-added to the (now empty)
        // override map above wins over its trait, rather than generating both.
        BlockModelTrait.bootstrapModels(modCore, generator, (key, block) -> !overrides.contain(block));

        // Blocks with an explicit model trait are now fully handled above - skip the
        // legacy BlockModelProvider-interface fallback in addFromRegistry() for them, so a block carrying
        // a MODEL trait does not also get a plain cube model registered by the fallback (which would
        // register the same model twice).
        registry.allBlocks().forEach(block -> {
            if (!overrides.contain(block) && BlockTrait.getRuntimeTraits(block, BlockModelBinding.MODEL_TRAIT_KEY) != null) {
                overrides.ignore(block);
            }
        });

        this.addFromRegistry(
                generator,
                registry,
                true,
                overrides
        );
    }



    public NetherModelProvider(ModCore modCore) {
        super(modCore);
    }

}
