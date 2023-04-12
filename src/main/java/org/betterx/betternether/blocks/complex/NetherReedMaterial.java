package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.blocks.BaseBookshelfBlock;
import org.betterx.bclib.blocks.BaseComposterBlock;
import org.betterx.bclib.blocks.BaseCraftingTableBlock;
import org.betterx.bclib.blocks.BaseLadderBlock;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.client.models.PatternsHelper;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BlockReedsBlock;
import org.betterx.betternether.client.block.Patterns;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;
import org.betterx.worlds.together.tag.v3.CommonItemTags;
import org.betterx.worlds.together.tag.v3.CommonPoiTags;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

class ReedBookshelfBlock extends BaseBookshelfBlock {

    public ReedBookshelfBlock(Block source) {
        super(source);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable BlockModel getBlockModel(ResourceLocation blockId, BlockState blockState) {
        Optional<String> pattern = PatternsHelper.createJson(Patterns.REED_BLOCK_BOOKSHELF, replacePath(blockId));
        return ModelsHelper.fromPattern(pattern);
    }
}

public class NetherReedMaterial extends RoofMaterial {

    public NetherReedMaterial() {
        super("nether_reed", MaterialColor.COLOR_CYAN, MaterialColor.COLOR_CYAN);
    }

    @Override
    public NetherReedMaterial init() {
        return (NetherReedMaterial) super.init();
    }

    @Override
    protected void initDefault(BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        super.initDefault(blockSettings, itemSettings);
    }

    @Override
    protected void initDecorations(BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        addBlockEntry(new BlockEntry(
                        BLOCK_CRAFTING_TABLE,
                        (cmx, settings) -> new BaseCraftingTableBlock(getBlock(BLOCK_PLANKS))
                )
                        .setBlockTags(CommonBlockTags.WORKBENCHES)
                        .setItemTags(CommonItemTags.WORKBENCHES)
        );

        addBlockEntry(new BlockEntry(
                BLOCK_BOOKSHELF,
                (cmx, settings) -> new ReedBookshelfBlock(getBlock(BLOCK_PLANKS))
        )
                .setBlockTags(CommonBlockTags.BOOKSHELVES));

        addBlockEntry(new BlockEntry(
                BLOCK_COMPOSTER,
                (complexMaterial, settings) -> new BaseComposterBlock(getBlock(BLOCK_PLANKS))
        )
                .setBlockTags(CommonPoiTags.FARMER_WORKSTATION));
    }

    @Override
    protected void _initBase(BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        addBlockEntry(new BlockEntry(BLOCK_PLANKS, (complexMaterial, settings) -> {
            return new BlockReedsBlock();
        }).setBlockTags(BlockTags.PLANKS).setItemTags(ItemTags.PLANKS));

        addBlockEntry(new BlockEntry(BLOCK_STAIRS, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseStairsBlock(getBlock(BLOCK_PLANKS), false);
        }).setBlockTags(BlockTags.WOODEN_STAIRS, BlockTags.STAIRS)
          .setItemTags(ItemTags.WOODEN_STAIRS, ItemTags.STAIRS));
        addBlockEntry(new BlockEntry(BLOCK_SLAB, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseSlabBlock(getBlock(BLOCK_PLANKS), false);
        }).setBlockTags(BlockTags.WOODEN_SLABS, BlockTags.SLABS)
          .setItemTags(ItemTags.WOODEN_SLABS, ItemTags.SLABS));
        addBlockEntry(new BlockEntry(BLOCK_FENCE, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseFenceBlock(getBlock(BLOCK_PLANKS));
        }).setBlockTags(BlockTags.FENCES, BlockTags.WOODEN_FENCES)
          .setItemTags(ItemTags.FENCES, ItemTags.WOODEN_FENCES));
        addBlockEntry(new BlockEntry(BLOCK_GATE, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseGateBlock(getBlock(BLOCK_PLANKS), woodType);
        }).setBlockTags(BlockTags.FENCE_GATES));
        addBlockEntry(new BlockEntry(BLOCK_BUTTON, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseWoodenButtonBlock(getBlock(BLOCK_PLANKS), woodType.setType());
        }).setBlockTags(BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS)
          .setItemTags(ItemTags.BUTTONS, ItemTags.WOODEN_BUTTONS));
        addBlockEntry(new BlockEntry(BLOCK_PRESSURE_PLATE, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.WoodenPressurePlateBlock(getBlock(BLOCK_PLANKS), woodType.setType());
        }).setBlockTags(BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES)
          .setItemTags(ItemTags.WOODEN_PRESSURE_PLATES));
        addBlockEntry(new BlockEntry(BLOCK_TRAPDOOR, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseTrapdoorBlock(getBlock(BLOCK_PLANKS), woodType.setType());
        }).setBlockTags(BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS)
          .setItemTags(ItemTags.TRAPDOORS, ItemTags.WOODEN_TRAPDOORS));
        addBlockEntry(new BlockEntry(BLOCK_DOOR, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseDoorBlock(getBlock(BLOCK_PLANKS), woodType.setType());
        }).setBlockTags(BlockTags.DOORS, BlockTags.WOODEN_DOORS)
          .setItemTags(ItemTags.DOORS, ItemTags.WOODEN_DOORS));


        addBlockEntry(new BlockEntry(BLOCK_LADDER, (complexMaterial, settings) -> {
            return new BaseLadderBlock(getBlock(BLOCK_PLANKS));
        }).setBlockTags(BlockTags.CLIMBABLE));
        addBlockEntry(new BlockEntry(BLOCK_SIGN, (complexMaterial, settings) -> {
            return new org.betterx.bclib.blocks.BaseSignBlock(getBlock(BLOCK_PLANKS));
        }).setBlockTags(BlockTags.SIGNS).setItemTags(ItemTags.SIGNS));
    }

    @Override
    public void initDefaultRecipes() {
        Block planks = getBlock(BLOCK_PLANKS);
        addRecipeEntry(new RecipeEntry(BLOCK_PLANKS, (material, config, id) -> {
            Block stem = getStem();

            BCLRecipeBuilder.crafting(id, planks)
                            .setOutputCount(1)
                            .setShape("##", "##")
                            .addMaterial('#', stem)
                            .setGroup("planks")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_STAIRS, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_STAIRS))
                            .setOutputCount(4)
                            .setShape("#  ", "## ", "###")
                            .addMaterial('#', planks)
                            .setGroup("stairs")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_SLAB, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_SLAB))
                            .setOutputCount(6)
                            .setShape("###")
                            .addMaterial('#', planks)
                            .setGroup("slab")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_FENCE, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_FENCE))
                            .setOutputCount(3)
                            .setShape("#I#", "#I#")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("fence")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_GATE, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_GATE))
                            .setShape("I#I", "I#I")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("gate")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BUTTON, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BUTTON))
                            .shapeless()
                            .addMaterial('#', planks)
                            .setGroup("button")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_PRESSURE_PLATE, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_PRESSURE_PLATE))
                            .setShape("##")
                            .addMaterial('#', planks)
                            .setGroup("pressure_plate")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_TRAPDOOR, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_TRAPDOOR))
                            .setOutputCount(2)
                            .setShape("###", "###")
                            .addMaterial('#', planks)
                            .setGroup("trapdoor")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_DOOR, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_DOOR))
                            .setOutputCount(3)
                            .setShape("##", "##", "##")
                            .addMaterial('#', planks)
                            .setGroup("door")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_CRAFTING_TABLE, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_CRAFTING_TABLE))
                            .setShape("##", "##")
                            .addMaterial('#', planks)
                            .setGroup("table")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_LADDER, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_LADDER))
                            .setOutputCount(3)
                            .setShape("I I", "I#I", "I I")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("ladder")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_SIGN, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_SIGN))
                            .setOutputCount(3)
                            .setShape("###", "###", " I ")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("sign")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_CHEST, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_CHEST))
                            .setShape("###", "# #", "###")
                            .addMaterial('#', planks)
                            .setGroup("chest")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BARREL, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BARREL))
                            .setShape("#S#", "# #", "#S#")
                            .addMaterial('#', planks)
                            .addMaterial('S', getBlock(BLOCK_SLAB))
                            .setGroup("barrel")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BOOKSHELF, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BOOKSHELF))
                            .setShape("###", "PPP", "###")
                            .addMaterial('#', planks)
                            .addMaterial('P', Items.BOOK)
                            .setGroup("bookshelf")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_COMPOSTER, (material, config, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_COMPOSTER))
                            .setShape("# #", "# #", "###")
                            .addMaterial('#', getBlock(BLOCK_SLAB))
                            .setGroup("composter")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));

        initDefaultFurniture();
        initRoofRecipes();
    }

    public Block getStem() {
        return NetherBlocks.NETHER_REED_STEM;
    }
}
