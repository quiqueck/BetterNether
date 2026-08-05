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

public class NetherResourceItems {


    public static final Item CINCINNASITE = NetherItems.registerItem("cincinnasite", Item::new);
    public static final Item CINCINNASITE_INGOT = NetherItems.registerItem(
            "cincinnasite_ingot", Item::new,
            CommonItemTags.IRON_INGOTS
    );
    public static final Item NETHER_RUBY = NetherItems.registerItem("nether_ruby", Item::new);

    public static final Item GLOWSTONE_PILE = NetherItems.registerItem("glowstone_pile", Item::new);
    public static final Item LAPIS_PILE = NetherItems.registerItem("lapis_pile", Item::new);

    public static final Item AGAVE_LEAF = NetherItems.registerItem("agave_leaf", Item::new);
    public static final Item AGAVE_MEDICINE = NetherItems.registerMedicine("agave_medicine", 40, 2, true);
    public static final Item HERBAL_MEDICINE = NetherItems.registerMedicine("herbal_medicine", 10, 5, true);

    public static void ensureLoaded() {}
}
