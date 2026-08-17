package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.api.v2.levelgen.structures.StructureNBT;
import org.betterx.betternether.BetterNether;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the mob spawner data in BetterNether's city templates against the NBT renames that
 * silently emptied it.
 * <p>
 * BetterNether loads its templates through {@link StructureNBT}, which - unlike vanilla's
 * {@code TemplateSource} - does not run them through {@code DataFixTypes.STRUCTURE}. Retrofitting
 * that pass is not an option either: every city template also holds a modded block entity
 * ({@code bclib:chest}, {@code betternether:forge}), and the vanilla datafixer's block entity
 * {@code TaggedChoice} rejects ids it does not know, so the whole structure would come back
 * unfixed. The templates therefore have to be authored in the current format, and this test is what
 * keeps them there.
 * <p>
 * The failure being guarded against is entirely silent: {@code ArmorItems} / {@code HandItems}
 * (pre-1.21.5) and {@code Count} / {@code tag} (pre-1.20.5) are read by nothing today, so a stale
 * template raises nothing at all - the city guards just spawn bare-handed. Asserting on the
 * equipment of the entity the spawner would actually produce is what makes that visible.
 */
public class CitySpawnerGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The city templates that carry guard spawners.
     */
    private static final List<String> GUARDED_TEMPLATES = List.of(
            "city/city_tower_01",
            "city/city_tower_02",
            "city/city_tower_03",
            "city/city_tower_04",
            "city/city_building_05"
    );

    private static final EquipmentSlot[] GUARD_SLOTS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @GameTest
    public void cityGuardsSpawnFullyEquipped(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        int checked = 0;

        for (String name : GUARDED_TEMPLATES) {
            final StructureTemplate template = StructureNBT
                    .create(BetterNether.C.id(name))
                    .getTemplate();
            if (template == null) {
                failures.add(name + ": template failed to load");
                continue;
            }

            final List<StructureBlockInfo> spawners = template.filterBlocks(
                    BlockPos.ZERO,
                    new StructurePlaceSettings(),
                    Blocks.SPAWNER,
                    false
            );
            if (spawners.isEmpty()) {
                failures.add(name + ": no spawner in template");
                continue;
            }

            for (StructureBlockInfo info : spawners) {
                if (info.nbt() == null) {
                    failures.add(name + " @ " + info.pos() + ": spawner has no block entity data");
                    continue;
                }
                checked++;
                failures.addAll(checkSpawner(helper, name + " @ " + info.pos(), info.nbt()));
            }
        }

        if (checked == 0) {
            helper.fail("no city spawners were checked at all");
            return;
        }
        if (!failures.isEmpty()) {
            helper.fail(failures.size() + " city spawner problem(s):\n  " + String.join("\n  ", failures));
            return;
        }
        helper.succeed();
    }

    /**
     * Loads {@code nbt} into a real spawner the same way {@code StructureTemplate.placeInWorld}
     * would, then inspects the entity that spawner would produce.
     */
    private static List<String> checkSpawner(GameTestHelper helper, String where, CompoundTag nbt) {
        final List<String> failures = new ArrayList<>();
        final BlockPos pos = new BlockPos(1, 2, 1);

        helper.setBlock(pos, Blocks.SPAWNER);
        final SpawnerBlockEntity spawner = helper.getBlockEntity(pos, SpawnerBlockEntity.class);

        // A copy, because StructureNBT caches its templates and the tag would otherwise be shared
        // with the next test run.
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            spawner.loadWithComponents(TagValueInput.create(
                    reporter,
                    helper.getLevel().registryAccess(),
                    nbt.copy()
            ));
        }

        // getOrCreateDisplayEntity walks the same path a real spawn does - it builds the entity from
        // SpawnData.entity - so anything the entity format no longer reads is missing here too.
        final Entity entity = spawner.getSpawner().getOrCreateDisplayEntity(
                helper.getLevel(),
                helper.absolutePos(pos)
        );
        if (!(entity instanceof LivingEntity guard)) {
            failures.add(where + ": spawner produced " + (entity == null
                    ? "no entity at all (SpawnData failed to decode)"
                    : "a non-living " + entity.getType()));
            helper.setBlock(pos, Blocks.AIR);
            return failures;
        }

        for (EquipmentSlot slot : GUARD_SLOTS) {
            if (guard.getItemBySlot(slot).isEmpty()) {
                failures.add(where + ": " + slot.getName() + " is empty");
            }
        }

        // The armour is enchanted in the template. If the item stacks were still in the pre-1.20.5
        // shape their components would be gone even where the item itself came through.
        final ItemStack chest = guard.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty()) {
            if (enchantmentLevel(helper, Enchantments.PROTECTION, chest) <= 0) {
                failures.add(where + ": chestplate has no Protection (item components were lost)");
            }
            if (enchantmentLevel(helper, Enchantments.THORNS, chest) <= 0) {
                failures.add(where + ": chestplate has no Thorns (item components were lost)");
            }
        }

        helper.setBlock(pos, Blocks.AIR);
        return failures;
    }

    private static int enchantmentLevel(
            GameTestHelper helper,
            ResourceKey<Enchantment> enchantment,
            ItemStack stack
    ) {
        final Holder<Enchantment> holder = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantment);
        return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
    }
}
