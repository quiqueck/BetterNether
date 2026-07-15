package org.betterx.betternether.blocks.complex.slots;

import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * A "vanilla wood" set that used to add the wooden furniture (taburet/chair/bar-stool). Those slots were
 * removed from wover-sets-api in 21.7, so this set now registers nothing; kept so existing call sites compile.
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
        return SlotMap.of();
    }

    public static VanillaWood create(String baseName, Block clothMaterial) {
        Block plank = getVanillaBlock(baseName, SlotType.PLANKS.suffix());
        return new VanillaWood(baseName, plank.defaultMapColor(), plank.defaultMapColor())
                .setFurnitureCloth(clothMaterial)
                .init();
    }
}
