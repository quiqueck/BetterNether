package org.betterx.datagen.betternether.worldgen;

import org.betterx.betternether.registry.NetherBiomes;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Biome tags BetterNether contributes to other mods' tags (compat data).
 * <p>
 * The entries are written as optional ({@code "required": false}). A tag in a foreign namespace is simply unused
 * when that mod is not installed, so nothing here depends on the other mod being present.
 */
public class NetherBiomeTagProvider extends WoverTagProvider.ForBiomes {
    /**
     * Waystones places its wild Nether waystones (blackstone style) only in the biomes of this tag. Waystones
     * itself lists just the five vanilla Nether biomes, which BetterNether's biomes mostly replace.
     */
    public static final TagKey<Biome> WAYSTONES_BLACKSTONE_WAYSTONE = TagKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath("waystones", "has_structure/blackstone_waystone")
    );

    public NetherBiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Biome> context) {
        // Every BetterNether biome has a solid floor to stand on - Waystones searches down from the ceiling for
        // the first free spot on solid ground in the Nether, so lava seas and hanging forests are fine too.
        context.addOptional(
                WAYSTONES_BLACKSTONE_WAYSTONE,
                NetherBiomes.BONE_REEF.key,
                NetherBiomes.CRIMSON_GLOWING_WOODS.key,
                NetherBiomes.CRIMSON_PINEWOOD.key,
                NetherBiomes.FLOODED_DELTAS.key,
                NetherBiomes.GLOOMWOOD.key,
                NetherBiomes.GRAVEL_DESERT.key,
                NetherBiomes.MAGMA_LAND.key,
                NetherBiomes.NETHER_GRASSLANDS.key,
                NetherBiomes.NETHER_JUNGLE.key,
                NetherBiomes.NETHER_MUSHROOM_FORREST.key,
                NetherBiomes.NETHER_MUSHROOM_FORREST_EDGE.key,
                NetherBiomes.NETHER_SWAMPLAND.key,
                NetherBiomes.NETHER_SWAMPLAND_TERRACES.key,
                NetherBiomes.OLD_FUNGIWOODS.key,
                NetherBiomes.OLD_SWAMPLAND.key,
                NetherBiomes.OLD_WARPED_WOODS.key,
                NetherBiomes.POOR_NETHER_GRASSLANDS.key,
                NetherBiomes.SOUL_PLAIN.key,
                NetherBiomes.SULFURIC_BONE_REEF.key,
                NetherBiomes.UPSIDE_DOWN_FOREST.key,
                NetherBiomes.UPSIDE_DOWN_FOREST_CLEARED.key,
                NetherBiomes.WART_FOREST.key,
                NetherBiomes.WART_FOREST_EDGE.key
        );
    }
}
