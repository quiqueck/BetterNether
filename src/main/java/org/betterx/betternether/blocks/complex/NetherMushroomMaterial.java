package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.blocks.BaseLadderBlock;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betternether.blocks.BlockStem;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

public class NetherMushroomMaterial extends NetherWoodenMaterial {
    public final static String BLOCK_STEM = BLOCK_OPTIONAL_STEM;

    public NetherMushroomMaterial() {
        super("nether_mushroom", MaterialColor.TERRACOTTA_WHITE, MaterialColor.COLOR_LIGHT_GRAY);
    }

    @Override
    public NetherMushroomMaterial init() {
        return (NetherMushroomMaterial) super.init();
    }

    @Override
    protected void initDefault(BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        super.initDefault(blockSettings, itemSettings);

        addBlockEntry(new BlockEntry(BLOCK_STEM, (complexMaterial, settings) -> {
            return new BlockStem(MaterialColor.TERRACOTTA_WHITE);
        }));
    }

    @Override
    protected void _initBase(BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        addBlockEntry(new BlockEntry(
                BLOCK_PLANKS,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseBlock(settings)
        ).setBlockTags(BlockTags.PLANKS).setItemTags(ItemTags.PLANKS));

        addBlockEntry(new BlockEntry(
                BLOCK_STAIRS,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseStairsBlock(
                        getBlock(BLOCK_PLANKS),
                        false
                )
        ).setBlockTags(BlockTags.WOODEN_STAIRS, BlockTags.STAIRS)
         .setItemTags(ItemTags.WOODEN_STAIRS, ItemTags.STAIRS));
        addBlockEntry(new BlockEntry(
                BLOCK_SLAB,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseSlabBlock(getBlock(BLOCK_PLANKS), false)
        ).setBlockTags(BlockTags.WOODEN_SLABS, BlockTags.SLABS)
         .setItemTags(ItemTags.WOODEN_SLABS, ItemTags.SLABS));
        addBlockEntry(new BlockEntry(
                BLOCK_FENCE,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseFenceBlock(getBlock(BLOCK_PLANKS))
        ).setBlockTags(BlockTags.FENCES, BlockTags.WOODEN_FENCES)
         .setItemTags(ItemTags.FENCES, ItemTags.WOODEN_FENCES));
        addBlockEntry(new BlockEntry(
                BLOCK_GATE,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseGateBlock(
                        getBlock(BLOCK_PLANKS),
                        woodType.type
                )
        ).setBlockTags(BlockTags.FENCE_GATES));
        addBlockEntry(new BlockEntry(
                BLOCK_BUTTON,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseWoodenButtonBlock(
                        getBlock(BLOCK_PLANKS),
                        woodType.setType()
                )
        ).setBlockTags(BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS)
         .setItemTags(ItemTags.BUTTONS, ItemTags.WOODEN_BUTTONS));
        addBlockEntry(new BlockEntry(
                BLOCK_PRESSURE_PLATE,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.WoodenPressurePlateBlock(getBlock(
                        BLOCK_PLANKS), woodType.setType())
        ).setBlockTags(BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES)
         .setItemTags(ItemTags.WOODEN_PRESSURE_PLATES));
        addBlockEntry(new BlockEntry(
                BLOCK_TRAPDOOR,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseTrapdoorBlock(
                        getBlock(BLOCK_PLANKS),
                        woodType.setType()
                )
        ).setBlockTags(BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS)
         .setItemTags(ItemTags.TRAPDOORS, ItemTags.WOODEN_TRAPDOORS));
        addBlockEntry(new BlockEntry(
                BLOCK_DOOR,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseDoorBlock(
                        getBlock(BLOCK_PLANKS),
                        woodType.setType()
                )
        ).setBlockTags(BlockTags.DOORS, BlockTags.WOODEN_DOORS)
         .setItemTags(ItemTags.DOORS, ItemTags.WOODEN_DOORS));


        addBlockEntry(new BlockEntry(
                BLOCK_LADDER,
                (complexMaterial, settings) -> new BaseLadderBlock(getBlock(BLOCK_PLANKS))
        ).setBlockTags(BlockTags.CLIMBABLE));
        addBlockEntry(new BlockEntry(
                BLOCK_SIGN,
                (complexMaterial, settings) -> new org.betterx.bclib.blocks.BaseSignBlock(woodType)
        ).setBlockTags(BlockTags.SIGNS).setItemTags(ItemTags.SIGNS));
    }

    @Override
    public void initDefaultRecipes() {
        Block planks = getBlock(BLOCK_PLANKS);
        addRecipeEntry(new RecipeEntry(BLOCK_PLANKS, (material, id) -> {
            Block log = getBlock(BLOCK_STEM);

            BCLRecipeBuilder.crafting(id, planks)
                            .setOutputCount(4)
                            .shapeless()
                            .addMaterial('#', log)
                            .setGroup("planks")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_STAIRS, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_STAIRS))
                            .setOutputCount(4)
                            .setShape("#  ", "## ", "###")
                            .addMaterial('#', planks)
                            .setGroup(receipGroupPrefix + "stairs")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_SLAB, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_SLAB))
                            .setOutputCount(6)
                            .setShape("###")
                            .addMaterial('#', planks)
                            .setGroup("slab")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_FENCE, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_FENCE))
                            .setOutputCount(3)
                            .setShape("#I#", "#I#")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("fence")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_GATE, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_GATE))
                            .setShape("I#I", "I#I")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("gate")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BUTTON, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BUTTON))
                            .shapeless()
                            .addMaterial('#', planks)
                            .setGroup("button")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_PRESSURE_PLATE, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_PRESSURE_PLATE))
                            .setShape("##")
                            .addMaterial('#', planks)
                            .setGroup("pressure_plate")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_TRAPDOOR, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_TRAPDOOR))
                            .setOutputCount(2)
                            .setShape("###", "###")
                            .addMaterial('#', planks)
                            .setGroup("trapdoor")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_DOOR, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_DOOR))
                            .setOutputCount(3)
                            .setShape("##", "##", "##")
                            .addMaterial('#', planks)
                            .setGroup("door")
                            .setCategory(RecipeCategory.REDSTONE)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_CRAFTING_TABLE, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_CRAFTING_TABLE))
                            .setShape("##", "##")
                            .addMaterial('#', planks)
                            .setGroup("table")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_LADDER, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_LADDER))
                            .setOutputCount(3)
                            .setShape("I I", "I#I", "I I")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("ladder")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_SIGN, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_SIGN))
                            .setOutputCount(3)
                            .setShape("###", "###", " I ")
                            .addMaterial('#', planks)
                            .addMaterial('I', Items.STICK)
                            .setGroup("sign")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_CHEST, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_CHEST))
                            .setShape("###", "# #", "###")
                            .addMaterial('#', planks)
                            .setGroup("chest")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BARREL, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BARREL))
                            .setShape("#S#", "# #", "#S#")
                            .addMaterial('#', planks)
                            .addMaterial('S', getBlock(BLOCK_SLAB))
                            .setGroup("barrel")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_BOOKSHELF, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_BOOKSHELF))
                            .setShape("###", "PPP", "###")
                            .addMaterial('#', planks)
                            .addMaterial('P', Items.BOOK)
                            .setGroup("bookshelf")
                            .setCategory(RecipeCategory.BUILDING_BLOCKS)
                            .build();
        }));
        addRecipeEntry(new RecipeEntry(BLOCK_COMPOSTER, (material, id) -> {
            BCLRecipeBuilder.crafting(id, getBlock(BLOCK_COMPOSTER))
                            .setShape("# #", "# #", "###")
                            .addMaterial('#', getBlock(BLOCK_SLAB))
                            .setGroup("composter")
                            .setCategory(RecipeCategory.DECORATIONS)
                            .build();
        }));

        initDefaultFurniture();
    }

    public Block getStem() {
        return getBlock(BLOCK_STEM);
    }
}
