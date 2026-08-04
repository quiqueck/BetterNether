package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;

import net.minecraft.world.level.material.MapColor;

/**
 * A "vanilla nether wood" set: adds the extra wooden blocks (crafting table, bookshelf, composter, chests,
 * barrel, ladder, boats) and the wooden furniture on top of vanilla planks/logs. The custom bookshelf was
 * removed in 21.7; the default {@link WoodSlots#BOOKSHELF} is used instead.
 */
public class VanillaNetherWood extends VanillaFallback<VanillaNetherWood> {
    public VanillaNetherWood(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return addFurniture(SlotMap.of(
                WoodSlots.CRAFTING_TABLE,
                WoodSlots.BOOKSHELF,
                WoodSlots.COMPOSTER,
                WoodSlots.CHEST,
                WoodSlots.BARREL,
                new NetherWoodSlots.Ladder(),
                WoodSlots.BOAT,
                WoodSlots.CHEST_BOAT
        ));
    }
}
