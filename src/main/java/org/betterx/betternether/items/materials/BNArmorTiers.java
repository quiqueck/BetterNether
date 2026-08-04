package org.betterx.betternether.items.materials;

import org.betterx.betternether.registry.NetherTemplates;
import de.ambertation.wover.complex.api.equipment.ArmorTier;
import de.ambertation.wover.complex.api.equipment.ArmorTiers;

public class BNArmorTiers {
    public static ArmorTier CINCINNASITE = ArmorTier
            .builder("cincinnasite")
            .armorMaterial(BNArmorMaterials.CINCINNASITE.value())
            .armorValuesWithOffset(ArmorTiers.IRON_ARMOR, new ArmorTier.ArmorValues(1))
            .build();

    public static ArmorTier NETHER_RUBY = ArmorTier
            .builder("nether_ruby")
            .armorMaterial(BNArmorMaterials.NETHER_RUBY.value())
            .armorValuesWithOffset(ArmorTiers.DIAMOND_ARMOR, new ArmorTier.ArmorValues(3))
            .build();

    public static ArmorTier FLAMING_RUBY = ArmorTier
            .builder("flaming_ruby")
            .armorMaterial(BNArmorMaterials.FLAMING_RUBY.value())
            .armorMaterial(BNArmorMaterials.FLAMING_RUBY.value())
            .armorValuesWithOffset(
                    ArmorTiers.NETHERITE_ARMOR,
                    new ArmorTier.ArmorValues(5, () -> NetherTemplates.FLAMING_RUBY_TEMPLATE)
            )
            .build();
}
