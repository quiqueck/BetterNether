# BetterNether — Mineable-tags review (WP: mineable-audit)

Analysis-only report. **No code was changed.** It audits every BetterNether block's
mining-related tags against (a) its own material/category signals in the golden and
(b) the vanilla analog's tags, proposing `align` where a tag looks accidental, `keep`
where the deviation is deliberate, and `discuss` where a decision is needed.

## Source data

- Mineable tags: `src/main/generated/data/minecraft/tags/block/mineable/{pickaxe,axe,shovel,hoe}.json`
- Tool-tier tags: `needs_iron_tool.json` (2 entries), `needs_diamond_tool.json` (13 entries).
  BetterNether ships **no** `needs_stone_tool` file.
- Efficiency tags: `wover:mineable/shears` (16), `wover:mineable/sword` (1). No `hammer` tag.
- Material signals (`reqTool`, `destroyTime`, `class`, `instrument`, `sound`): `src/main/generated/block_properties.txt` (565 blocks).
- Analog pairing: `docs/vanilla-alignment-review.md`.

### The rule this audit enforces (mineable × tier × reqTool)

- `reqTool=true` **and no `mineable/*` tag → unharvestable** (never drops).
- `reqTool=true` + `mineable/*` + no `needs_*_tool` → wood-tier gate (fine for most stone/wood).
- `reqTool=false` + `mineable/*` → speed only (fine).
- `reqTool=false` + `needs_*_tool` → nonsense (none found — good).
- A block in **>1** `mineable/*` tag → the fastest matching tool wins; usually a datagen artifact.

Unlike BetterEnd, BetterNether **does** populate tier tags (iron for its ores, diamond for
obsidian) — so most of the findings here are *family-uniformity* gaps in those tier tags plus
a few double-tagged blocks, rather than mass unharvestability.

---

## Terrain, stone, wood, plants, leaves — the clean bulk

| category | current | expected (vanilla analog) | verdict |
|---|---|---|---|
| terrain (netherrack_moss + 5 grasses) | `pickaxe`, reqTool=true | netherrack = `pickaxe` reqTool=true | **ok** |
| stone sets (nether_brick tile/roof, soul_sandstone, basalt, bone) | `pickaxe`, reqTool=true | nether_bricks/blackstone = `pickaxe` | **ok** |
| obsidian base blocks (9, dt=50) | `pickaxe` + `needs_diamond_tool` | obsidian = `pickaxe`+`needs_diamond_tool` | **ok** |
| wood sets & trees (303 WOOD-sound blocks) | `axe` only | crimson_*/warped_* = `axe` | **ok** — 0 WOOD-sound blocks leaked into pickaxe |
| leaves (4: anchor_tree/nether_sakura/rubeus/willow) | `hoe` + `wover:shears` | oak_leaves = `hoe` (+ shears) | **ok** |
| plants/vines/mushrooms (~22 untagged) | no mineable tag, reqTool=false, dt=0.0 | crimson_roots/nether_sprouts = no mineable tag | **ok** |

No STONE/METAL-sound block is in axe and no WOOD-sound block is in pickaxe — material↔tool
alignment is clean.

---

## A. Family divergence — obsidian slabs/stairs missing `needs_diamond_tool` (8 blocks) → **align**

The base obsidian bricks/tiles are `needs_diamond_tool`, but their slab/stairs variants —
identical material (dt=50.0/1200, reqTool=true, STONE, in `pickaxe`) — were left out, so a
**stone/iron pickaxe drops them** while the base block needs diamond.

| block (group) | current tier | expected | verdict |
|---|---|---|---|
| `obsidian_bricks_slab`, `obsidian_bricks_stairs`, `obsidian_tile_slab`, `obsidian_tile_stairs`, `blue_obsidian_bricks_slab`, `blue_obsidian_bricks_stairs`, `blue_obsidian_tile_slab`, `blue_obsidian_tile_stairs` (8) | none (stone-tier effective) | base `obsidian_bricks`/`obsidian_tile` (+ blue) are `needs_diamond_tool` → same | **align** — add 8 to `needs_diamond_tool` |

(`obsidian_bricks`, `obsidian_tile`, `obsidian_tile_small`, `obsidian_rod_tiles` and their blue
twins, `weeping_obsidian`, `blue_weeping_obsidian`, `blue_crying_obsidian`, `nether_ruby_ore`
are already correctly in `needs_diamond_tool`.)

## B. Multiple mineable tags — datagen artifacts (8 blocks) → **align / discuss**

| block (group) | current mineable | expected | verdict |
|---|---|---|---|
| `anchor_tree_sapling`, `mushroom_fir_sapling`, `nether_sakura_sapling`, `rubeus_sapling`, `stalagnate_seed`, `wart_seed`, `willow_sapling` (7) | **`axe` + `hoe`** | BetterEnd saplings are `hoe`-only; vanilla saplings have no mineable tag | **align** — remove from `axe`, keep `hoe` |
| `bar_stool_cincinnasite` (1) | **`pickaxe` + `axe`** | cincinnasite = metal → `pickaxe`; its wood-seat cousins are `axe` | **discuss** — see below |

- The 7 saplings/seeds are double-tagged because the tree-set datagen dropped them into `axe`
  while the plant datagen put them in `hoe`. BetterEnd's equivalent saplings are hoe-only, so
  removing the `axe` membership is the consistent fix.
- `bar_stool_cincinnasite` is a **metal** stool in both `pickaxe` and `axe`, while its siblings
  `chair_cincinnasite` and `taburet_cincinnasite` are `axe`-**only** (and all other cincinnasite
  blocks — pedestal, forge, roof_tile, bars — are `pickaxe`). So the three cincinnasite seats
  diverge three ways. Recommend making **all three cincinnasite seats `pickaxe`-only** (metal),
  removing them from `axe`. **Confidence MED** (furniture has no strict vanilla analog).

## C. Ore-tier inconsistency → **discuss (LOW–MED confidence)**

| block | current tier | vanilla analog tier | verdict |
|---|---|---|---|
| `nether_redstone_ore` | **none (wood)** | redstone_ore = `needs_iron_tool` | **discuss** — under-tiered, and internally *below* `nether_lapis_ore` |
| `nether_lapis_ore` | `needs_iron_tool` | lapis_ore = `needs_stone_tool` | **keep** — deliberate "tougher nether ore" (matches its 5.0 resistance) |
| `cincinnasite_ore` | `needs_iron_tool` | (mod's iron-tier metal) | **keep** — iron-analog ore |
| `nether_ruby_ore` | `needs_diamond_tool` | (end-game gem) | **keep** — deliberate |

The concrete inconsistency: `nether_redstone_ore` (reqTool=true, dt=3.0, in `pickaxe`, **no tier
tag**) is mineable with a wooden pickaxe, yet `nether_lapis_ore` alongside it needs an iron
pickaxe — the reverse of vanilla, where redstone (iron-tier) is gated *above* lapis (stone-tier).
Recommend at least `needs_iron_tool` for `nether_redstone_ore` for internal + vanilla parity.
**Confidence LOW–MED** — depends on whether the nether-ore tiering is deliberate.

## D. Metal blocks lack a tier tag → **discuss**

`cincinnasite_block` and `nether_ruby_block` (both iron-analog, dt=5.0/6.0, IRON_XYLOPHONE) are
`pickaxe` with **no tier tag** → wooden pickaxe drops them. Vanilla `iron_block` is
`needs_stone_tool`. Consistent with the mod not gating metal *blocks* by tier; flag `discuss`
if vanilla parity (add `needs_stone_tool`) is wanted.

## E. `agave` — reqTool anomaly → **discuss (LOW confidence)**

`agave` (class `BlockAgave`, WOOL, dt=0.0) has **`reqTool=true`** but sits in **no vanilla
mineable tag** — only in `wover:mineable/shears`. Its plant siblings `barrel_cactus` and
`nether_cactus` (also WOOL) are `reqTool=false`. If the wover shears tag does not count as a
"correct tool" for drops, `agave` is effectively unharvestable; the more likely intent is
`reqTool=false`. This is a block-property fix (outside strict tag scope) — **flag for decision,
confidence LOW** on the wover-shears harvest semantics.

## F. Sword-efficient convention — barely populated → **discuss (LOW confidence)**

`wover:mineable/sword` contains a single block (`nether_reed_stem`); vanilla's
`minecraft:sword_efficient` (plants, leaves, …) is not populated. So BetterNether's plants/vines
and 4 leaf blocks are not sword-efficient the way their vanilla analogs are. **Confidence LOW**
on whether the mod intends this convention. (BetterEnd ships *no* sword tag at all — the two
mods are inconsistent with each other here.)

## G. Minor / cross-referenced (no tag action here)

- **Glass in `pickaxe`**: all `quartz_glass*` and `*_glass_pane` variants are in `mineable/pickaxe`,
  but vanilla `glass`/`glass_pane` are in **no** mineable tag (no tool mines glass faster). reqTool=false,
  so this is a harmless speed-only deviation. Exception: `obsidian_glass_pane` / `blue_obsidian_glass_pane`
  carry `reqTool=true` at dt=0.3 — already a `discuss` in `vanilla-alignment-review.md` §Glass & panes.
- `veined_sand` is the sole `mineable/shovel` member — correct (sand analog).

---

## Proposed change list (align items only)

**Add to `minecraft:tags/block/needs_diamond_tool`:**

1. `obsidian_bricks_slab`, `obsidian_bricks_stairs`, `obsidian_tile_slab`, `obsidian_tile_stairs`, `blue_obsidian_bricks_slab`, `blue_obsidian_bricks_stairs`, `blue_obsidian_tile_slab`, `blue_obsidian_tile_stairs` — obsidian-tier slabs/stairs currently minable with a stone pickaxe while their base blocks need diamond.

**Remove from `minecraft:tags/block/mineable/axe` (keep in `hoe`):**

2. `anchor_tree_sapling`, `mushroom_fir_sapling`, `nether_sakura_sapling`, `rubeus_sapling`, `stalagnate_seed`, `wart_seed`, `willow_sapling` — double-tagged; saplings should be hoe-only.

**Discuss before applying:** `bar_stool_cincinnasite`/`chair_cincinnasite`/`taburet_cincinnasite` → pickaxe-only (§B); `nether_redstone_ore` → `needs_iron_tool` (§C); metal blocks → `needs_stone_tool` (§D); `agave` reqTool (§E); `sword_efficient` (§F).

## Counts

| classification | blocks | notes |
|---|---|---|
| **align** | **15** | 8 obsidian slabs/stairs → diamond tier + 7 saplings de-double-tagged |
| **discuss** | ~7 topics | cincinnasite seats, redstone-ore tier, metal-block tier, agave reqTool, sword_efficient, glass-in-pickaxe |
| **ok / keep** | ~550 | terrain, stone, obsidian(9), wood(303), leaves(4), ~22 plants; correct iron/diamond tiers on ores/obsidian |
| blocks audited | **565** | |
