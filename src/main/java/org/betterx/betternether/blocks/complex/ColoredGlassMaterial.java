package org.betterx.betternether.blocks.complex;

import org.betterx.bclib.blocks.BaseGlassBlock;
import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.GlassBlockTrait;
import org.betterx.betternether.blocks.BNPane;
import org.betterx.betternether.blocks.NetherModels;
import org.betterx.betternether.blocks.NetherRender;
import org.betterx.betternether.recipes.RecipesHelper;
import org.betterx.betternether.registry.NetherBlocks;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ColoredGlassMaterial {
    public final Block white;
    public final Block orange;
    public final Block magenta;
    public final Block light_blue;
    public final Block yellow;
    public final Block lime;
    public final Block pink;
    public final Block gray;
    public final Block light_gray;
    public final Block cyan;
    public final Block purple;
    public final Block blue;
    public final Block brown;
    public final Block green;
    public final Block red;
    public final Block black;

    /**
     * Full Block Constructor
     *
     * @param name - base name of block (prefix) and it's group
     * @param base - block base for material properties and crafting
     */
    public <T extends Block> ColoredGlassMaterial(String name, Block base) {
        white = makeInstance(name, base, Items.WHITE_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        orange = makeInstance(name, base, Items.ORANGE_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        magenta = makeInstance(name, base, Items.MAGENTA_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        light_blue = makeInstance(name, base, Items.LIGHT_BLUE_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        yellow = makeInstance(name, base, Items.YELLOW_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        lime = makeInstance(name, base, Items.LIME_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        pink = makeInstance(name, base, Items.PINK_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        gray = makeInstance(name, base, Items.GRAY_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        light_gray = makeInstance(name, base, Items.LIGHT_GRAY_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        cyan = makeInstance(name, base, Items.CYAN_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        purple = makeInstance(name, base, Items.PURPLE_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        blue = makeInstance(name, base, Items.BLUE_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        brown = makeInstance(name, base, Items.BROWN_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        green = makeInstance(name, base, Items.GREEN_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        red = makeInstance(name, base, Items.RED_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
        black = makeInstance(name, base, Items.BLACK_DYE, true, null, RecipeCategory.BUILDING_BLOCKS);
    }

    /**
     * Pane Block Constructor
     *
     * @param name        - base name of block (prefix) and it's group
     * @param base        - block base for material properties and crafting
     * @param paneFactory - builds the coloured pane, e.g. {@code p -> new BNPane(p)}. This
     *                    used to be inferred from {@code base} by the retired material-dispatch helper,
     *                    which picked the material by testing marker interfaces and, failing those, the
     *                    note-block instrument - so a BaseGlassBlock base fell through to the *wood* branch. The
     *                    material is known here, so it is passed directly instead. (BNPane also took a
     *                    drop-itself flag until its getDrops() override was dropped; the loot now comes
     *                    entirely from {@code paneTraits}.)
     * @param paneProperties - chained property setters applied to each colored pane's definition (e.g.
     *                       {@code def -> { def.strength(0.3F, 0.3F); def.noOcclusion(); }}), applied once per
     *                       dye color inside {@link #makeInstance}. There is no single {@code BlockDefinition}
     *                       at this call site (one gets built per color), so - unlike a one-shot registration -
     *                       the override can't be written as a chained setter here directly; this callback is
     *                       the visible equivalent, applied at the same call-order position every trait bundle
     *                       used to occupy via {@code NetherProps}. May be {@code null} for no extra properties.
     */
    public <T extends Block> ColoredGlassMaterial(
            String name,
            Block base,
            Function<BlockBehaviour.Properties, BNPane> paneFactory,
            List<BlockTrait<?, ?>> paneTraits,
            Consumer<BlockDefinition<?, ?>> paneProperties
    ) {
        this.paneTraits = paneTraits;
        this.paneProperties = paneProperties;
        white = makeInstance(name, base, Items.WHITE_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        orange = makeInstance(name, base, Items.ORANGE_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        magenta = makeInstance(name, base, Items.MAGENTA_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        light_blue = makeInstance(name, base, Items.LIGHT_BLUE_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        yellow = makeInstance(name, base, Items.YELLOW_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        lime = makeInstance(name, base, Items.LIME_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        pink = makeInstance(name, base, Items.PINK_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        gray = makeInstance(name, base, Items.GRAY_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        light_gray = makeInstance(name, base, Items.LIGHT_GRAY_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        cyan = makeInstance(name, base, Items.CYAN_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        purple = makeInstance(name, base, Items.PURPLE_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        blue = makeInstance(name, base, Items.BLUE_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        brown = makeInstance(name, base, Items.BROWN_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        green = makeInstance(name, base, Items.GREEN_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        red = makeInstance(name, base, Items.RED_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
        black = makeInstance(name, base, Items.BLACK_DYE, false, paneFactory, RecipeCategory.DECORATIONS);
    }

    /** The material traits for the pane variant (see {@link NetherMaterial}); empty for the full block. */
    private List<BlockTrait<?, ?>> paneTraits = List.of();
    /** Chained property setters for the pane variant (see the pane constructor); {@code null} for the full block. */
    private Consumer<BlockDefinition<?, ?>> paneProperties = null;

    private Block makeInstance(
            String group,
            Block base,
            Item dye,
            boolean isFullBlock,
            Function<BlockBehaviour.Properties, BNPane> paneFactory,
            RecipeCategory category
    ) {
        String name = group + "_" + ((DyeItem) dye).getDyeColor().getSerializedName();

        // Both the full-block variant (BaseGlassBlock) and the pane variant render translucent. Only the
        // full block carries the glass model trait - the panes get theirs from BNPane's own provider.
        var definition = NetherBlocks
                .defineBlock(name, p -> isFullBlock ? new BaseGlassBlock(p) : paneFactory.apply(p))
                .replacePropertiesWithCopy(base);
        if (isFullBlock) {
            // BaseGlassBlock used to drop itself only when silk-touched through the retired
            // BlockLootProvider interface; that loot is now a wover trait. The translucent render layer
            // used to come from BaseGlassBlock's RenderLayerProvider interface; it is now the
            // RENDER_LAYER trait.
            definition
                    .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
                    .addTrait(NetherModels.quartzGlass())
                    .addTrait(BlockTraits.LOOT_TABLE.silkTouchSelf())
                    // BaseGlassBlock used to get mineable/pickaxe from the retired AddMineablePickaxe marker.
                    .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                    // noOcclusion()/explosionResistance(0.3)/isSuffocating/isViewBlocking moved out of the
                    // BaseGlassBlock constructor (R1). The base copied here is quartz_glass_framed, whose
                    // own properties descend from opaque CINCINNASITE_BLOCK, so all 16 colours inherited
                    // its redstone conduction and mob spawning until this trait set the missing two calls.
                    .addTrait(GlassBlockTrait.glass(0.3f));
        } else {
            definition.addTrait(NetherRender.translucent()).addTrait(paneTraits);
            if (paneProperties != null) paneProperties.accept(definition);
        }
        Block block = definition.buildAndRegister();
        if (ModCore.isDatagen())
            RecipesHelper.makeColoringRecipe(base, block, dye, group, category);

        return block;
    }
}