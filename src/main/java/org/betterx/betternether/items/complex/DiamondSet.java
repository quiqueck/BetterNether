package org.betterx.betternether.items.complex;

import org.betterx.betternether.items.materials.BNToolTiers;
import org.betterx.wover.complex.api.tool.EquipmentSet;

import org.jetbrains.annotations.NotNull;

public class DiamondSet extends NetherSet {
    public DiamondSet(EquipmentSet set) {
        super(
                set.baseName,
                BNToolTiers.CINCINNASITE_DIAMOND,
                null,
                false,
                set
        );
    }

    @Override
    protected @NotNull String nameForSlot(String slotName) {
        return baseName + "_" + slotName + "_diamond";
    }
}
