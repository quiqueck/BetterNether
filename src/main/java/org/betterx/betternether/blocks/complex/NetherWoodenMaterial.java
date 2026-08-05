package org.betterx.betternether.blocks.complex;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.behaviour.MineableWithTagTrait;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.tag.api.predefined.MineableTags;
import org.betterx.bclib.furniture.slots.BarStool;
import org.betterx.bclib.furniture.slots.Chair;
import org.betterx.bclib.furniture.slots.Taburet;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.complex.slots.NetherSlots;
import org.betterx.betternether.blocks.complex.slots.NetherWoodSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

/**
 * Base wooden material for BetterNether, on top of the wover-sets-api {@link WoodenBlockSet}.
 * <p>
 * Differs from the vanilla-wood default in three ways: Nether wood does not burn (no
 * {@link BlockTraits#FLAMMABLE} trait), and the set also carries a {@code wall} slot as well as the wooden
 * furniture slots (see {@link #addFurniture}).
 */
public class NetherWoodenMaterial<T extends NetherWoodenMaterial<T>> extends WoodenBlockSet<T> {


    protected final MapColor plankColor;
    protected Block furnitureCloth = Blocks.RED_WOOL;

    public NetherWoodenMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(BetterNether.C, name, woodColor, SlotType.PLANKS, true);
        setPlanksColor(planksColor);
        this.plankColor = planksColor;
    }

    /**
     * Builds and registers every slot in this set. Kept named {@code init()} for parity with the
     * previous complex-material API so the {@code NetherBlocks} call sites read the same.
     */
    public T init() {
        return this.buildAndRegister();
    }

    /**
     * Sets the block whose texture the upholstery of this set's furniture (chair/bar-stool) uses. Must be
     * called before {@link #init()}, since that is when the slots are built.
     */
    public T setFurnitureCloth(Block clothMaterial) {
        this.furnitureCloth = clothMaterial;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Adds the three wooden furniture slots (taburet/chair/bar-stool) to {@code slots}. The slot classes live in
     * bclib (BetterEnd shares them); wover-sets-api has no furniture of its own. Each is built from this set's
     * slab and textured with its planks, matching what {@code WoodSlots.TABURET}/{@code CHAIR}/{@code BAR_STOOL}
     * used to do before the wover migration.
     *
     * @param slots the map to add to
     * @return {@code slots}, for chaining
     */
    protected SlotMap addFurniture(SlotMap slots) {
        return slots
                .add(new Taburet())
                // read the cloth lazily: subclasses may set it after this map is built
                .add(new Chair(() -> furnitureCloth))
                .add(new BarStool(() -> furnitureCloth));
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // Deliberately NOT calling super: the base adds BlockTraits.FLAMMABLE, but nothing burns in the Nether.
        // netherWood() is withDefault() minus FLAMMABLE. Nothing burns in the nether: the wood sets
        // used to say so through initFlammable(), which was a deliberate no-op, and that hook is gone -
        // WOOD_BLOCK.withDefault() would silently make every nether wood flammable. Datagen output is the
        // same either way; both variants still declare MINEABLE_WITH.needsAxe().
        //
        // Sapling/seed slots are the exception (WP: mineable-audit §B): they are plants, not wood, and their
        // own slot config (NetherSlots's Sapling class) already adds mineable/hoe - keeping WOOD_BLOCK's axe
        // tag on top double-tags them (axe+hoe), whereas BetterEnd's saplings are hoe-only. Filter the axe
        // trait back out for those two slots while keeping the rest of netherWood()'s bundle (mapColor/
        // instrument/strength/sound) untouched.
        boolean isSaplingLike = slot.equals(NetherSlots.SAPLING) || slot.equals(NetherSlots.SEED);
        List<BlockTrait<?, ?>> woodTraits = BlockTraits.WOOD_BLOCK.netherWood();
        if (woodTraits != null) {
            for (BlockTrait<?, ?> trait : woodTraits) {
                if (isSaplingLike
                        && trait instanceof MineableWithTagTrait mineableTrait
                        && mineableTrait.mineableTag().equals(MineableTags.AXE)) {
                    continue;
                }
                blockDefinition.addTrait(trait);
            }
        }
        // WP8.3 (REVIEWED): WOOD_BLOCK's Trait.configure() sets sound(SoundType.WOOD) unconditionally; a
        // chained sound(...) call queued after it (BlockDefinition interleaves traits/setters in call order -
        // see BlockDefinition#addTrait) wins. soundOverride(slot) returns null for {@link
        // org.betterx.betternether.blocks.complex.slots.VanillaFallback} sets (vanilla-wood furniture, out of
        // this review's scope), so the sound(...) call is skipped there and those blocks keep whatever
        // sound their copied/trait-default properties already carry.
        SoundType sound = soundOverride(slot);
        if (sound != null) blockDefinition.sound(sound);
        // addTrait(null) is a documented no-op (BlockDefinition#addTrait): bark/log/stem/trunk slots (absent
        // from FUEL_TICKS) get no fuel trait at all, matching decision 6.
        blockDefinition.addTrait(fuelTrait(slot));
        if (slot == SlotType.PLANKS || isFurniture(slot)) {
            // The furniture is made of (and pre-migration copied its properties from) the planks/slab, so it
            // takes the plank color rather than the log's.
            blockDefinition.mapColor(plankColor);
        } else {
            blockDefinition.mapColor(woodColor);
        }
    }

    private static boolean isFurniture(SlotType slot) {
        return slot == SlotType.TABURET || slot == SlotType.CHAIR || slot == SlotType.BAR_STOOL;
    }

    /**
     * The stem/hyphae-family slots (log/bark, stripped or not) - the vanilla-analogous {@code SoundType.STEM}
     * group. Every other slot in the set is a planks-derived part and uses {@code SoundType.NETHER_WOOD}.
     */
    private static boolean isStemSlot(SlotType slot) {
        return slot == SlotType.LOG
                || slot == SlotType.BARK
                || slot == SlotType.STRIPPED_LOG
                || slot == SlotType.STRIPPED_BARK;
    }

    /**
     * WP8.3 (REVIEWED): the {@code sound(...)} override applied for the given slot, or {@code null} to leave
     * whatever sound the block already has (from {@code WOOD_BLOCK}'s trait default or a copied source
     * block's properties) untouched. Matches vanilla's crimson/warped split: the stem/hyphae-family blocks
     * (log/bark, stripped or not) use {@code SoundType.STEM}, every planks-derived part (including this set's
     * wall/furniture, which have no direct vanilla analog but are built from planks) uses
     * {@code SoundType.NETHER_WOOD} - see {@code BlockSetType.CRIMSON}/{@code WARPED.soundType()} and
     * {@code Blocks.CRIMSON_STEM}/{@code CRIMSON_PLANKS} in vanilla.
     * <p>
     * Overridden to {@code null} (no override) by
     * {@link org.betterx.betternether.blocks.complex.slots.VanillaFallback}: the vanilla-alignment review's
     * wood-sound scope is this mod's own 9 nether-wood species, not the vanilla-wood-fallback furniture sets
     * (overworld wood taburets/chairs/bar-stools, or the crimson/warped furniture built on vanilla nether
     * wood), which are left exactly as they were.
     *
     * @param slot the slot being configured
     * @return the sound to apply, or {@code null} to leave the current sound untouched
     */
    protected SoundType soundOverride(SlotType slot) {
        return isStemSlot(slot) ? SoundType.STEM : SoundType.NETHER_WOOD;
    }

    /**
     * WP8.3 (REVIEWED): door/trapdoor/button/pressure-plate (via {@code BlockSetType.soundType()}) and fence
     * gate/sign (via {@code WoodType.soundType()}) all bake their sound into this set's derived
     * set-type/wood-type rather than reading the block's own {@code sound(...)} - see
     * {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet#setTypeSound()}. Every one of those slots is
     * planks-derived, so {@code NETHER_WOOD} (never {@code STEM}) is correct here unconditionally.
     */
    @Override
    protected SoundType setTypeSound() {
        return SoundType.NETHER_WOOD;
    }

    /**
     * WP8.3 fuel policy (decision 6): every slot in {@link #FUEL_TICKS} becomes furnace fuel at its
     * vanilla-equivalent tick count; bark/log/stem/trunk (not in the map) stay non-fuel.
     */
    @Override
    protected BlockTrait<?, ?> fuelTrait(SlotType slot) {
        if (slot == SlotType.LOG || slot == SlotType.BARK) return null;
        return super.fuelTrait(slot);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        // The replaced slots keep their default block/recipe/tags and only opt out of model generation:
        // BetterNether hand-authors their blockstates and models (see NetherWoodSlots).
        return addFurniture(super.createDefaultDefinitions()
                                 .add(WoodSlots.WALL)
                                 .replace(new NetherWoodSlots.Ladder())
                                 .replace(new NetherWoodSlots.Trapdoor())
                                 .replace(new NetherWoodSlots.Log(true))
                                 .replace(new NetherWoodSlots.Log(false))
                                 .replace(new NetherWoodSlots.Bark(true))
                                 .replace(new NetherWoodSlots.Bark(false)));
    }

    public Block getPlanks() {
        return getBlock(SlotType.PLANKS);
    }

    public Block getSlab() {
        return getBlock(SlotType.SLAB);
    }

    public Block getLog() {
        return getBlock(SlotType.LOG);
    }

    public Block getBark() {
        return getBlock(SlotType.BARK);
    }

    public Block getStrippedLog() {
        return getBlock(SlotType.STRIPPED_LOG);
    }

    public Block getStrippedBark() {
        return getBlock(SlotType.STRIPPED_BARK);
    }
}
