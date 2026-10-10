package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.registry.block.NetherMushroomBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Breaks every {@code betternether:} block through the real survival mining path
 * ({@code ServerPlayerGameMode#destroyBlock}, so tool-tier gating, {@code playerDestroy}/{@code getDrops}
 * overrides and the generated loot table all take part) with a set of tools, and checks the drops.
 * <p>
 * Two sweeps over the whole registry:
 * <ul>
 *   <li>{@link #noBlockDropsNothingWithEveryTool}: a block that has an item form must drop <i>something</i>
 *       with at least one of the tools (#227 hook mushroom was this).</li>
 *   <li>{@link #silkTouchNeverDuplicatesTheBlock}: silk touch and shears must never yield more than one of
 *       the block itself (#226 giant lucis rolled its silk-touch alternative once per pool; black, blooming
 *       and gloomsculk vine rolled their whole pool 1-2 times).</li>
 * </ul>
 * Loot is random, so every block/tool pair is broken {@link #TRIALS} times and the results merged. When
 * the default state drops nothing, every other state of the block is tried before calling it a failure,
 * since multi-part plants deliberately drop from one segment only.
 * <p>
 * The full table is written to the log (search for {@code LOOT AUDIT}) so a reviewer can scan what each
 * block drops without re-running anything.
 */
public class LootAuditGameTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockPos POS = new BlockPos(3, 2, 3);
    private static final int TRIALS = 6;
    /** Breaks per block/tool in {@link #silkTouchNeverDuplicatesTheBlock}; a 50/50 extra item survives this with odds 2^-16. */
    private static final int SELF_DROP_TRIALS = 16;
    private static final int MAX_STATES = 96;
    private static final int PLACE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SKIP_ON_PLACE;

    /**
     * Blocks with an item form that are allowed to drop nothing with every audited tool. Each entry needs a
     * reason - either why it is by design, or the open issue tracking it.
     */
    private static final Map<String, String> DROPS_NOTHING_ALLOWED = Map.of();

    /** The tools the audit breaks with, in report-column order. */
    private static List<Tool> tools(GameTestHelper helper) {
        return List.of(
                new Tool("hand", () -> ItemStack.EMPTY),
                new Tool("shears", () -> new ItemStack(Items.SHEARS)),
                new Tool("silk_pickaxe", () -> silk(helper, Items.NETHERITE_PICKAXE)),
                new Tool("silk_axe", () -> silk(helper, Items.NETHERITE_AXE)),
                new Tool("silk_shovel", () -> silk(helper, Items.NETHERITE_SHOVEL)),
                new Tool("hoe", () -> new ItemStack(Items.NETHERITE_HOE))
        );
    }

    private static boolean isSilk(Tool tool) {
        return tool.name.startsWith("silk_");
    }

    /** Tools that take a block whole: silk touch, and shears. */
    private static boolean isSelfDropTool(Tool tool) {
        return isSilk(tool) || tool.name.equals("shears");
    }

    @GameTest(maxTicks = 400)
    public void noBlockDropsNothingWithEveryTool(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final List<Tool> tools = tools(helper);
        final List<String> failures = new ArrayList<>();
        final StringBuilder report = new StringBuilder("=== LOOT AUDIT (default state, max over ")
                .append(TRIALS).append(" trials) ===\n");

        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            final String id = entry.getKey().identifier().toString();
            if (!id.startsWith("betternether:")) continue;
            final Block block = entry.getValue();

            final Map<String, Map<Item, Integer>> byTool = new LinkedHashMap<>();
            for (Tool tool : tools) {
                byTool.put(tool.name, breakRepeatedly(helper, player, block.defaultBlockState(), tool));
            }
            report.append(id).append(block.asItem() == Items.AIR ? " (no item)" : "").append('\n');
            byTool.forEach((t, d) -> report.append("    ").append(t).append(": ").append(format(d)).append('\n'));

            if (block.asItem() == Items.AIR) continue;
            if (byTool.values().stream().anyMatch(d -> !d.isEmpty())) continue;
            if (DROPS_NOTHING_ALLOWED.containsKey(id)) continue;

            // Multi-part blocks may only drop from one segment - try the other states first.
            final List<String> droppingStates = new ArrayList<>();
            final List<BlockState> states = block.getStateDefinition().getPossibleStates();
            for (BlockState state : states.subList(0, Math.min(MAX_STATES, states.size()))) {
                if (state == block.defaultBlockState()) continue;
                for (Tool tool : tools) {
                    final Map<Item, Integer> drops = breakRepeatedly(helper, player, state, tool);
                    if (!drops.isEmpty()) {
                        droppingStates.add(state + " with " + tool.name + " -> " + format(drops));
                        break;
                    }
                }
                if (!droppingStates.isEmpty()) break;
            }
            if (droppingStates.isEmpty()) {
                failures.add(id + " drops nothing with " + tools.stream().map(Tool::name).toList());
            } else {
                report.append("    (default state drops nothing; ").append(droppingStates.get(0)).append(")\n");
            }
        }

        LOGGER.info(report.toString());
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "blocks that drop nothing with any tool:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 400)
    public void silkTouchNeverDuplicatesTheBlock(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final List<Tool> tools = tools(helper).stream().filter(LootAuditGameTest::isSelfDropTool).toList();
        final List<String> failures = new ArrayList<>();

        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            final String id = entry.getKey().identifier().toString();
            if (!id.startsWith("betternether:")) continue;
            final Block block = entry.getValue();
            final Item self = block.asItem();
            if (self == Items.AIR) continue;

            // Like vanilla (vines, cave vines, weeping vines, leaves...), the tools that exist to pick a
            // block up whole hand back exactly one of it. There used to be an allowance here for blocks
            // whose other tools roll 2 of themselves, but that compared two random samples, so a 1-2 roll
            // passed whenever the shears happened to roll low (gloomsculk vine flaked like that). The bound
            // is fixed now, and the max over SELF_DROP_TRIALS breaks makes a 1-2 roll slip through with
            // odds of 2^-SELF_DROP_TRIALS per tool.
            for (Tool tool : tools) {
                final Map<Item, Integer> drops = breakRepeatedly(helper, player, block.defaultBlockState(), tool, SELF_DROP_TRIALS);
                if (drops.getOrDefault(self, 0) > 1) {
                    failures.add(id + " with " + tool.name + " -> " + format(drops));
                }
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "silk touch or shears yield more than one of the block itself:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /** #228: golden vine drops itself to shears, a hoe or silk touch, and nothing by hand. */
    @GameTest
    public void goldenVineDropsWithShearsHoeOrSilkTouch(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final BlockState vine = NetherVineBlocks.GOLDEN_VINE.defaultBlockState();
        final Map<Item, Integer> one = Map.of(NetherVineBlocks.GOLDEN_VINE.asItem(), 1);

        final List<String> failures = new ArrayList<>();
        expect(failures, "shears", breakRepeatedly(helper, player, vine, new Tool("shears", () -> new ItemStack(Items.SHEARS))), one);
        expect(failures, "hoe", breakRepeatedly(helper, player, vine, new Tool("hoe", () -> new ItemStack(Items.IRON_HOE))), one);
        expect(failures, "silk_pickaxe", breakRepeatedly(helper, player, vine, new Tool("silk", () -> silk(helper, Items.IRON_PICKAXE))), one);
        expect(failures, "hand", breakRepeatedly(helper, player, vine, new Tool("hand", () -> ItemStack.EMPTY)), Map.of());
        finish(helper, "golden_vine", failures);
    }

    /** #227: hook mushroom drops itself to shears or silk touch (it dropped nothing with any tool). */
    @GameTest
    public void hookMushroomDropsWithShearsOrSilkTouch(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final BlockState mushroom = NetherMushroomBlocks.HOOK_MUSHROOM.defaultBlockState();
        final Map<Item, Integer> one = Map.of(NetherMushroomBlocks.HOOK_MUSHROOM.asItem(), 1);

        final List<String> failures = new ArrayList<>();
        expect(failures, "shears", breakRepeatedly(helper, player, mushroom, new Tool("shears", () -> new ItemStack(Items.SHEARS))), one);
        expect(failures, "silk_axe", breakRepeatedly(helper, player, mushroom, new Tool("silk", () -> silk(helper, Items.IRON_AXE))), one);
        finish(helper, "hook_mushroom", failures);
    }

    /** #226: a silk-touch axe yields exactly one giant lucis, and none of the non-silk drops. */
    @GameTest
    public void giantLucisSilkTouchDropsExactlyOne(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, POS.east(2));
        final BlockState lucis = NetherMushroomBlocks.GIANT_LUCIS.defaultBlockState();

        final List<String> failures = new ArrayList<>();
        expect(
                failures, "silk_axe",
                breakRepeatedly(helper, player, lucis, new Tool("silk", () -> silk(helper, Items.IRON_AXE))),
                Map.of(NetherMushroomBlocks.GIANT_LUCIS.asItem(), 1)
        );
        finish(helper, "giant_lucis", failures);
    }

    private static void expect(List<String> failures, String tool, Map<Item, Integer> got, Map<Item, Integer> expected) {
        if (!got.equals(expected)) {
            failures.add(tool + ": expected " + format(expected) + ", got " + format(got));
        }
    }

    private static void finish(GameTestHelper helper, String block, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    block + " drop regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /**
     * Breaks {@code state} {@link #TRIALS} times with {@code tool} and returns, per item, the largest count
     * seen in a single break.
     */
    static Map<Item, Integer> breakRepeatedly(GameTestHelper helper, ServerPlayer player, BlockState state, Tool tool) {
        return breakRepeatedly(helper, player, state, tool, TRIALS);
    }

    /** As {@link #breakRepeatedly(GameTestHelper, ServerPlayer, BlockState, Tool)}, with {@code trials} breaks. */
    static Map<Item, Integer> breakRepeatedly(GameTestHelper helper, ServerPlayer player, BlockState state, Tool tool, int trials) {
        final Map<Item, Integer> max = new TreeMap<>((a, b) -> BuiltInRegistries.ITEM.getKey(a).compareTo(BuiltInRegistries.ITEM.getKey(b)));
        for (int i = 0; i < trials; i++) {
            breakOnce(helper, player, state, tool.make.get()).forEach((item, count) -> max.merge(item, count, Math::max));
        }
        return max;
    }

    /** Places {@code state} without shape updates, mines it as a survival player and collects the drops. */
    static Map<Item, Integer> breakOnce(GameTestHelper helper, ServerPlayer player, BlockState state, ItemStack tool) {
        final ServerLevel level = helper.getLevel();
        final BlockPos abs = helper.absolutePos(POS);
        final AABB area = new AABB(abs).inflate(3.0);
        clearItems(level, area);

        level.setBlock(abs, state, PLACE_FLAGS);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.gameMode.destroyBlock(abs);

        final Map<Item, Integer> drops = new LinkedHashMap<>();
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, area)) {
            drops.merge(e.getItem().getItem(), e.getItem().getCount(), Integer::sum);
            e.discard();
        }
        level.setBlock(abs, Blocks.AIR.defaultBlockState(), PLACE_FLAGS);
        return drops;
    }

    private static void clearItems(ServerLevel level, AABB area) {
        level.getEntitiesOfClass(ItemEntity.class, area).forEach(ItemEntity::discard);
    }

    static ItemStack silk(GameTestHelper helper, Item item) {
        final ItemStack stack = new ItemStack(item);
        stack.enchant(helper.getLevel()
                            .registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.SILK_TOUCH), 1);
        return stack;
    }

    static String format(Map<Item, Integer> drops) {
        if (drops.isEmpty()) return "-";
        return drops.entrySet()
                    .stream()
                    .map(e -> e.getValue() + "x " + BuiltInRegistries.ITEM.getKey(e.getKey()))
                    .collect(Collectors.joining(", "));
    }

    record Tool(String name, Supplier<ItemStack> make) {}
}
