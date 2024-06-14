package org.betterx.betternether.integrations;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherItems;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;

public class VanillaHammersIntegration {
    private static boolean hasHammers;
    private static Constructor<?> hammerConstructor;

    public static Item makeHammer(Tier material, int attackDamage, float attackSpeed) {
        if (!hasHammers) {
            //make sure we generate an Item during datagen. When doing datagen it does not matter what type the item is,
            //we just need to be able to reference it.
            if (ModCore.isDatagen()) {
                return new Item(NetherItems.defaultSettings());
            }

            return Items.AIR;
        }


        try {
            return (Item) hammerConstructor.newInstance(
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
        hasHammers = BetterNether.VANILLA_HAMMERS.isLoaded();
        try {
            if (hasHammers) {
                LogManager.getLogger().info("[BetterNether] Enabled Vanilla Hammers Integration");
                Class<?> hammerItemClass = Class.forName("draylar.magna.item.HammerItem");
                if (hammerItemClass != null)
                    for (Constructor<?> c : hammerItemClass.getConstructors())
                        if (c.getParameterCount() == 4) {
                            hammerConstructor = c;
                            break;
                        }
                hasHammers = (hammerConstructor != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasHammers() {
        return hasHammers;
    }
}
