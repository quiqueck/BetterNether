package org.betterx.betternether.blocks;

import org.betterx.bclib.trait.TraitLists;
import org.betterx.bclib.trait.block.RecipeTraits;
import org.betterx.betternether.BetterNether;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * BetterNether-local composite trait bundles for the mod's remaining one-off {@code register*} helpers
 * ({@code registerRoof}, {@code registerSoulBlock}, {@code registerTrimmedChest}, {@code registerFireBowl}), so
 * Phase 5 can inline each call site as a definition chain plus a single {@code .addTraits(...)} instead of a
 * trait-hiding helper.
 * <p>
 * Modelled on {@link org.betterx.bclib.trait.block.TerrainTraits}/{@code FurnitureTraits}: only the actual
 * <em>traits</em> each helper added (loot, recipe, model, classification) are bundled here. Everything that is not
 * a trait stays at the registration site, exactly as it does there:
 * <ul>
 *   <li>the block factory and {@code replacePropertiesWithCopy(source)} (constructor/definition concerns);</li>
 *   <li>the per-site material bundle ({@code NetherMaterial.stone()}/{@code cincinnasite()}/… - it varies per
 *       call and is itself a trait list, added alongside), and any plain {@code addTags(...)};</li>
 *   <li>the furnace-fuel side effect ({@code addFuel(source, block)}), which per user decision 6 becomes an
 *       explicit per-site {@code FuelBlockTrait} decision rather than an implicit bundle member.</li>
 * </ul>
 */
public class NetherCompositeTraits {
    private NetherCompositeTraits() {
    }

    /**
     * The trait bundle {@code registerRoof} added on top of the copied source properties and the per-site
     * material: a plain self-drop loot table and the {@code roof_tile}-group crafting recipe from {@code source}.
     * The material bundle, block factory and the (decision-6) fuel decision stay at the call site.
     *
     * @param source the base block the roof tile is crafted from
     * @return self-drop loot + roof recipe
     */
    public static List<BlockTrait<?, ?>> roof(Block source) {
        return TraitLists.of(
                BlockTraits.LOOT_TABLE.dropSelf(),
                RecipeTraits.roofFrom(source)
        );
    }

    /**
     * The trait bundle {@code registerSoulBlock} contributed: the full stone classification
     * ({@code NetherMaterial.stone()}) plus the two soul-fire tags ({@link BlockTags#SOUL_FIRE_BASE_BLOCKS},
     * {@link BlockTags#SOUL_SPEED_BLOCKS}). The old helper added the tags through {@code addTags(...)}; expressed
     * here as a {@code BLOCK_TAG} trait (the same pattern {@code NetherMaterial.immobile()}/{@code obsidian()}
     * use), which produces the identical tag membership.
     *
     * @return stone classification + soul-fire/soul-speed tags
     */
    public static List<BlockTrait<?, ?>> soulBlock() {
        return TraitLists.and(
                NetherMaterial.stone(),
                BlockTraits.BLOCK_TAG.with(List.of(
                        BlockTags.SOUL_FIRE_BASE_BLOCKS,
                        BlockTags.SOUL_SPEED_BLOCKS
                ))
        );
    }

    /**
     * The trait bundle {@code registerTrimmedChest} added on top of the copied planks properties: the wood
     * classification, the chest classification, the shapeless {@code chest}-group recipe (one {@code chest} in a
     * trim of {@code trim}), and the chest block model built from {@code planks}.
     * <p>
     * WP8.3 (REVIEWED): uses {@code WOOD_BLOCK.netherWood()} rather than {@code withDefault()} - the
     * latter bundles its own {@code FLAMMABLE.withDefault()} (5/5), which made
     * {@code mushroom_fir_trimmed_chest} the only flammable block in all of BetterNether (nether wood never
     * burns, decision 6; this chest was an oversight, per the vanilla-alignment review). No other nether-wood
     * material/composite in this mod grants chests fire resistance via a second trait: unlike
     * {@code BlockRegistry.registerAsFlammable}, simply never adding {@code FLAMMABLE} in the first place needs
     * no such workaround.
     *
     * @param planks the planks whose properties/model back this chest
     * @param chest  the chest consumed by the recipe
     * @param trim   the trim block consumed by the recipe
     * @return wood classification + chest classification + trim recipe + chest model
     */
    public static List<BlockTrait<?, ?>> trimmedChest(Block planks, Block chest, Block trim) {
        return TraitLists.and(
                TraitLists.concat(BlockTraits.WOOD_BLOCK.netherWood(), BlockTraits.CHEST_BLOCK.withDefault()),
                BlockTraits.RECIPE.with((key, block, context) -> RecipeBuilder
                        .crafting(key.location(), block)
                        .shapeless()
                        .addMaterial('C', chest)
                        .addMaterial('#', trim)
                        .group("chest")
                        .outputCount(1)
                        .category(RecipeCategory.DECORATIONS)
                        .build(context)),
                ModelTraitLibrary.chest(() -> planks)
        );
    }

    /**
     * The trait bundle {@code registerFireBowl} added on top of the copied source properties and the per-site
     * material: the cutout render layer and - for the craftable (non-netherite) bowls - the {@code fire_bowl}
     * crafting recipe (a ring of {@code source} around an {@code inside} filling on a pair of {@code leg} legs).
     * The material bundle and block factory stay at the call site.
     *
     * @param source     the material forming the bowl's rim (and the recipe's ring)
     * @param inside     the block filling the bowl in the recipe
     * @param leg        the item forming the bowl's legs in the recipe
     * @param withRecipe whether to emit the crafting recipe (the netherite bowls pass {@code false} - they have
     *                   no recipe, matching {@code registerFireBowl}'s {@code !isNetherite} guard)
     * @return cutout render layer, plus the fire-bowl recipe when {@code withRecipe}
     */
    public static List<BlockTrait<?, ?>> fireBowl(Block source, Block inside, Item leg, boolean withRecipe) {
        return TraitLists.and(
                NetherRender.cutout(),
                withRecipe
                        ? BlockTraits.RECIPE.with((key, block, context) ->
                        new RecipeBuilder.Templates(context, BetterNether.C).makeFireBowlRecipe(source, inside, leg, block))
                        : null
        );
    }
}
