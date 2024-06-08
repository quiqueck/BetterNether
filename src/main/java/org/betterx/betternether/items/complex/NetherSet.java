package org.betterx.betternether.items.complex;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.bclib.items.tool.BaseShearsItem;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.items.*;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.complex.api.tool.*;

public class NetherSet extends EquipmentSet {
    public NetherSet(
            String prefix,
            ToolTier toolTier,
            ArmorTier armorTier,
            boolean withShears
    ) {
        this(prefix, toolTier, armorTier, withShears, null);
    }

    public NetherSet(
            String prefix,
            ToolTier toolTier,
            ArmorTier armorTier,
            boolean withShears,
            EquipmentSet templateBaseSet
    ) {
        super(
                BetterNether.C,
                prefix,
                toolTier,
                armorTier,
                NetherBlocks.NETHER_REED_STEM,
                templateBaseSet
        );


        if (toolTier != null) {
            ToolSlot.PropertiesBuilder toolPropertiesBuilder = (slot, tier) -> {
                var values = tier.getValues(slot);
                if (values == null) {
                    throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
                }
                if (slot == ToolSlot.SWORD_SLOT)
                    return NetherItems.createDefaultNetherSwordSettings(tier.toolTier, values.attackDamage(), values.attackSpeed());

                return NetherItems.createDefaultNetherToolSettings(tier.toolTier, values.attackDamage(), values.attackSpeed());
            };

            ToolSlot.PropertiesBuilder shearPropertiesBuilder = (slot, tier) -> toolPropertiesBuilder
                    .build(slot, tier)
                    .durability((int) (tier.toolTier.getUses() * 0.75));

            add(ToolSlot.PICKAXE_SLOT, NetherPickaxe::new, toolPropertiesBuilder);
            add(ToolSlot.AXE_SLOT, NetherAxe::new, toolPropertiesBuilder);
            add(ToolSlot.SHOVEL_SLOT, NetherShovel::new, toolPropertiesBuilder);
            add(ToolSlot.HOE_SLOT, NetherHoe::new, toolPropertiesBuilder);
            add(ToolSlot.SWORD_SLOT, NetherSword::new, toolPropertiesBuilder);


            if (withShears) {
                add(ToolSlot.SHEARS_SLOT, (t, p) -> new BaseShearsItem(p), shearPropertiesBuilder);
            }
        }

        if (armorTier != null) {
            ArmorSlot.PropertiesBuilder armorPropertiesBuilder = (slot, tier) -> {
                var values = tier.getValues(slot);
                if (values == null) {
                    throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
                }

                return NetherItems.createDefaultNetherArmorSettings(slot.armorType, values.durability());
            };

            add(ArmorSlot.HELMET_SLOT, NetherArmor::new, armorPropertiesBuilder);
            add(ArmorSlot.CHESTPLATE_SLOT, NetherArmor::new, armorPropertiesBuilder);
            add(ArmorSlot.LEGGINGS_SLOT, NetherArmor::new, armorPropertiesBuilder);
            add(ArmorSlot.BOOTS_SLOT, NetherArmor::new, armorPropertiesBuilder);
        }
    }

    //WoVer does not yet have an AdvancementManager, so we need to create a wrapper for the BCLib AdvancementManager
    //-------------

    private AdvancementManager.Builder addEquipmentSetSlotCriterion(AdvancementManager.Builder builder, ToolSlot slot) {
        return builder.addInventoryChangedCriterion(
                this.baseName + "_" + slot,
                this.get(slot)
        );
    }

    private AdvancementManager.Builder addEquipmentSetSlotCriterion(
            AdvancementManager.Builder builder,
            ArmorSlot slot
    ) {
        return builder.addInventoryChangedCriterion(
                this.baseName + "_" + slot,
                this.get(slot)
        );
    }

    public AdvancementManager.Builder addArmorSetCriterion(AdvancementManager.Builder builder) {
        addEquipmentSetSlotCriterion(builder, ArmorSlot.HELMET_SLOT);
        addEquipmentSetSlotCriterion(builder, ArmorSlot.CHESTPLATE_SLOT);
        addEquipmentSetSlotCriterion(builder, ArmorSlot.LEGGINGS_SLOT);
        addEquipmentSetSlotCriterion(builder, ArmorSlot.BOOTS_SLOT);
        return builder;
    }

    public AdvancementManager.Builder addToolSetCriterion(AdvancementManager.Builder builder) {
        addEquipmentSetSlotCriterion(builder, ToolSlot.PICKAXE_SLOT);
        addEquipmentSetSlotCriterion(builder, ToolSlot.AXE_SLOT);
        addEquipmentSetSlotCriterion(builder, ToolSlot.SHOVEL_SLOT);
        addEquipmentSetSlotCriterion(builder, ToolSlot.SWORD_SLOT);
        addEquipmentSetSlotCriterion(builder, ToolSlot.HOE_SLOT);
        return builder;
    }
}
