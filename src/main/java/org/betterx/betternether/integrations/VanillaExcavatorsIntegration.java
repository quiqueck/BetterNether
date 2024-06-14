package org.betterx.betternether.integrations;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;

public class VanillaExcavatorsIntegration {
    private static boolean hasExcavators;
    private static Constructor<?> excavatorConstructor;

    public static Item makeExcavator(Tier material, int attackDamage, float attackSpeed) {
        if (!hasExcavators) {
            //make sure we generate an Item during datagen. When doing datagen it does not matter what type the item is,
            //we just need to be able to reference it.
            if (ModCore.isDatagen()) {
                return new Item(NetherItems.defaultSettings());
            }

            return Items.AIR;
        }
        try {
            return (Item) excavatorConstructor.newInstance(
                    material,
                    attackDamage,
                    attackSpeed,
                    NetherItems.defaultSettings()
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
