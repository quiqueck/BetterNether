# Registry split map (WP7.1)

**Authoritative execution spec** for the Phase 7 registry split (req 10) of `NetherBlocks / NetherItems`.
Generated and hand-verified under WP7.1; the split WPs (WP7.6-7.8 blocks, WP7.9/7.10 items, WP7.11 hook removal) must reproduce
this field->file assignment, this boot order, and this within-file order exactly.

This document is machine-checked: every `public static final` field of the source registry
class is assigned to exactly one target file, and every cross-field reference in an initializer
was extracted and proven to point *backwards* in the combined (boot-order, in-file-source-order)
sequence. **Zero unresolved ordering violations remain.**

## 0. Mechanics recap (why order matters)

Static fields initialise top-to-bottom at class-load. Today the whole registry is one class, so
registration order == declaration order. After the split, registration order becomes:

> for each category class in the `register()` / `ensureStaticallyLoaded()` **boot order**,
> that class's fields in **source order**.

Registration order is observable in exactly two places, both accepted as a one-time `REVIEWED`
change per user-decision 4 and R2:

- **creative-tab order** (eyeballed in `runClient`), and
- **tag JSON array order** (`src/main/generated/**/tags/**.json` — arrays reorder).

Everything else is order-immune: per-block loot/recipe/model/blockstate JSON, and the three sorted
goldens (`block_properties.txt`, `block_registrations.txt`, `block_shapes.txt`) are all sorted by
registry key, so they do **not** move. **Expected diff surfaces for the whole split: tag JSON arrays
+ the two `docs/registration-order-blocks.txt` / `docs/registration-order-items.txt` dumps. Nothing else.**

A field's initializer may reference another field only if that field is initialised earlier. Within a
file this is guaranteed by preserving source order (all intra-class references are already backward, or
the current code would not compile). Across files it is guaranteed by the boot order below. A reference
from class A to a not-yet-loaded field of class B forces B's full `<clinit>` first, so **a reference into
a later-booted category would silently reorder that category** (and, in a cycle, read `null`) — hence the
topological check in section 3.

## 1. Field -> category map

### Blocks (`NetherBlocks` -> `org.betterx.betternether.registry.block.*`)

#### `block/NetherTerrainBlocks` — Terrain (9 fields)

- `NETHERRACK_MOSS` `[netherrack_moss]` _BlockTerrain_
- `NETHER_MYCELIUM` `[nether_mycelium]` _BlockNetherMycelium_
- `JUNGLE_GRASS` `[jungle_grass]` _BlockTerrain_
- `MUSHROOM_GRASS` `[mushroom_grass]` _BlockTerrain_
- `SEPIA_MUSHROOM_GRASS` `[sepia_mushroom_grass]` _BlockTerrain_
- `SWAMPLAND_GRASS` `[swampland_grass]` _BlockTerrain_
- `FARMLAND` `[farmland]`
- `CEILING_MUSHROOMS` `[ceiling_mushrooms]` _BlockTerrain_
- `VEINED_SAND` `[veined_sand]`

#### `block/NetherStoneBlocks` — Stone (38 fields)

- `NETHER_BRICK_TILE_LARGE` `[nether_brick_tile_large]`
- `NETHER_BRICK_TILE_SMALL` `[nether_brick_tile_small]`
- `NETHER_BRICK_WALL` `[nether_brick_wall]`  — refs `NETHER_BRICK_TILE_LARGE`→Stone
- `NETHER_BRICK_TILE_SLAB` `[nether_brick_tile_slab]`  — refs `NETHER_BRICK_TILE_SMALL`→Stone
- `NETHER_BRICK_TILE_STAIRS` `[nether_brick_tile_stairs]`  — refs `NETHER_BRICK_TILE_SMALL`→Stone
- `BONE_BLOCK` `[bone_block]`
- `BONE_STAIRS` `[bone_stairs]`  — refs `BONE_BLOCK`→Stone
- `BONE_SLAB` `[bone_slab]`  — refs `BONE_BLOCK`→Stone
- `BONE_BUTTON` `[bone_button]`  — refs `BONE_BLOCK`→Stone
- `BONE_PLATE` `[bone_plate]`  — refs `BONE_BLOCK`→Stone
- `BONE_WALL` `[bone_wall]`  — refs `BONE_BLOCK`→Stone
- `BONE_TILE` `[bone_tile]`
- `BONE_REED_DOOR` `[bone_reed_door]`  — refs `BONE_BLOCK`→Stone
- `BONE_CINCINNASITE_DOOR` `[bone_cincinnasite_door]`  — refs `BONE_BLOCK`→Stone
- `SOUL_SANDSTONE` `[soul_sandstone]`
- `SOUL_SANDSTONE_CUT` `[soul_sandstone_cut]`  — refs `SOUL_SANDSTONE`→Stone
- `SOUL_SANDSTONE_CUT_STAIRS` `[soul_sandstone_cut_stairs]`  — refs `SOUL_SANDSTONE_CUT`→Stone
- `SOUL_SANDSTONE_CUT_SLAB` `[soul_sandstone_cut_slab]`  — refs `SOUL_SANDSTONE_CUT`→Stone
- `SOUL_SANDSTONE_WALL` `[soul_sandstone_wall]`  — refs `SOUL_SANDSTONE_CUT`→Stone
- `SOUL_SANDSTONE_SMOOTH` `[soul_sandstone_smooth]`
- `SOUL_SANDSTONE_CHISELED` `[soul_sandstone_chiseled]`  — refs `SOUL_SANDSTONE_SMOOTH`→Stone
- `SOUL_SANDSTONE_STAIRS` `[soul_sandstone_stairs]`  — refs `SOUL_SANDSTONE`→Stone
- `SOUL_SANDSTONE_SMOOTH_STAIRS` `[soul_sandstone_smooth_stairs]`  — refs `SOUL_SANDSTONE_SMOOTH`→Stone
- `SOUL_SANDSTONE_SLAB` `[soul_sandstone_slab]`  — refs `SOUL_SANDSTONE`→Stone
- `SOUL_SANDSTONE_SMOOTH_SLAB` `[soul_sandstone_smooth_slab]`  — refs `SOUL_SANDSTONE_SMOOTH`→Stone
- `BASALT_BRICKS` `[basalt_bricks]`
- `BASALT_BRICKS_STAIRS` `[basalt_bricks_stairs]`  — refs `BASALT_BRICKS`→Stone
- `BASALT_BRICKS_SLAB` `[basalt_bricks_slab]`  — refs `BASALT_BRICKS`→Stone
- `BASALT_BRICKS_WALL` `[basalt_bricks_wall]`  — refs `BASALT_BRICKS`→Stone
- `BASALT_SLAB` `[basalt_slab]`
- `NETHERRACK_STALACTITE` `[netherrack_stalactite]`
- `GLOWSTONE_STALACTITE` `[glowstone_stalactite]`
- `BLACKSTONE_STALACTITE` `[blackstone_stalactite]`
- `BASALT_STALACTITE` `[basalt_stalactite]`
- `BONE_STALACTITE` `[bone_stalactite]`  — refs `BONE_BLOCK`→Stone
- `NETHERRACK_SLAB` `[netherrack_slab]`
- `NETHERRACK_STAIR` `[netherrack_stairs]`
- `NETHERRACK_WALLS` `[netherrack_wall]`

#### `block/NetherMetalBlocks` — Metal (19 fields)

- `CINCINNASITE_BLOCK` `[cincinnasite_block]`
- `CINCINNASITE_FORGED` `[cincinnasite_forged]`
- `CINCINNASITE_PILLAR` `[cincinnasite_pillar]`  — refs `CINCINNASITE_BLOCK`→Metal
- `CINCINNASITE_BRICKS` `[cincinnasite_bricks]`
- `CINCINNASITE_BRICK_PLATE` `[cincinnasite_brick_plate]`
- `CINCINNASITE_STAIRS` `[cincinnasite_stairs]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_SLAB` `[cincinnasite_slab]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_BUTTON` `[cincinnasite_button]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_PLATE` `[cincinnasite_plate]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_TILE_LARGE` `[cincinnasite_tile_large]`
- `CINCINNASITE_TILE_SMALL` `[cincinnasite_tile_small]`
- `CINCINNASITE_CARVED` `[cincinnasite_carved]`
- `CINCINNASITE_WALL` `[cincinnasite_wall]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_BRICKS_PILLAR` `[cincinnasite_bricks_pillar]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_BARS` `[cincinnasite_bars]`  — refs `CINCINNASITE_FORGED`→Metal
- `CINCINNASITE_CHAIN` `[cincinnasite_chain]`
- `NETHER_RUBY_BLOCK` `[nether_ruby_block]`
- `NETHER_RUBY_STAIRS` `[nether_ruby_stairs]`  — refs `NETHER_RUBY_BLOCK`→Metal
- `NETHER_RUBY_SLAB` `[nether_ruby_slab]`  — refs `NETHER_RUBY_BLOCK`→Metal

#### `block/NetherOreBlocks` — Ore (4 fields)

- `CINCINNASITE_ORE` `[cincinnasite_ore]`
- `NETHER_RUBY_ORE` `[nether_ruby_ore]`
- `NETHER_LAPIS_ORE` `[nether_lapis_ore]`
- `NETHER_REDSTONE_ORE` `[nether_redstone_ore]`

#### `block/NetherObsidianBlocks` — Obsidian (24 fields)

- `BLUE_WEEPING_OBSIDIAN` `[blue_weeping_obsidian]`
- `WEEPING_OBSIDIAN` `[weeping_obsidian]`
- `BLUE_CRYING_OBSIDIAN` `[blue_crying_obsidian]`
- `OBSIDIAN_BRICKS` `[obsidian_bricks]`
- `OBSIDIAN_BRICKS_STAIRS` `[obsidian_bricks_stairs]`  — refs `OBSIDIAN_BRICKS`→Obsidian
- `OBSIDIAN_BRICKS_SLAB` `[obsidian_bricks_slab]`  — refs `OBSIDIAN_BRICKS`→Obsidian
- `OBSIDIAN_TILE` `[obsidian_tile]`
- `OBSIDIAN_TILE_SMALL` `[obsidian_tile_small]`
- `OBSIDIAN_TILE_STAIRS` `[obsidian_tile_stairs]`  — refs `OBSIDIAN_TILE_SMALL`→Obsidian
- `OBSIDIAN_TILE_SLAB` `[obsidian_tile_slab]`  — refs `OBSIDIAN_TILE_SMALL`→Obsidian
- `OBSIDIAN_ROD_TILES` `[obsidian_rod_tiles]`
- `OBSIDIAN_GLASS` `[obsidian_glass]`
- `OBSIDIAN_GLASS_PANE` `[obsidian_glass_pane]`  — refs `OBSIDIAN_GLASS`→Obsidian
- `BLUE_OBSIDIAN` `[blue_obsidian]`  — refs `BLUE_CRYING_OBSIDIAN`→Obsidian
- `BLUE_OBSIDIAN_BRICKS` `[blue_obsidian_bricks]`
- `BLUE_OBSIDIAN_BRICKS_STAIRS` `[blue_obsidian_bricks_stairs]`  — refs `BLUE_OBSIDIAN_BRICKS`→Obsidian
- `BLUE_OBSIDIAN_BRICKS_SLAB` `[blue_obsidian_bricks_slab]`  — refs `BLUE_OBSIDIAN_BRICKS`→Obsidian
- `BLUE_OBSIDIAN_TILE` `[blue_obsidian_tile]`
- `BLUE_OBSIDIAN_TILE_SMALL` `[blue_obsidian_tile_small]`
- `BLUE_OBSIDIAN_TILE_STAIRS` `[blue_obsidian_tile_stairs]`  — refs `BLUE_OBSIDIAN_TILE_SMALL`→Obsidian
- `BLUE_OBSIDIAN_TILE_SLAB` `[blue_obsidian_tile_slab]`  — refs `BLUE_OBSIDIAN_TILE_SMALL`→Obsidian
- `BLUE_OBSIDIAN_ROD_TILES` `[blue_obsidian_rod_tiles]`
- `BLUE_OBSIDIAN_GLASS` `[blue_obsidian_glass]`
- `BLUE_OBSIDIAN_GLASS_PANE` `[blue_obsidian_glass_pane]`  — refs `BLUE_OBSIDIAN_GLASS`→Obsidian

#### `block/NetherGlassBlocks` — Glass (8 fields)

- `QUARTZ_GLASS` `[quartz_glass]`
- `QUARTZ_GLASS_FRAMED` `[quartz_glass_framed]`  — refs `CINCINNASITE_BLOCK`→Metal
- `QUARTZ_GLASS_FRAMED_COLORED` _ColoredGlassMaterial_  — refs `QUARTZ_GLASS_FRAMED`→Glass
- `QUARTZ_GLASS_PANE` `[quartz_glass_pane]`  — refs `QUARTZ_GLASS`→Glass
- `QUARTZ_GLASS_PANE_COLORED` _ColoredGlassMaterial_  — refs `QUARTZ_GLASS_PANE`→Glass
- `QUARTZ_GLASS_FRAMED_PANE` `[quartz_glass_framed_pane]`  — refs `CINCINNASITE_BLOCK`→Metal
- `QUARTZ_GLASS_FRAMED_PANE_COLORED` _ColoredGlassMaterial_  — refs `QUARTZ_GLASS_FRAMED_PANE`→Glass
- `QUARTZ_GLASS_COLORED` _ColoredGlassMaterial_  — refs `QUARTZ_GLASS`→Glass

#### `block/NetherWoodBlocks` — Wood (21 fields)

- `NETHER_REED_STEM` `[nether_reed_stem]`
- `MAT_REED` _NetherReedMaterial_
- `MAT_STALAGNATE` _StalagnateMaterial_
- `MAT_WILLOW` _WillowMaterial_
- `MAT_WART` _WartMaterial_
- `WARPED_WOOD` _VanillaNetherWood_
- `CRIMSON_WOOD` _VanillaNetherWood_
- `OAK_WOOD` _VanillaWood_
- `SPRUCE_WOOD` _VanillaWood_
- `BIRCH_WOOD` _VanillaWood_
- `JUNGLE_WOOD` _VanillaWood_
- `ACACIA_WOOD` _VanillaWood_
- `DARK_OAK_WOOD` _VanillaWood_
- `CHERRY_WOOD` _VanillaWood_
- `BAMBOO_WOOD` _VanillaWood_
- `MANGROVE_WOOD` _VanillaWood_
- `MAT_RUBEUS` _RubeusMaterial_
- `MAT_MUSHROOM_FIR` _MushroomFirMaterial_
- `MAT_NETHER_MUSHROOM` _NetherMushroomMaterial_
- `MAT_ANCHOR_TREE` _AnchorTreeMaterial_
- `MAT_NETHER_SAKURA` _NetherSakuraMaterial_

#### `block/NetherLeavesBlocks` — Leaves (4 fields)

- `WILLOW_LEAVES` `[willow_leaves]`  — refs `MAT_WILLOW`→Wood
- `RUBEUS_LEAVES` `[rubeus_leaves]`  — refs `MAT_RUBEUS`→Wood
- `ANCHOR_TREE_LEAVES` `[anchor_tree_leaves]`  — refs `MAT_ANCHOR_TREE`→Wood
- `NETHER_SAKURA_LEAVES` `[nether_sakura_leaves]`  — refs `MAT_NETHER_SAKURA`→Wood

#### `block/NetherPlantBlocks` — Plant (23 fields)

- `EYE_SEED` `[eye_seed]`
- `NETHER_GRASS` `[nether_grass]`
- `SWAMP_GRASS` `[swamp_grass]`
- `SOUL_GRASS` `[soul_grass]`
- `JUNGLE_PLANT` `[jungle_plant]`
- `BONE_GRASS` `[bone_grass]`
- `SEPIA_BONE_GRASS` `[sepia_bone_grass]`
- `BLACK_BUSH` `[black_bush]`
- `INK_BUSH` `[ink_bush]`
- `INK_BUSH_SEED` `[ink_bush_seed]`
- `EGG_PLANT` `[egg_plant]`
- `MAGMA_FLOWER` `[magma_flower]`
- `FEATHER_FERN` `[feather_fern]`
- `MOSS_COVER` `[moss_cover]`
- `NEON_EQUISETUM` `[neon_equisetum]`
- `AGAVE` `[agave]`
- `BARREL_CACTUS` `[barrel_cactus]`
- `NETHER_CACTUS` `[nether_cactus]`
- `JUNGLE_MOSS` `[jungle_moss]`
- `SOUL_LILY` `[soul_lily]`
- `EYEBALL` `[eyeball]`
- `EYEBALL_SMALL` `[eyeball_small]`
- `POTTED_PLANT` `[potted_plant]`

#### `block/NetherWallPlantBlocks` — WallPlant (3 fields)

- `WALL_MOSS` `[wall_moss]`
- `WALL_MUSHROOM_BROWN` `[wall_mushroom_brown]`
- `WALL_MUSHROOM_RED` `[wall_mushroom_red]`

#### `block/NetherCropBlocks` — Crop (3 fields)

- `BLACK_APPLE` `[black_apple]`
- `BLACK_APPLE_SEED` `[black_apple_seed]`
- `WHISPERING_GOURD` `[whispering_gourd]`

#### `block/NetherSaplingBlocks` — Sapling (3 fields)

- `GIANT_MOLD_SAPLING` `[giant_mold_sapling]`
- `JELLYFISH_MUSHROOM_SAPLING` `[jellyfish_mushroom_sapling]`
- `SOUL_LILY_SAPLING` `[soul_lily_sapling]`

#### `block/NetherVineBlocks` — Vine (11 fields)

- `BLACK_VINE` `[black_vine]`
- `BLOOMING_VINE` `[blooming_vine]`
- `GOLDEN_VINE` `[golden_vine]`
- `LUMABUS_VINE` `[lumabus_vine]` _BlockLumabusVine_
- `GOLDEN_LUMABUS_VINE` `[golden_lumabus_vine]` _BlockLumabusVine_
- `SOUL_VEIN` `[soul_vein]`
- `WHISPERING_GOURD_VINE` `[whispering_gourd_vine]`
- `ANCHOR_TREE_VINE` `[anchor_tree_vine]`
- `EYE_VINE` `[eye_vine]`
- `LUMABUS_SEED` `[lumabus_seed]`  — refs `LUMABUS_VINE`→Vine
- `GOLDEN_LUMABUS_SEED` `[golden_lumabus_seed]`  — refs `GOLDEN_LUMABUS_VINE`→Vine

#### `block/NetherMushroomBlocks` — Mushroom (13 fields)

- `ORANGE_MUSHROOM` `[orange_mushroom]`
- `RED_MOLD` `[red_mold]`
- `GRAY_MOLD` `[gray_mold]`
- `LUCIS_SPORE` `[lucis_spore]`
- `GIANT_LUCIS` `[giant_lucis]`
- `BONE_MUSHROOM` `[bone_mushroom]`
- `SMOKER` `[smoker]`
- `HOOK_MUSHROOM` `[hook_mushroom]`
- `RED_LARGE_MUSHROOM` `[red_large_mushroom]`
- `BROWN_LARGE_MUSHROOM` `[brown_large_mushroom]`
- `LUCIS_MUSHROOM` `[lucis_mushroom]`
- `GIANT_MOLD` `[giant_mold]`
- `JELLYFISH_MUSHROOM` `[jellyfish_mushroom]`

#### `block/NetherLightBlocks` — Light (9 fields)

- `CINCINNASITE_LANTERN` `[cincinnasite_lantern]`  — refs `CINCINNASITE_BLOCK`→Metal
- `CINCINNASITE_LANTERN_SMALL` `[cincinnasite_lantern_small]`  — refs `CINCINNASITE_LANTERN`→Light
- `WHISPERING_GOURD_LANTERN` `[whispering_gourd_lantern]`
- `CINCINNASITE_FIRE_BOWL` `[cincinnasite_fire_bowl]`  — refs `CINCINNASITE_FORGED`→Metal
- `BRICKS_FIRE_BOWL` `[bricks_fire_bowl]`  — refs `NETHER_BRICK_TILE_LARGE`→Stone
- `NETHERITE_FIRE_BOWL` `[netherite_fire_bowl]`
- `CINCINNASITE_FIRE_BOWL_SOUL` `[cincinnasite_fire_bowl_soul]`  — refs `CINCINNASITE_FORGED`→Metal
- `BRICKS_FIRE_BOWL_SOUL` `[bricks_fire_bowl_soul]`  — refs `NETHER_BRICK_TILE_LARGE`→Stone
- `NETHERITE_FIRE_BOWL_SOUL` `[netherite_fire_bowl_soul]`

#### `block/NetherFurnitureBlocks` — Furniture (5 fields)

- `TABURET_CINCINNASITE` `[taburet_cincinnasite]`  — refs `CINCINNASITE_SLAB`→Metal
- `CHAIR_CINCINNASITE` `[chair_cincinnasite]`  — refs `CINCINNASITE_SLAB`→Metal, `NETHER_BRICK_TILE_LARGE`→Stone
- `BAR_STOOL_CINCINNASITE` `[bar_stool_cincinnasite]`  — refs `CINCINNASITE_SLAB`→Metal, `NETHER_BRICK_TILE_LARGE`→Stone
- `CHEST_OF_DRAWERS` `[chest_of_drawers]`  — refs `CINCINNASITE_BLOCK`→Metal
- `TRIMMED_MUSHROOM_FIR_CHEST` `[mushroom_fir_trimmed_chest]`  — refs `MAT_MUSHROOM_FIR`→Wood

#### `block/NetherFunctionalBlocks` — Functional (8 fields)

- `CINCINNASITE_PEDESTAL` `[cincinnasite_pedestal]`  — refs `CINCINNASITE_BLOCK`→Metal
- `PIG_STATUE_RESPAWNER` `[pig_statue_respawner]`  — refs `CINCINNASITE_BLOCK`→Metal
- `BLACKSTONE_FURNACE` `[blackstone_furnace]`
- `BASALT_FURNACE` `[basalt_furnace]`
- `NETHERRACK_FURNACE` `[netherrack_furnace]`
- `CINCINNASITE_FORGE` `[cincinnasite_forge]`  — refs `CINCINNASITE_BLOCK`→Metal
- `NETHER_BREWING_STAND` `[nether_brewing_stand]`
- `CINCINNASITE_ANVIL` `[cincinnasite_anvil]`  — refs `CINCINNASITE_BLOCK`→Metal

#### `block/NetherDecorBlocks` — Decor (10 fields)

- `CINCINNASITE_FRAME` `[cincinnasite_frame]`  — refs `CINCINNASITE_BLOCK`→Metal
- `CINCINNASITE_POT` `[cincinnasite_pot]`  — refs `CINCINNASITE_BLOCK`→Metal
- `BRICK_POT` `[brick_pot]`
- `GEYSER` `[geyser]`
- `ROOF_TILE_NETHER_BRICKS` `[roof_tile_nether_bricks]`
- `ROOF_TILE_NETHER_BRICKS_STAIRS` `[roof_tile_nether_bricks_stairs]`  — refs `ROOF_TILE_NETHER_BRICKS`→Decor
- `ROOF_TILE_NETHER_BRICKS_SLAB` `[roof_tile_nether_bricks_slab]`  — refs `ROOF_TILE_NETHER_BRICKS`→Decor
- `ROOF_TILE_CINCINNASITE` `[roof_tile_cincinnasite]`  — refs `CINCINNASITE_FORGED`→Metal
- `ROOF_TILE_CINCINNASITE_STAIRS` `[roof_tile_cincinnasite_stairs]`  — refs `ROOF_TILE_CINCINNASITE`→Decor
- `ROOF_TILE_CINCINNASITE_SLAB` `[roof_tile_cincinnasite_slab]`  — refs `ROOF_TILE_CINCINNASITE`→Decor

### Items (`NetherItems` -> `org.betterx.betternether.registry.item.*`)

#### `item/NetherFoodItems` — Food (6 fields)

- `BLACK_APPLE` `[black_apple]`
- `STALAGNATE_BOWL` `[stalagnate_bowl]`
- `STALAGNATE_BOWL_WART` `[stalagnate_bowl_wart]`
- `STALAGNATE_BOWL_MUSHROOM` `[stalagnate_bowl_mushroom]`
- `STALAGNATE_BOWL_APPLE` `[stalagnate_bowl_apple]`
- `HOOK_MUSHROOM_COOKED` `[hook_mushroom_cooked]`

#### `item/NetherEquipmentItems` — Equipment (10 fields)

- `CINCINNASITE_SET` _NetherSet_
- `NETHER_RUBY_SET` _NetherSet_
- `CINCINNASITE_DIAMOND_SET` _DiamondSet_  — refs `CINCINNASITE_SET`→Equipment
- `FLAMING_RUBY_SET` _NetherSet_  — refs `NETHER_RUBY_SET`→Equipment
- `CINCINNASITE_HAMMER` `[cincinnasite_hammer]`
- `CINCINNASITE_HAMMER_DIAMOND` `[cincinnasite_hammer_diamond]`
- `NETHER_RUBY_HAMMER` `[nether_ruby_hammer]`
- `CINCINNASITE_EXCAVATOR` `[cincinnasite_excavator]`
- `CINCINNASITE_EXCAVATOR_DIAMOND` `[cincinnasite_excavator_diamond]`
- `NETHER_RUBY_EXCAVATOR` `[nether_ruby_excavator]`

#### `item/NetherResourceItems` — Resource (8 fields)

- `CINCINNASITE` `[cincinnasite]`
- `CINCINNASITE_INGOT` `[cincinnasite_ingot]`
- `NETHER_RUBY` `[nether_ruby]`
- `GLOWSTONE_PILE` `[glowstone_pile]`
- `LAPIS_PILE` `[lapis_pile]`
- `AGAVE_LEAF` `[agave_leaf]`
- `AGAVE_MEDICINE` `[agave_medicine]`
- `HERBAL_MEDICINE` `[herbal_medicine]`

## 2. Boot order & ordering design (user-decision 4)

Category classes boot in **the plan-table order unchanged** (no category reorder was needed for BN),
items after blocks:

| # | Category | File |
|---|---|---|
| 1 | Terrain (nylium/mycelium/grass, farmland, veined sand) | `block/NetherTerrainBlocks` |
| 2 | Stone (nether-brick set, bone set, soul-sandstone set, basalt set, netherrack set, all stalactites) | `block/NetherStoneBlocks` |
| 3 | Metal (cincinnasite material + full building set, nether-ruby set) | `block/NetherMetalBlocks` |
| 4 | Ore | `block/NetherOreBlocks` |
| 5 | Obsidian (weeping/crying, obsidian + blue-obsidian brick/tile/glass sets) | `block/NetherObsidianBlocks` |
| 6 | Glass/Pane (quartz glass + coloured variants) | `block/NetherGlassBlocks` |
| 7 | Wood/trees (reed/stalagnate/willow/wart/rubeus/... material sets, vanilla wood sets) | `block/NetherWoodBlocks` |
| 8 | Leaves | `block/NetherLeavesBlocks` |
| 9 | Plant / DoublePlant | `block/NetherPlantBlocks` |
| 10 | WallPlant | `block/NetherWallPlantBlocks` |
| 11 | Crop | `block/NetherCropBlocks` |
| 12 | Sapling | `block/NetherSaplingBlocks` |
| 13 | Vine | `block/NetherVineBlocks` |
| 14 | Mushroom | `block/NetherMushroomBlocks` |
| 15 | Light (lanterns, fire bowls) | `block/NetherLightBlocks` |
| 16 | Furniture (chairs/stools/taburet, chests) | `block/NetherFurnitureBlocks` |
| 17 | Functional (furnaces, forge, brewing stand, anvil, pedestal, respawner) | `block/NetherFunctionalBlocks` |
| 18 | Decor/Misc (frame, pots, geyser, roof-tile sets) | `block/NetherDecorBlocks` |

Item files boot **Food -> Equipment -> Resource** (`NetherDiscItems` has no members and is not created):
`item/NetherFoodItems`, `item/NetherEquipmentItems`, `item/NetherResourceItems`.

Within each file, fields keep their **current source order** (families already contiguous), which is
human-sensible and keeps intra-file references backward.

### Placement decisions vs. the plan table (assignment, not reorder)
The plan's `Metal (+vanilla metal sets)` and `Stone (+... vanilla stone sets)` are read as "material
**sets** live with their material", so the mod's own material sets go there and boot early - which is
exactly what makes the late Furniture/Light/Functional/Decor dependents legal without any reorder:
- **cincinnasite material + full structural building set** (block, forged, pillar, bricks, brick_plate,
  stairs, slab, button, plate, tiles L/S, carved, bricks_pillar, bars, chain) and the **nether-ruby set**
  -> Metal (3). Ornamental cincinnasite pieces (`CINCINNASITE_FRAME`, `CINCINNASITE_POT`) -> Decor;
  `CINCINNASITE_PEDESTAL` -> Functional; `CINCINNASITE_LANTERN(_SMALL)` -> Light; the seats -> Furniture.
- **nether-brick, bone, soul-sandstone, basalt, netherrack building sets + all stalactites** -> Stone (2).
- `NetherDiscItems` from the taxonomy has no BetterNether members -> file omitted (not an addition).

**No brand-new category was invented for BN.** `MAT_WART` (the nether-wart-block material set) is the one
genuinely ambiguous object - filed under Wood as a material-set object; flag for the orchestrator if a
`Fungus`/`Wart` home is preferred (it has no cross-references, so the choice is order-free).

## 3. Cross-reference check & resolutions (correctness core)

Every RHS initializer was scanned for references to other fields (comments stripped; last-field span
bounded). All references resolve backwards; **zero violations**.

**Intra-file (auto-satisfied by preserved source order):**
- Stone: `NETHER_BRICK_WALL/TILE_SLAB/TILE_STAIRS -> NETHER_BRICK_TILE_LARGE/SMALL`; the full
  `BONE_*` set -> `BONE_BLOCK` (incl. `BONE_STALACTITE`, `BONE_REED_DOOR`, `BONE_CINCINNASITE_DOOR`);
  `SOUL_SANDSTONE_*` and `BASALT_BRICKS_*` families -> their bases.
- Metal: the cincinnasite building set -> `CINCINNASITE_FORGED` / `CINCINNASITE_BLOCK`;
  `NETHER_RUBY_STAIRS/SLAB -> NETHER_RUBY_BLOCK`.
- Obsidian: `OBSIDIAN_*` and `BLUE_OBSIDIAN_*` brick/tile/glass families -> their bases;
  `BLUE_OBSIDIAN -> BLUE_CRYING_OBSIDIAN`.
- Glass: `QUARTZ_GLASS_*_COLORED` / `*_PANE` -> their base glass.
- Decor: `ROOF_TILE_*_STAIRS/SLAB -> ROOF_TILE_*` base.
- Light: `CINCINNASITE_LANTERN_SMALL -> CINCINNASITE_LANTERN`.
- Vine: `LUMABUS_SEED -> LUMABUS_VINE`, `GOLDEN_LUMABUS_SEED -> GOLDEN_LUMABUS_VINE` (vines declared far
  earlier in source than the two deferred seeds, so still backward).

**Cross-file, satisfied by boot order (this is why the material sets go early):**
- Furniture (16) `TABURET/CHAIR/BAR_STOOL_CINCINNASITE -> CINCINNASITE_SLAB` (Metal 3) and
  `NETHER_BRICK_TILE_LARGE` (Stone 2); `CHEST_OF_DRAWERS -> CINCINNASITE_BLOCK` (Metal 3);
  `TRIMMED_MUSHROOM_FIR_CHEST -> MAT_MUSHROOM_FIR` (Wood 7).
- Light (15) fire bowls -> `CINCINNASITE_FORGED` (Metal 3) / `NETHER_BRICK_TILE_LARGE` (Stone 2);
  `CINCINNASITE_LANTERN -> CINCINNASITE_BLOCK` (Metal 3).
- Functional (17) `PIG_STATUE_RESPAWNER`, `CINCINNASITE_FORGE`, `CINCINNASITE_ANVIL`,
  `CINCINNASITE_PEDESTAL` -> `CINCINNASITE_BLOCK` (Metal 3).
- Decor (18) `CINCINNASITE_FRAME`, `CINCINNASITE_POT` -> `CINCINNASITE_BLOCK` (Metal 3);
  `ROOF_TILE_CINCINNASITE* -> CINCINNASITE_FORGED` (Metal 3).
- Glass (6) `QUARTZ_GLASS_FRAMED(_PANE) -> CINCINNASITE_BLOCK` (Metal 3).
- Leaves (8) `WILLOW_LEAVES -> MAT_WILLOW`, `RUBEUS_LEAVES -> MAT_RUBEUS`, `ANCHOR_TREE_LEAVES ->
  MAT_ANCHOR_TREE`, `NETHER_SAKURA_LEAVES -> MAT_NETHER_SAKURA` (all Wood 7).

**Result: no field had to leave its natural category and no category had to be reordered.** The single
lever that makes this work is putting the depended-upon material sets (cincinnasite, nether-brick, bone,
etc.) in the early Metal/Stone files rather than in the late Decor file. Had the cincinnasite building set
been placed in Decor (18), `CINCINNASITE_SLAB` would boot after Furniture (16) and break the seat blocks;
the Metal placement is therefore load-bearing, not cosmetic.

## 4. Bootstrap design

### Shrunken parent `NetherBlocks`
Keeps: `getBlockRegistry()`, `getModBlocks()`, `getModBlockItems()`; the R4-legal forwarders (the
`registerBlock(...)` overloads, `defineBlock(...)`, and the private `registerBlockNI(...)` helpers); and
`register()` (currently a no-op, called from `BetterNether.java:44`) rewritten into the **driver** calling
each category class's `ensureLoaded()` in boot order:

```
public static void register() {
    NetherTerrainBlocks.ensureLoaded();
    NetherStoneBlocks.ensureLoaded();
    NetherMetalBlocks.ensureLoaded();
    NetherOreBlocks.ensureLoaded();
    NetherObsidianBlocks.ensureLoaded();
    NetherGlassBlocks.ensureLoaded();
    NetherWoodBlocks.ensureLoaded();
    NetherLeavesBlocks.ensureLoaded();
    NetherPlantBlocks.ensureLoaded();
    NetherWallPlantBlocks.ensureLoaded();
    NetherCropBlocks.ensureLoaded();
    NetherSaplingBlocks.ensureLoaded();
    NetherVineBlocks.ensureLoaded();
    NetherMushroomBlocks.ensureLoaded();
    NetherLightBlocks.ensureLoaded();
    NetherFurnitureBlocks.ensureLoaded();
    NetherFunctionalBlocks.ensureLoaded();
    NetherDecorBlocks.ensureLoaded();
}
```
Each `Nether<Category>Blocks` holds its fields plus a `public static void ensureLoaded() {}` no-op.

### Shrunken parent `NetherItems`
Keeps `getItemRegistry()` and the item forwarders; add an `ensureStaticallyLoaded()` driver calling
`NetherFoodItems.ensureLoaded(); NetherEquipmentItems.ensureLoaded(); NetherResourceItems.ensureLoaded();`
and wire it into `BetterNether.java` **after** `NetherBlocks.register()` (block-before-item guarantee for
`BlockItem`s / for any item set that reads a block field). BetterNether currently loads items lazily; make
this call explicit next to line 44 so the item order is deterministic.

### ensureLoaded() ordering between block and item files
All 18 block category classes load (via `NetherBlocks.register()`) before the 3 item classes. BN has no
`block -> item -> block` clinit cycle analogous to BE's `SHADOW_BERRY/TERMINITE` (the cincinnasite item
sets read cincinnasite **items**, and `HERBAL_MEDICINE` does **not** reference the equipment sets - that
was a scan artifact from the last field's span, verified false), so block-then-item is sufficient.

### Reference rewrite (user-decision: YES, scripted, no facades)
`NetherBlocks.<FIELD>` -> `Nether<Category>Blocks.<FIELD>` (and items likewise) via `rewrite.js` /
`rewrite_all.sh` or word-boundary `sed`, driven by the section-1 map. **Feasibility (measured): ~733
`NetherBlocks.<FIELD>` external usages across 106 files.** 1:1 mapping -> deterministic; tooling adds the
new imports. **No facade fields remain on `NetherBlocks`.**

### Model / recipe factory statics (must move - they are shared across the new files)
`NetherBlocks` currently holds private statics used by several categories; after the split they must live
somewhere every category class can call:

- **Recipe-trait factories** `stalactiteRecipe(Block)` (9 call sites, Stone), `simple2x2Recipe(...)`
  (Stone: soul-sandstone, basalt), `furnaceRecipe(Block)` (Functional furnaces). Recommendation: a
  dedicated mod-local `registry.block.NetherRecipeTraits` helper (public static, returns `BlockTrait`),
  called inline as `.addTrait(NetherRecipeTraits.stalactite(source))` - keeps the RECIPE trait visible per
  R7/decision-2 style. (Keeping them as `public static` on the `NetherBlocks` parent is an acceptable
  fallback.)
- **Model-trait factories** `lumabusVineModelTrait(String)` (Vine, 2 call sites) and
  `netherSakuraLeavesModelTrait()` (Leaves). Recommendation: a dedicated `client/models/NetherModels`
  class, mirroring BetterEnd's existing `EndModelTraits` (user-decision 2: named pure `BlockModelTrait`
  factories in a dedicated class are allowed).

## 5. Sanity totals

**Blocks: 215 source fields -> 215 mapped (18 files).** Per file:
`NetherTerrainBlocks` 9, `NetherStoneBlocks` 38, `NetherMetalBlocks` 19, `NetherOreBlocks` 4,
`NetherObsidianBlocks` 24, `NetherGlassBlocks` 8, `NetherWoodBlocks` 21, `NetherLeavesBlocks` 4,
`NetherPlantBlocks` 23, `NetherWallPlantBlocks` 3, `NetherCropBlocks` 3, `NetherSaplingBlocks` 3,
`NetherVineBlocks` 11, `NetherMushroomBlocks` 13, `NetherLightBlocks` 9, `NetherFurnitureBlocks` 5,
`NetherFunctionalBlocks` 8, `NetherDecorBlocks` 10.
Sum = 9+38+19+4+24+8+21+4+23+3+3+3+11+13+9+5+8+10 = **215**. Original `NetherBlocks` count = **215**. Match.

**Items: 24 source fields -> 24 mapped (3 files; `NetherDiscItems` empty/omitted).**
`NetherFoodItems` 6, `NetherEquipmentItems` 10, `NetherResourceItems` 8. Sum = **24**. Original
`NetherItems` count = **24**. Match.
