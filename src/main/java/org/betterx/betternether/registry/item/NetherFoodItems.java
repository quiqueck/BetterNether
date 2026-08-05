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

public class NetherFoodItems {

    public static final Item BLACK_APPLE = NetherItems.registerItem("black_apple", ItemBlackApple::new);

    public static final Item STALAGNATE_BOWL = NetherItems.registerItem(
            "stalagnate_bowl",
            props -> new ItemBowlFood(null, FoodShape.NONE, props)
    );
    public static final Item STALAGNATE_BOWL_WART = NetherItems.registerItem(
            "stalagnate_bowl_wart",
            props -> new ItemBowlFood(
                    Foods.COOKED_CHICKEN,
                    FoodShape.WART,
                    props
            )
    );
    public static final Item STALAGNATE_BOWL_MUSHROOM = NetherItems.registerItem(
            "stalagnate_bowl_mushroom",
            props -> new ItemBowlFood(
                    Foods.MUSHROOM_STEW,
                    FoodShape.MUSHROOM,
                    props
            )
    );
    public static final Item STALAGNATE_BOWL_APPLE = NetherItems.registerItem(
            "stalagnate_bowl_apple",
            props -> new ItemBowlFood(Foods.APPLE, FoodShape.APPLE, props)
    );
    public static final Item HOOK_MUSHROOM_COOKED = NetherItems.registerFood("hook_mushroom_cooked", 4, 0.4F);

    public static void ensureLoaded() {}
}
