package org.betterx.betternether;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rewrites already-generated mob spawners to the current NBT layout.
 * <p>
 * BetterNether's structure templates are loaded by {@code StructureNBT}, which - unlike vanilla's
 * {@code TemplateSource} - never ran them through {@code DataFixTypes.STRUCTURE}. Every spawner the
 * mod has ever placed therefore went into the world in the format its template was authored in, and
 * three later renames left that data silently unreadable:
 * <ul>
 *     <li><b>1.16</b> nested the payload under {@code entity} and turned {@code SpawnPotentials}
 *     entries from {@code {Entity, Weight}} into {@code {data, weight}}. A spawner still carrying the
 *     flat form fails to decode entirely and never spawns anything at all.</li>
 *     <li><b>1.20.5</b> componentized item stacks: {@code Count} + {@code tag} became {@code count}
 *     + {@code components}.</li>
 *     <li><b>1.21.5</b> replaced {@code ArmorItems} / {@code HandItems} with a slot-keyed
 *     {@code equipment} compound and the two drop-chance lists with {@code drop_chances}. This is
 *     what stripped the city guards: nothing reads the old keys, so the wither skeletons spawned
 *     bare-handed rather than armoured.</li>
 * </ul>
 * This is a pure format migration - it preserves whatever gear a spawner already holds rather than
 * re-gearing it, so it is safe to run over any spawner and is a no-op on data that is already
 * current. Re-gearing would mean resolving enchantment levels through
 * {@code WorldState.registryAccess()}, which is not available while the datafixer runs.
 * <p>
 * Kept separate from {@link Patcher} so the transformation can be driven directly by
 * {@code SpawnerPatchGameTest}: {@code Patch}'s constructor refuses a second instance once the patch
 * is registered, which makes the {@code Patch} subclass itself impossible to build in a test.
 */
final class LegacySpawnerFix {
    private static final String[] ARMOR_SLOTS = {"feet", "legs", "chest", "head"};
    private static final String[] HAND_SLOTS = {"mainhand", "offhand"};

    private LegacySpawnerFix() {
    }

    /**
     * Walks a chunk's block entities and rewrites every legacy spawner it finds.
     * <p>
     * Pre-1.18 chunks keep them under {@code Level/TileEntities}, newer ones directly under
     * {@code block_entities}; both are checked so a world that was never opened by a modern version
     * is still repaired.
     *
     * @return {@code true} if anything was changed and the chunk needs saving
     */
    static boolean patchChunk(CompoundTag root) {
        final boolean[] changed = {false};

        root.getCompound("Level")
            .flatMap(legacy -> legacy.getList("TileEntities"))
            .ifPresent(list -> patchBlockEntities(list, changed));
        root.getList("block_entities")
            .ifPresent(list -> patchBlockEntities(list, changed));

        return changed[0];
    }

    private static void patchBlockEntities(ListTag blockEntities, boolean[] changed) {
        blockEntities.forEach(tag -> {
            if (!(tag instanceof CompoundTag blockEntity)) return;
            if (!"minecraft:mob_spawner".equals(blockEntity.getStringOr("id", ""))) return;
            if (patchSpawner(blockEntity)) changed[0] = true;
        });
    }

    static boolean patchSpawner(CompoundTag spawner) {
        boolean changed = false;

        // 1.16: SpawnData was the entity tag itself, not a wrapper around it.
        final CompoundTag spawnData = spawner.getCompound("SpawnData").orElse(null);
        if (spawnData != null && spawnData.getCompound("entity").isEmpty()) {
            final CompoundTag wrapper = new CompoundTag();
            wrapper.put("entity", spawnData);
            spawner.put("SpawnData", wrapper);
            changed = true;
        }

        // 1.16: SpawnPotentials entries were {Entity, Weight}, not {data: {entity}, weight}.
        final ListTag potentials = spawner.getList("SpawnPotentials").orElse(null);
        if (potentials != null) {
            for (int i = 0; i < potentials.size(); i++) {
                if (!(potentials.get(i) instanceof CompoundTag entry)) continue;
                final CompoundTag legacyEntity = entry.getCompound("Entity").orElse(null);
                if (legacyEntity == null) continue;

                final CompoundTag data = new CompoundTag();
                data.put("entity", legacyEntity);

                final CompoundTag rebuilt = new CompoundTag();
                rebuilt.put("data", data);
                rebuilt.putInt("weight", entry.getIntOr("Weight", 1));
                potentials.set(i, rebuilt);
                changed = true;
            }
        }

        // The entity payloads, in SpawnData and in every SpawnPotentials entry.
        for (CompoundTag holder : spawnEntityHolders(spawner)) {
            final CompoundTag entity = holder.getCompound("entity").orElse(null);
            if (entity != null && patchEntity(entity)) changed = true;
        }

        return changed;
    }

    private static List<CompoundTag> spawnEntityHolders(CompoundTag spawner) {
        final List<CompoundTag> holders = new ArrayList<>(2);
        spawner.getCompound("SpawnData").ifPresent(holders::add);
        spawner.getList("SpawnPotentials").ifPresent(potentials -> potentials.forEach(tag -> {
            if (tag instanceof CompoundTag entry) entry.getCompound("data").ifPresent(holders::add);
        }));
        return holders;
    }

    private static boolean patchEntity(CompoundTag entity) {
        boolean changed = false;

        // 1.21.5: the two ordered lists became one slot-keyed compound.
        final CompoundTag equipment = entity.getCompound("equipment").orElseGet(CompoundTag::new);
        changed |= moveEquipment(entity, "ArmorItems", ARMOR_SLOTS, equipment);
        changed |= moveEquipment(entity, "HandItems", HAND_SLOTS, equipment);
        if (!equipment.isEmpty()) entity.put("equipment", equipment);

        // 1.21.5: likewise for the drop chances. Slots missing from drop_chances default to 0.085,
        // so a chance is only worth recording for a slot that actually holds something.
        final CompoundTag dropChances = entity.getCompound("drop_chances").orElseGet(CompoundTag::new);
        changed |= moveDropChances(entity, "ArmorDropChances", ARMOR_SLOTS, equipment, dropChances);
        changed |= moveDropChances(entity, "HandDropChances", HAND_SLOTS, equipment, dropChances);
        if (!dropChances.isEmpty()) entity.put("drop_chances", dropChances);

        // 1.20.2: ActiveEffects -> active_effects, with a namespaced id in place of the numeric one.
        final ListTag legacyEffects = entity.getList("ActiveEffects").orElse(null);
        if (legacyEffects != null) {
            entity.remove("ActiveEffects");
            final ListTag effects = new ListTag();
            legacyEffects.forEach(tag -> {
                if (tag instanceof CompoundTag legacy) effects.add(convertEffect(legacy));
            });
            entity.put("active_effects", effects);
            changed = true;
        }

        return changed;
    }

    private static boolean moveEquipment(
            CompoundTag entity,
            String legacyKey,
            String[] slots,
            CompoundTag equipment
    ) {
        final ListTag legacy = entity.getList(legacyKey).orElse(null);
        if (legacy == null) return false;
        entity.remove(legacyKey);

        for (int i = 0; i < Math.min(legacy.size(), slots.length); i++) {
            if (!(legacy.get(i) instanceof CompoundTag stack)) continue;
            // Unused slots are padded with an empty compound.
            if (stack.getString("id").isEmpty()) continue;
            equipment.put(slots[i], convertItem(stack));
        }
        return true;
    }

    private static boolean moveDropChances(
            CompoundTag entity,
            String legacyKey,
            String[] slots,
            CompoundTag equipment,
            CompoundTag dropChances
    ) {
        final ListTag legacy = entity.getList(legacyKey).orElse(null);
        if (legacy == null) return false;
        entity.remove(legacyKey);

        for (int i = 0; i < Math.min(legacy.size(), slots.length); i++) {
            if (!(legacy.get(i) instanceof NumericTag chance)) continue;
            if (equipment.getCompound(slots[i]).isEmpty()) continue;
            dropChances.put(slots[i], FloatTag.valueOf(chance.floatValue()));
        }
        return true;
    }

    /**
     * 1.20.5: {@code {id, Count, tag: {Enchantments: [{id, lvl}], Damage}}} became
     * {@code {id, count, components: {"minecraft:enchantments": {<id>: <level>}}}}.
     * <p>
     * Only the enchantments are carried over, because that is all BetterNether's spawners ever put
     * on a stack. Anything else in {@code tag} has no componentized equivalent that can be derived
     * here and is dropped rather than guessed at.
     */
    private static CompoundTag convertItem(CompoundTag legacy) {
        final CompoundTag stack = new CompoundTag();
        stack.putString("id", legacy.getStringOr("id", "minecraft:air"));
        stack.putInt("count", Math.max(1, legacy.getIntOr("Count", 1)));

        legacy.getCompound("tag").ifPresent(tag -> tag.getList("Enchantments").ifPresent(list -> {
            final CompoundTag enchantments = new CompoundTag();
            list.forEach(entry -> {
                if (!(entry instanceof CompoundTag enchantment)) return;
                final String id = enchantment.getStringOr("id", "");
                if (!id.isEmpty()) enchantments.putInt(id, Math.max(1, enchantment.getIntOr("lvl", 1)));
            });
            if (!enchantments.isEmpty()) {
                final CompoundTag components = new CompoundTag();
                components.put("minecraft:enchantments", enchantments);
                stack.put("components", components);
            }
        }));

        return stack;
    }

    private static CompoundTag convertEffect(CompoundTag legacy) {
        final CompoundTag effect = new CompoundTag();

        final Tag id = legacy.get("id");
        if (id instanceof StringTag) {
            effect.putString("id", legacy.getStringOr("id", "minecraft:regeneration"));
        } else if (id instanceof NumericTag numeric) {
            effect.putString("id", VANILLA_EFFECT_IDS.getOrDefault(numeric.intValue(), "minecraft:regeneration"));
        }

        final int duration = legacy.getIntOr("Duration", 0);
        if (duration != 0) effect.putInt("duration", duration);

        final int amplifier = legacy.getIntOr("Amplifier", 0);
        if (amplifier != 0) effect.putInt("amplifier", amplifier);

        return effect;
    }

    /**
     * The numeric {@code MobEffect} ids that {@code ActiveEffects} used before 1.20.2. Only the
     * effects that existed back then are listed - a newer effect can never appear in this format.
     */
    private static final Map<Integer, String> VANILLA_EFFECT_IDS = Map.ofEntries(
            Map.entry(1, "minecraft:speed"),
            Map.entry(2, "minecraft:slowness"),
            Map.entry(3, "minecraft:haste"),
            Map.entry(4, "minecraft:mining_fatigue"),
            Map.entry(5, "minecraft:strength"),
            Map.entry(6, "minecraft:instant_health"),
            Map.entry(7, "minecraft:instant_damage"),
            Map.entry(8, "minecraft:jump_boost"),
            Map.entry(9, "minecraft:nausea"),
            Map.entry(10, "minecraft:regeneration"),
            Map.entry(11, "minecraft:resistance"),
            Map.entry(12, "minecraft:fire_resistance"),
            Map.entry(13, "minecraft:water_breathing"),
            Map.entry(14, "minecraft:invisibility"),
            Map.entry(15, "minecraft:blindness"),
            Map.entry(16, "minecraft:night_vision"),
            Map.entry(17, "minecraft:hunger"),
            Map.entry(18, "minecraft:weakness"),
            Map.entry(19, "minecraft:poison"),
            Map.entry(20, "minecraft:wither"),
            Map.entry(21, "minecraft:health_boost"),
            Map.entry(22, "minecraft:absorption"),
            Map.entry(23, "minecraft:saturation"),
            Map.entry(24, "minecraft:glowing"),
            Map.entry(25, "minecraft:levitation"),
            Map.entry(26, "minecraft:luck"),
            Map.entry(27, "minecraft:unluck")
    );
}
