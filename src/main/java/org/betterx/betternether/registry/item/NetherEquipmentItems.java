package org.betterx.betternether.registry.item;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.DebugDataItem;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.blocks.BNBlockProperties.FoodShape;
import org.betterx.betternether.blocks.NetherModels;
import org.betterx.betternether.integrations.VanillaExcavatorsIntegration;
import org.betterx.betternether.integrations.VanillaHammersIntegration;
import org.betterx.betternether.items.ItemBlackApple;
import org.betterx.betternether.items.ItemBowlFood;
import org.betterx.betternether.items.complex.DiamondSet;
import org.betterx.betternether.items.complex.NetherSet;
import org.betterx.betternether.items.materials.BNArmorTiers;
import org.betterx.betternether.items.materials.BNToolMaterial;
import org.betterx.betternether.items.materials.BNToolTiers;
import org.betterx.betternether.loot.BNLoot;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.betterx.betternether.registry.NetherItems;

public class NetherEquipmentItems {


    public static final NetherSet CINCINNASITE_SET = new NetherSet(
            "cincinnasite",
            BNToolTiers.CINCINNASITE,
            BNArmorTiers.CINCINNASITE,
            true
    );


    public static final NetherSet NETHER_RUBY_SET = new NetherSet(
            "nether_ruby",
            BNToolTiers.NETHER_RUBY,
            BNArmorTiers.NETHER_RUBY,
            false
    );

    public static final DiamondSet CINCINNASITE_DIAMOND_SET = new DiamondSet(CINCINNASITE_SET);

    public static final NetherSet FLAMING_RUBY_SET = new NetherSet(
            "flaming_ruby",
            BNToolTiers.FLAMING_RUBY,
            BNArmorTiers.FLAMING_RUBY,
            false,
            NETHER_RUBY_SET
    );
    public static final Item CINCINNASITE_HAMMER = NetherItems.registerItem(
            "cincinnasite_hammer",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.CINCINNASITE,
                    4,
                    -2.0F,
                    props
            )
    );
    public static final Item CINCINNASITE_HAMMER_DIAMOND = NetherItems.registerItem(
            "cincinnasite_hammer_diamond",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.CINCINNASITE_DIAMOND,
                    5,
                    -2.0F,
                    props
            )
    );
    public static final Item NETHER_RUBY_HAMMER = NetherItems.registerItem(
            "nether_ruby_hammer",
            props -> VanillaHammersIntegration.makeHammer(
                    BNToolMaterial.NETHER_RUBY,
                    5,
                    -2.0F,
                    props
            )
    );

    public static final Item CINCINNASITE_EXCAVATOR = NetherItems.registerItem(
            "cincinnasite_excavator",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.CINCINNASITE,
                    4,
                    -1.6F,
                    props
            )
    );
    public static final Item CINCINNASITE_EXCAVATOR_DIAMOND = NetherItems.registerItem(
            "cincinnasite_excavator_diamond",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.CINCINNASITE_DIAMOND,
                    5,
                    -2.0F,
                    props
            )
    );
    public static final Item NETHER_RUBY_EXCAVATOR = NetherItems.registerItem(
            "nether_ruby_excavator",
            props -> VanillaExcavatorsIntegration.makeExcavator(
                    BNToolMaterial.NETHER_RUBY,
                    5,
                    -2.0F,
                    props
            )
    );

    public static void ensureLoaded() {}
}
