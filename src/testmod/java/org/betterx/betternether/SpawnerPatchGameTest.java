package org.betterx.betternether;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Exercises {@link LegacySpawnerFix}, the transformation behind the {@code Patcher_005} chunk patch
 * that repairs mob spawners in worlds generated before the templates were brought up to the current
 * NBT layout.
 * <p>
 * Fixing the templates only helps cities generated from now on; the block entities already written
 * into a save have to be rewritten in place. That patch runs destructively over every region file a
 * player owns, so its transformation is driven here against synthetic chunk tags in both legacy
 * shapes rather than only against a real world.
 * <p>
 * Lives in {@code org.betterx.betternether} because {@link LegacySpawnerFix} is package-private.
 */
public class SpawnerPatchGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest
    public void patchRestoresEquipmentOnLegacyCityGuard(GameTestHelper helper) {
        final CompoundTag chunk = chunkWith(legacyArmouredGuard());

        final boolean changed = applyPatch(chunk);
        if (!changed) {
            helper.fail("the chunk patcher reported no change for a legacy armoured spawner");
            return;
        }

        final List<String> failures = new ArrayList<>();
        final Entity entity = spawnedEntity(helper, spawnerOf(chunk));
        if (!(entity instanceof Mob guard)) {
            helper.fail("patched spawner produced " + (entity == null ? "no entity" : entity.getType().toString()));
            return;
        }

        if (guard.getType() != EntityTypes.WITHER_SKELETON) {
            failures.add("expected a wither skeleton, got " + guard.getType());
        }
        if (guard.getItemBySlot(EquipmentSlot.MAINHAND).getItem() != Items.NETHERITE_SWORD) {
            failures.add("mainhand is " + guard.getItemBySlot(EquipmentSlot.MAINHAND));
        }
        if (guard.getItemBySlot(EquipmentSlot.OFFHAND).getItem() != Items.SHIELD) {
            failures.add("offhand is " + guard.getItemBySlot(EquipmentSlot.OFFHAND));
        }
        if (guard.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.NETHERITE_CHESTPLATE) {
            failures.add("chest is " + guard.getItemBySlot(EquipmentSlot.CHEST));
        }
        if (guard.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.NETHERITE_HELMET) {
            failures.add("head is " + guard.getItemBySlot(EquipmentSlot.HEAD));
        }
        // The gear is part of the building and must keep never dropping.
        if (guard.getDropChances().byEquipment(EquipmentSlot.CHEST) != 0.0F) {
            failures.add("chest drop chance is " + guard.getDropChances().byEquipment(EquipmentSlot.CHEST));
        }

        if (!failures.isEmpty()) {
            helper.fail("patched guard is wrong:\n  " + String.join("\n  ", failures));
            return;
        }
        helper.succeed();
    }

    /**
     * The pre-1.16 layout, which does not decode at all today - the spawner is inert rather than
     * merely stripped, so this is the case where the patch is the difference between a dead spawner
     * and a working one.
     */
    @GameTest
    public void patchRevivesPre116FlatSpawnData(GameTestHelper helper) {
        final CompoundTag spawner = baseSpawner();

        final CompoundTag flat = new CompoundTag();
        flat.putString("id", "minecraft:blaze");
        spawner.put("SpawnData", flat);

        final CompoundTag potential = new CompoundTag();
        final CompoundTag potentialEntity = new CompoundTag();
        potentialEntity.putString("id", "minecraft:blaze");
        potential.put("Entity", potentialEntity);
        potential.putInt("Weight", 1);
        final ListTag potentials = new ListTag();
        potentials.add(potential);
        spawner.put("SpawnPotentials", potentials);

        final CompoundTag chunk = chunkWith(spawner);

        // Before the patch this data cannot produce an entity at all.
        if (spawnedEntity(helper, spawnerOf(chunk).copy()) != null) {
            helper.fail("pre-1.16 SpawnData unexpectedly decoded without the patch - "
                    + "this test no longer proves anything");
            return;
        }

        if (!applyPatch(chunk)) {
            helper.fail("the chunk patcher reported no change for a pre-1.16 spawner");
            return;
        }

        final Entity entity = spawnedEntity(helper, spawnerOf(chunk));
        if (entity == null) {
            helper.fail("patched pre-1.16 spawner still produces no entity");
            return;
        }
        if (entity.getType() != EntityTypes.BLAZE) {
            helper.fail("expected a blaze, got " + entity.getType());
            return;
        }
        helper.succeed();
    }

    /**
     * Pre-1.18 chunks keep their block entities under {@code Level/TileEntities} with capitalised
     * keys; a world that was never opened by a modern version has to be repaired too.
     */
    @GameTest
    public void patchReachesPre118ChunkLayout(GameTestHelper helper) {
        final ListTag tileEntities = new ListTag();
        tileEntities.add(legacyArmouredGuard());

        final CompoundTag level = new CompoundTag();
        level.put("TileEntities", tileEntities);

        final CompoundTag chunk = new CompoundTag();
        chunk.put("Level", level);

        if (!applyPatch(chunk)) {
            helper.fail("the chunk patcher did not reach Level/TileEntities");
            return;
        }

        final CompoundTag patched = (CompoundTag) chunk
                .getCompound("Level").orElseThrow()
                .getList("TileEntities").orElseThrow()
                .get(0);
        final CompoundTag entity = patched
                .getCompound("SpawnData").orElseThrow()
                .getCompound("entity").orElseThrow();

        if (entity.getCompound("equipment").isEmpty()) {
            helper.fail("pre-1.18 chunk was visited but its spawner kept the legacy equipment keys");
            return;
        }
        helper.succeed();
    }

    /**
     * The patch has to be a no-op on data that is already current, because it runs over every chunk
     * of every world - a spurious "changed" would rewrite the whole save.
     */
    @GameTest
    public void patchLeavesModernSpawnersAlone(GameTestHelper helper) {
        final CompoundTag spawner = baseSpawner();

        final CompoundTag equipment = new CompoundTag();
        final CompoundTag sword = new CompoundTag();
        sword.putString("id", "minecraft:netherite_sword");
        sword.putInt("count", 1);
        equipment.put("mainhand", sword);

        final CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:wither_skeleton");
        entity.put("equipment", equipment);

        final CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        spawner.put("SpawnData", spawnData);

        final CompoundTag chunk = chunkWith(spawner);
        final String before = chunk.toString();

        if (applyPatch(chunk)) {
            helper.fail("the chunk patcher reported a change for already-modern data");
            return;
        }
        if (!before.equals(chunk.toString())) {
            helper.fail("the chunk patcher mutated already-modern data:\n" + chunk);
            return;
        }
        helper.succeed();
    }

    @GameTest
    public void patchConvertsNumericEffectIds(GameTestHelper helper) {
        final CompoundTag spawner = baseSpawner();

        final CompoundTag legacyEffect = new CompoundTag();
        legacyEffect.putByte("id", (byte) 10); // regeneration, before ids were namespaced
        legacyEffect.putInt("Duration", 200);
        legacyEffect.putByte("Amplifier", (byte) 2);
        final ListTag effects = new ListTag();
        effects.add(legacyEffect);

        final CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:wither_skeleton");
        entity.put("ActiveEffects", effects);

        final CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        spawner.put("SpawnData", spawnData);

        final CompoundTag chunk = chunkWith(spawner);
        if (!applyPatch(chunk)) {
            helper.fail("the chunk patcher reported no change for a legacy ActiveEffects list");
            return;
        }

        final Entity spawned = spawnedEntity(helper, spawnerOf(chunk));
        if (!(spawned instanceof LivingEntity mob)) {
            helper.fail("patched spawner produced " + (spawned == null ? "no entity" : spawned.getType().toString()));
            return;
        }
        if (!mob.hasEffect(MobEffects.REGENERATION)) {
            helper.fail("numeric effect id 10 did not survive as minecraft:regeneration");
            return;
        }
        helper.succeed();
    }

    // ---------------------------------------------------------------- helpers

    private static boolean applyPatch(CompoundTag chunk) {
        // Driven through LegacySpawnerFix rather than Patcher_005: Patch's constructor refuses a
        // second instance once the patch is registered, so the Patch subclass cannot be built here.
        return LegacySpawnerFix.patchChunk(chunk);
    }

    private static CompoundTag chunkWith(CompoundTag blockEntity) {
        final ListTag blockEntities = new ListTag();
        blockEntities.add(blockEntity);
        final CompoundTag chunk = new CompoundTag();
        chunk.put("block_entities", blockEntities);
        return chunk;
    }

    private static CompoundTag spawnerOf(CompoundTag chunk) {
        return (CompoundTag) chunk.getList("block_entities").orElseThrow().get(0);
    }

    private static CompoundTag baseSpawner() {
        final CompoundTag spawner = new CompoundTag();
        spawner.putString("id", "minecraft:mob_spawner");
        spawner.putShort("SpawnRange", (short) 4);
        spawner.putShort("SpawnCount", (short) 4);
        return spawner;
    }

    /**
     * A city guard exactly as {@code city_tower_01.nbt} used to hold it: 1.21.4-era equipment lists
     * of 1.20.4-era item stacks.
     */
    private static CompoundTag legacyArmouredGuard() {
        final CompoundTag spawner = baseSpawner();

        final ListTag handItems = new ListTag();
        handItems.add(legacyItem("minecraft:netherite_sword", null, 0));
        handItems.add(legacyItem("minecraft:shield", null, 0));

        final ListTag armorItems = new ListTag();
        armorItems.add(legacyItem("minecraft:netherite_boots", "minecraft:protection", 1));
        armorItems.add(legacyItem("minecraft:netherite_leggings", "minecraft:protection", 1));
        armorItems.add(legacyItem("minecraft:netherite_chestplate", "minecraft:protection", 1));
        armorItems.add(legacyItem("minecraft:netherite_helmet", "minecraft:protection", 1));

        final ListTag handDropChances = new ListTag();
        final ListTag armorDropChances = new ListTag();
        for (int i = 0; i < 2; i++) handDropChances.add(FloatTag.valueOf(0));
        for (int i = 0; i < 4; i++) armorDropChances.add(FloatTag.valueOf(0));

        final CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:wither_skeleton");
        entity.putInt("PersistenceRequired", 1); // the old editor wrote this as an int
        entity.put("HandItems", handItems);
        entity.put("ArmorItems", armorItems);
        entity.put("HandDropChances", handDropChances);
        entity.put("ArmorDropChances", armorDropChances);

        final CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        spawnData.put("custom_spawn_rules", new CompoundTag());
        spawner.put("SpawnData", spawnData);

        return spawner;
    }

    private static CompoundTag legacyItem(String id, String enchantment, int level) {
        final CompoundTag stack = new CompoundTag();
        stack.putString("id", id);
        stack.putByte("Count", (byte) 1);
        if (enchantment != null) {
            final CompoundTag entry = new CompoundTag();
            entry.putString("id", enchantment);
            entry.putInt("lvl", level);
            final ListTag enchantments = new ListTag();
            enchantments.add(entry);
            final CompoundTag tag = new CompoundTag();
            tag.put("Enchantments", enchantments);
            tag.putInt("Damage", 0);
            stack.put("tag", tag);
        }
        return stack;
    }

    /**
     * Loads a spawner block entity tag into a real spawner and returns the entity it would spawn,
     * or {@code null} if the data does not decode.
     */
    private static Entity spawnedEntity(GameTestHelper helper, CompoundTag spawnerTag) {
        final BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, Blocks.SPAWNER);
        final SpawnerBlockEntity spawner = helper.getBlockEntity(pos, SpawnerBlockEntity.class);

        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(spawner.problemPath(), LOGGER)) {
            spawner.loadWithComponents(TagValueInput.create(
                    reporter,
                    helper.getLevel().registryAccess(),
                    spawnerTag.copy()
            ));
        }

        final Entity entity = spawner.getSpawner().getOrCreateDisplayEntity(
                helper.getLevel(),
                helper.absolutePos(pos)
        );
        helper.setBlock(pos, Blocks.AIR);
        return entity;
    }
}
