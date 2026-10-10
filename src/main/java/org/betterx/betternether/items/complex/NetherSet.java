package org.betterx.betternether.items.complex;

import org.betterx.betternether.registry.block.NetherWoodBlocks;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.items.*;
import org.betterx.betternether.registry.NetherBlocks;
import org.betterx.betternether.registry.NetherTags;
import de.ambertation.wover.complex.api.equipment.*;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

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
                NetherWoodBlocks.NETHER_REED_STEM,
                templateBaseSet == null ? null : (Supplier<EquipmentSet>) () -> templateBaseSet
        );


        if (toolTier != null) {
            add(
                    ToolSlot.PICKAXE_SLOT,
                    (definition, values) -> {
                        // NETHER_PICKAXES used to be applied through the retired ItemTagProvider interface;
                        // add it directly to the definition here instead (the item registers with this tag).
                        definition.addTags(NetherTags.NETHER_PICKAXES);
                        return new NetherPickaxe(
                                toolTier.toolMaterial,
                                commonToolProperties(definition.getProperties())
                        );
                    }
            );
            add(
                    ToolSlot.AXE_SLOT,
                    (definition, values) -> new NetherAxe(
                            toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    )
            );
            add(
                    ToolSlot.SHOVEL_SLOT,
                    (definition, values) -> new NetherShovel(
                            toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    )
            );
            add(
                    ToolSlot.HOE_SLOT,
                    (definition, values) -> new NetherHoe(
                            toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    )
            );
            add(
                    ToolSlot.SWORD_SLOT,
                    (definition, values) -> new NetherSword(
                            toolTier.toolMaterial,
                            commonToolProperties(definition.getProperties())
                    )
            );

            if (withShears) {
                add(
                        ToolSlot.SHEARS_SLOT,
                        (definition, values) -> new ShearsItem(
                                commonToolProperties(definition.getProperties())
                                        .durability((int) (toolTier.toolMaterial.durability() * 0.75))
                                        .component(DataComponents.TOOL, shearsTool(toolTier.toolMaterial))
                        )
                );
            }
        }

        if (armorTier != null) {
            add(
                    ArmorSlot.HELMET_SLOT,
                    (definition) -> new NetherArmor(armorTier.armorMaterial, commonArmorProperties(definition.getProperties()))
            );
            add(
                    ArmorSlot.CHESTPLATE_SLOT,
                    (definition) -> new NetherArmor(armorTier.armorMaterial, commonArmorProperties(definition.getProperties()))
            );
            add(
                    ArmorSlot.LEGGINGS_SLOT,
                    (definition) -> new NetherArmor(armorTier.armorMaterial, commonArmorProperties(definition.getProperties()))
            );
            add(
                    ArmorSlot.BOOTS_SLOT,
                    (definition) -> new NetherArmor(armorTier.armorMaterial, commonArmorProperties(definition.getProperties()))
            );
        }
    }

    /**
     * The generic digger tool component only speeds up {@code #wover:mineable/shears}, so on vanilla leaves,
     * wool, vines and cobwebs these shears were no faster than a bare hand - and Efficiency, which only
     * applies when the base speed is above 1, did nothing (quiqueck/BetterNether#181). Keep vanilla's shear
     * rules first and add the mineable tag at the material's speed behind them.
     */
    private static Tool shearsTool(ToolMaterial material) {
        final Tool vanilla = ShearsItem.createToolProperties();
        final HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        final List<Tool.Rule> rules = new ArrayList<>(vanilla.rules());
        rules.add(Tool.Rule.minesAndDrops(blocks.getOrThrow(MineableTags.SHEARS), material.speed()));
        return new Tool(rules, vanilla.defaultMiningSpeed(), vanilla.damagePerBlock(), vanilla.canDestroyBlocksInCreative());
    }

    @Override
    public @NotNull Item.Properties commonToolProperties(@NotNull Item.Properties properties) {
        return properties.fireResistant();
    }

    //WoVer does not yet have an AdvancementManager, so we need to create a wrapper for the BCLib AdvancementManager
    //-------------

    private AdvancementManager.Builder addEquipmentSetSlotCriterion(AdvancementManager.Builder builder, ToolSlot slot) {
        return builder.addInventoryChangedCriterion(
                this.baseName + "_" + slot.name,
                this.get(slot)
        );
    }

    private AdvancementManager.Builder addEquipmentSetSlotCriterion(
            AdvancementManager.Builder builder,
            ArmorSlot slot
    ) {
        return builder.addInventoryChangedCriterion(
                this.baseName + "_" + slot.name,
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
