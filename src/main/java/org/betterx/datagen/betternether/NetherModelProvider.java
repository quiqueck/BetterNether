package org.betterx.datagen.betternether;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betternether.blocks.complex.NetherWoodenMaterial;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.client.block.BNModels;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    private static boolean shipsHandAuthoredModel(ResourceLocation model) {
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

            final ResourceLocation model = ModelLocationUtils.getModelLocation(item);
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

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.generator = generator;
        final Block reedPlanks = NetherBlocks.MAT_REED.getBlock(SlotType.PLANKS);
        final ResourceLocation NETHER_REED_PLANKS = TextureMapping.getBlockTexture(reedPlanks);
        final ResourceLocation NETHER_REED_PLANKS_TOP = BetterNether.C.mk("block/nether_reed_planks_top");

        final ResourceLocation SOUL_SANDSTONE_BOTTOM = BetterNether.C.mk("block/soul_sandstone_bottom");
        final ResourceLocation SOUL_SANDSTONE_TOP = BetterNether.C.mk("block/soul_sandstone_top");
        final ResourceLocation SOUL_SANDSTONE_SLABS = BetterNether.C.mk("block/soul_sandstone_slabs");
        final ResourceLocation SOUL_SANDSTONE_CUT_SLABS = BetterNether.C.mk("block/soul_sandstone_cut_slabs");

        final ModelOverides overrides = ModelOverides
                .create()
                .override(NetherBlocks.BASALT_BRICKS, block -> BNModels.provideSimpleMultiStateBlock(generator, block, "", "_cracked"))
                .override(NetherBlocks.MAT_REED.getBlock(SlotType.STAIRS), block -> generator.createStairs(block, NETHER_REED_PLANKS, NETHER_REED_PLANKS, NETHER_REED_PLANKS_TOP))

                // These three keep their hand-authored blockstate/model, which the generic slot model would
                // overwrite with a strictly worse one; the override only wires the item model (pointing at the
                // same model the hand-authored blockstate uses, since the generic one is no longer written).
                // Reed planks are axis-aware and top-textured: the slot model is an axis-less cube_all, whose
                // blockstate has no variant for axis=x/z at all. The two stems delegate to a dedicated trunk
                // model (mushroom fir) and to randomised stem variants (stalagnate).
                .override(reedPlanks, generator::delegateItemModel)
                .override(
                        NetherBlocks.MAT_MUSHROOM_FIR.getStem(),
                        b -> generator.delegateItemModel(b, BetterNether.C.mk("block/mushroom_fir_trunk_middle"))
                )
                .override(
                        NetherBlocks.MAT_STALAGNATE.getStem(),
                        b -> generator.delegateItemModel(b, BetterNether.C.mk("block/stalagnate_stem_1"))
                )
                .override(NetherBlocks.SOUL_SANDSTONE_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_SLABS, SOUL_SANDSTONE_BOTTOM))
                .override(NetherBlocks.SOUL_SANDSTONE_SMOOTH_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_TOP))
                .override(NetherBlocks.SOUL_SANDSTONE_CUT_STAIRS, block -> generator.createStairs(block, SOUL_SANDSTONE_TOP, SOUL_SANDSTONE_CUT_SLABS, SOUL_SANDSTONE_TOP))
                .ignore(NetherBlocks.SOUL_SANDSTONE_CUT_SLAB)
                .ignore(NetherBlocks.SOUL_SANDSTONE_SLAB)
                .ignore(NetherBlocks.SOUL_SANDSTONE_SMOOTH_SLAB)
                .ignore(NetherBlocks.BASALT_SLAB)
                .ignore(NetherBlocks.BASALT_BRICKS_SLAB)
                .ignore(NetherBlocks.BONE_SLAB)
                .ignore(NetherBlocks.BLUE_OBSIDIAN_BRICKS_SLAB)
                .ignore(NetherBlocks.BLUE_OBSIDIAN_TILE_SLAB)
                .ignore(NetherBlocks.NETHER_BRICK_TILE_SLAB)
                .ignore(NetherBlocks.NETHER_RUBY_SLAB)
                .ignore(NetherBlocks.OBSIDIAN_BRICKS_SLAB)
                .ignore(NetherBlocks.OBSIDIAN_TILE_SLAB)
                .ignore(NetherBlocks.CINCINNASITE_SLAB)
                .ignore(NetherBlocks.ROOF_TILE_CINCINNASITE_SLAB)
                .ignore(NetherBlocks.ROOF_TILE_NETHER_BRICKS_SLAB)
                .override(NetherBlocks.BONE_PLATE, block -> generator.createPressurePlate(block, BetterNether.C.mk("block/bone_block_plate")))
                .override(NetherBlocks.BONE_BUTTON, block -> generator.createButton(block, BetterNether.C.mk("block/bone_button")))
                .override(NetherBlocks.CINCINNASITE_PLATE, block -> generator.createPressurePlate(block, BetterNether.C.mk("block/cincinnasite_plate_up")))
                .override(NetherBlocks.CINCINNASITE_BUTTON, block -> generator.createButton(block, BetterNether.C.mk("block/cincinnasite_button")))
                .override(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(SlotType.PRESSURE_PLATE), block -> generator.createPressurePlate(block, BetterNether.C.mk("block/nether_mushroom_plate")))
                .override(NetherBlocks.MAT_NETHER_MUSHROOM.getBlock(SlotType.BUTTON), block -> generator.createButton(block, BetterNether.C.mk("block/nether_mushroom_button")))
                .override(NetherBlocks.CHAIR_CINCINNASITE, block -> {
                    //this was a custom Item with a view transform, it is easier to recreate the json instead of finding
                    //an appropriate API call that fits this special case...
                    generator.acceptModelOutput(ModelLocationUtils.getModelLocation(block.asItem()), () -> {
                        JsonObject root = new JsonObject();
                        JsonObject display = new JsonObject();
                        JsonObject gui = new JsonObject();
                        JsonObject fixed = new JsonObject();

                        root.addProperty("parent", ModelLocationUtils.getModelLocation(block).toString());
                        root.add("display", display);

                        display.add("gui", gui);
                        gui.add("rotation", toArray(30, 45, 0));
                        gui.add("translation", toArray(0, -1.4f, 0));
                        gui.add("scale", toArray(0.625f, 0.625f, 0.625f));

                        display.add("fixed", fixed);
                        fixed.add("rotation", toArray(0, 0, 0));
                        fixed.add("translation", toArray(0, 0, 0));
                        fixed.add("scale", toArray(0.5f, 0.5f, 0.5f));

                        return root;
                    });
                    // acceptModelOutput above only writes the item MODEL. Register the item-model
                    // DEFINITION pointing at it (and mark the block's item model as provided) so the
                    // flat-item fallback in bootstrapItemModels doesn't re-generate/collide with it.
                    generator.delegateItemModel(block, ModelLocationUtils.getModelLocation(block.asItem()));
                    BCLModels.createChairBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE);
                })
                .override(NetherBlocks.BAR_STOOL_CINCINNASITE, block -> BCLModels.createBarStoolBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED, NetherBlocks.NETHER_BRICK_TILE_LARGE))
                .override(NetherBlocks.TABURET_CINCINNASITE, block -> BCLModels.createTaburetBlockModel(generator, block, NetherBlocks.CINCINNASITE_FORGED))
                .ignore(NetherBlocks.NETHER_BRICK_WALL)
                .ignore(NetherBlocks.BASALT_BRICKS_WALL)
                .ignore(NetherBlocks.CINCINNASITE_WALL)
                .ignore(NetherBlocks.SOUL_SANDSTONE_WALL)
                .ignore(NetherBlocks.BONE_WALL)
                .ignore(NetherBlocks.NEON_EQUISETUM)
                .ignore(NetherBlocks.WHISPERING_GOURD_VINE)
                .ignore(NetherBlocks.GOLDEN_VINE)
                .ignore(NetherBlocks.LUMABUS_VINE)
                .ignore(NetherBlocks.GOLDEN_LUMABUS_VINE)
                .ignore(NetherBlocks.EYE_VINE)
                .ignore(NetherBlocks.BLACK_VINE)
                .ignore(NetherBlocks.BLOOMING_VINE);


        addMaterialOverrides(overrides, NetherBlocks.MAT_REED);
        addMaterialOverrides(overrides, NetherBlocks.MAT_ANCHOR_TREE);
        addMaterialOverrides(overrides, NetherBlocks.MAT_NETHER_MUSHROOM);
        addMaterialOverrides(overrides, NetherBlocks.MAT_NETHER_SAKURA);
        addMaterialOverrides(overrides, NetherBlocks.MAT_MUSHROOM_FIR);
        addMaterialOverrides(overrides, NetherBlocks.MAT_STALAGNATE);
        addMaterialOverrides(overrides, NetherBlocks.MAT_RUBEUS);
        addMaterialOverrides(overrides, NetherBlocks.MAT_WILLOW);
        addMaterialOverrides(overrides, NetherBlocks.MAT_WART);
        addMaterialOverrides(overrides, NetherBlocks.WARPED_WOOD);
        addMaterialOverrides(overrides, NetherBlocks.CRIMSON_WOOD);

        final BlockRegistry registry = BlockRegistry.forMod(BetterNether.C);

        // The wover block sets express their models as ClientBlockTraits.MODEL traits, which only this
        // call honours - without it none of the set blocks (planks, chests, doors, furniture, ...) get a
        // blockstate at all. Blocks with an explicit entry in ModelOverides above are meant to use that
        // model instead of the trait's (or keep a hand-authored one, via .ignore()), so skip them here.
        BlockModelTrait.bootstrapModels(modCore, generator, (key, block) -> !overrides.contain(block));

        // Blocks with an explicit ClientBlockTraits.MODEL trait are now fully handled above - skip the
        // legacy BlockModelProvider-interface fallback in addFromRegistry() for them, since BaseBlock
        // implements that interface unconditionally (defaulting to a plain cube model) and running both
        // would register the same model twice.
        registry.allBlocks().forEach(block -> {
            if (!overrides.contain(block) && ClientBlockTraits.MODEL.getRuntimeTraits(block) != null) {
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

    private void addMaterialOverrides(ModelOverides overides, NetherWoodenMaterial<?> mat) {
        overides
                .ignore(mat.getBlock(SlotType.LADDER))
                .ignore(mat.getBlock(SlotType.TRAPDOOR))
                .ignore(mat.getBlock(SlotType.GATE))
                .ignore(mat.getBlock(SlotType.FENCE))
                .ignore(mat.getBlock(SlotType.SLAB))
                .ignore(mat.getBlock(SlotType.LOG))
                .ignore(mat.getBlock(SlotType.STRIPPED_LOG))
                .ignore(mat.getBlock(SlotType.BARK))
                .ignore(mat.getBlock(SlotType.STRIPPED_BARK))
                .ignore(mat.getBlock(NetherSlots.ROOF_SLAB));
    }


    public NetherModelProvider(ModCore modCore) {
        super(modCore);
    }

    private static JsonElement toArray(float... values) {
        JsonArray array = new JsonArray(values.length);
        for (float value : values) {
            array.add(value);
        }
        return array;
    }
}
