package org.betterx.betternether.integrations;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;

public class VanillaExcavatorsIntegration {
    private static boolean hasExcavators;
    private static Constructor<?> excavatorConstructor;

    public static Item makeExcavator(
            ToolMaterial material,
            int attackDamage,
            float attackSpeed,
            Item.Properties properties
    ) {
        if (!hasExcavators) {
            //make sure we generate an Item during datagen and GameTest runs. Datagen just needs something to
            //reference; GameTest force-enables this item's recipe/advancement pack regardless of whether the
            //compat mod is loaded (see ModCore#isGametest), so the id must resolve to a real, bound item there too.
            if (ModCore.isDatagen() || ModCore.isGametest()) {
                return new Item(properties);
            }

            return Items.AIR;
        }
        try {
            return (Item) excavatorConstructor.newInstance(
                    material,
                    attackDamage,
                    attackSpeed,
                    properties
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Items.AIR;
        }
    }

    static {
        hasExcavators = BetterNether.VANILLA_EXCAVATORS.isLoaded();
        try {
            if (hasExcavators) {
                LogManager.getLogger().info("[BetterNether] Enabled Vanilla Excavators Integration");
                Class<?> itemClass = Class.forName("draylar.magna.item.ExcavatorItem");
                if (itemClass != null)
                    for (Constructor<?> c : itemClass.getConstructors())
                        if (c.getParameterCount() == 4) {
                            excavatorConstructor = c;
                            break;
                        }
                hasExcavators = (excavatorConstructor != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasExcavators() {
        return hasExcavators;
    }
}
