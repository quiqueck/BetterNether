package org.betterx.betternether.testmod.gametest;

import org.betterx.bclib.trait.block.DescriptionBlockTrait;
import org.betterx.betternether.registry.block.NetherPlantBlocks;
import org.betterx.betternether.registry.block.NetherVineBlocks;
import org.betterx.betternether.registry.block.NetherWoodBlocks;
import org.betterx.betternether.world.features.GloomwoodTreeFeature;
import de.ambertation.wover.block.api.trait.BlockTrait;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The saplings and seeds whose growing rules cannot be seen on the block carry a tooltip that says them, in
 * English and German, and the one tooltip that quotes a number from the code still quotes the right one.
 * <p>
 * The soil each one survives on is not covered here: bclib generates that line from the block's own
 * {@code SurvivesOnBlockTrait}, so it cannot drift. The lines here are hand-written, and this test is what
 * keeps them attached and translated.
 */
public class SaplingTooltipGameTest {
    private static final String[] LANGUAGES = {"en_us", "de_de"};

    @GameTest
    public void unusualSaplingsExplainThemselves(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final Map<Block, String> expected = new LinkedHashMap<>();
        expected.put(NetherWoodBlocks.MAT_NETHER_SAKURA.getSapling(), "tooltip.betternether.nether_sakura_sapling.space");
        expected.put(NetherWoodBlocks.MAT_ANCHOR_TREE.getSapling(), "tooltip.betternether.anchor_tree_sapling.hanging");
        expected.put(NetherWoodBlocks.MAT_GLOOMWOOD.getSapling(), "tooltip.betternether.gloomwood_sapling.space");
        expected.put(NetherWoodBlocks.MAT_WILLOW.getSapling(), "tooltip.betternether.willow_sapling.large");
        expected.put(NetherWoodBlocks.MAT_STALAGNATE.getSeed(), "tooltip.betternether.stalagnate_seed.reach");
        expected.put(NetherWoodBlocks.MAT_WART.getSeed(), "tooltip.betternether.wart_seed.tree");
        expected.put(NetherPlantBlocks.EYE_SEED, "tooltip.betternether.sapling.hanging");
        expected.put(NetherVineBlocks.LUMABUS_SEED, "tooltip.betternether.sapling.hanging");
        expected.put(NetherVineBlocks.GOLDEN_LUMABUS_SEED, "tooltip.betternether.sapling.hanging");
        expected.forEach((block, key) -> {
            if (!descriptionKeys(block).contains(key)) {
                failures.add(name(block) + " does not carry the tooltip line " + key);
            }
        });

        // Every description line any BetterNether block carries must be translated.
        final Map<String, JsonObject> languages = new LinkedHashMap<>();
        for (String language : LANGUAGES) {
            final JsonObject json = readLanguage(language, failures);
            if (json != null) languages.put(language, json);
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            if (!BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("betternether")) continue;
            for (String key : descriptionKeys(block)) {
                languages.forEach((language, json) -> {
                    if (!json.has(key)) failures.add(name(block) + ": " + key + " is missing from " + language);
                });
            }
        }

        // The gloomwood line quotes the headroom below which its sapling refuses bone meal.
        final String number = Integer.toString(GloomwoodTreeFeature.MIN_CLEARANCE);
        languages.forEach((language, json) -> {
            final String key = "tooltip.betternether.gloomwood_sapling.space";
            if (json.has(key) && !json.get(key).getAsString().contains(number)) {
                failures.add(language + " " + key + " does not mention GloomwoodTreeFeature.MIN_CLEARANCE ("
                        + number + "): '" + json.get(key).getAsString() + "'");
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Sapling tooltip regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static List<String> descriptionKeys(Block block) {
        return BlockTrait.runtimeTraits(block)
                         .filter(t -> t instanceof DescriptionBlockTrait)
                         .flatMap(t -> ((DescriptionBlockTrait) t).translationKeys.stream())
                         .toList();
    }

    private static JsonObject readLanguage(String language, List<String> failures) {
        final String file = "assets/betternether/lang/" + language + ".json";
        final Path path = FabricLoader.getInstance()
                                      .getModContainer("betternether")
                                      .flatMap(mod -> mod.findPath(file))
                                      .orElse(null);
        if (path == null) {
            failures.add(file + " not found in the betternether mod");
            return null;
        }
        try {
            return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
        } catch (IOException | RuntimeException e) {
            failures.add(file + " can not be read: " + e);
            return null;
        }
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }
}
