package org.betterx.betternether.blocks.complex.slots;

import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * A "vanilla wood" set: adds only the wooden furniture (taburet/chair/bar-stool) on top of vanilla planks/slabs,
 * which {@link VanillaFallback} resolves from the vanilla registry.
 */
public class VanillaWood extends VanillaFallback<VanillaWood> {
    public VanillaWood(
            String name,
            MapColor woodColor,
            MapColor planksColor
    ) {
        super(name, woodColor, planksColor);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return addFurniture(SlotMap.of());
    }

    public static VanillaWood create(String baseName, Block clothMaterial) {
        Block plank = getVanillaBlock(baseName, SlotType.PLANKS.suffix());
        return new VanillaWood(baseName, plank.defaultMapColor(), plank.defaultMapColor())
                .setFurnitureCloth(clothMaterial)
                .init();
    }
}
