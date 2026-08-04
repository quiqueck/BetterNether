# BetterNether — Vanilla-alignment review (WP8.1)

Analysis-only report. **No code was changed.** It pairs every BetterNether block with a
hand-curated vanilla analog and diffs the golden columns so a human can decide, per
mismatch, whether we should adopt vanilla's value (**align**), keep our intentional
deviation (**keep-deliberate**), or think about it (**discuss**).

## How to read this

- **Source data**: `src/main/generated/block_properties.txt` (vanilla blocks live in the same
  file, so analogs are read from there) and `src/main/generated/block_registrations.txt`
  (`flammable=burn/spread`, `compostable=`, `fuel=`).
- **Analog choice**: from the block's category class (`registry/block/Nether*Blocks.java`)
  + name + material. BetterNether trees are **nether woods**, so wood-set parts pair with
  `minecraft:crimson_*` / `minecraft:warped_*`, not overworld wood. Furniture with no vanilla
  counterpart is `analog=none`.
- **Columns diffed**: `destroyTime, resistance, reqTool, instrument, sound, friction,
  pushReaction, ignitedByLava, lightEmission, offsetType, renderLayer`, the default-state
  predicate columns, and the `block_registrations.txt` columns.
- **Proposal legend**: `align` = adopt vanilla's value (looks accidental); `keep` =
  intentional Nether design difference; `discuss` = unclear.
- Tables list **only mismatches**; family-wide verdicts show one representative row with
  "(applies to N blocks)".

### Two important golden caveats

1. **`compostable=` and `fuel=` are empty/`?`** for every block, vanilla included
   (`ComposterBlock.COMPOSTABLES` / `FuelValues` aren't populated at datagen — WP0.4). No
   composting or fuel-*value* proposals are made from the golden; the fuel policy in
   §"Fuel & flammability" is prescriptive (decision 6).
2. **Custom `SoundType`s serialize as `sound=path:block.<x>.break`.** Nether terrain reports
   `sound=path:block.netherrack.break` — a real custom sound (netherrack break), intentional,
   not a missing value.

---

## Terrain (`BlockTerrain`, 6: mushroom/jungle/swampland grass, netherrack_moss, …)

Analog: `minecraft:netherrack` (0.4/0.4, reqTool=true) / `minecraft:crimson_nylium`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| netherrack_moss *(applies to all 6)* | netherrack | destroyTime/resistance/reqTool | 0.4 / 0.4 / true | 0.4 / 0.4 / true | — | aligned. |
| netherrack_moss *(all 6)* | netherrack | sound | path:block.netherrack.break | NETHERRACK | keep | Deliberate custom netherrack-break sound. |

No action — terrain matches netherrack cleanly.

## Stone sets (nether bricks, soul sandstone, basalt-like, …)

Analog: `minecraft:nether_bricks` (2.0/6.0), `minecraft:blackstone`, `minecraft:basalt`,
`minecraft:soul_soil`.

Nether stone sets sit at 2.0/6.0 (nether-brick tier), `reqTool=true`, `sound=STONE/NETHER_BRICKS`.
No internal leaks were found (unlike BetterEnd). All `destroyTime/resistance` diffs vs the
softer vanilla analogs are the intended nether-brick hardness → **keep**. No action.

## Metal (cincinnasite, nether ruby + sets)

Analog: `minecraft:iron_block` (5.0/6.0, IRON, IRON_XYLOPHONE).

| block | analog | column | ours | vanilla / base | proposal | reason |
|---|---|---|---|---|---|---|
| cincinnasite_block | iron_block | destroyTime/resistance | 3.0 / 10.0 | 5.0 / 6.0 | keep | Nether metal tuned harder; IRON_XYLOPHONE instrument matches. |
| cincinnasite_block | iron_block | sound | METAL | IRON | discuss | Uses the generic `METAL` SoundType rather than iron's `IRON`; minor, but a whole metal family reads slightly different from vanilla iron. |
| nether_ruby_block | iron_block | instrument | BASEDRUM | IRON_XYLOPHONE | **discuss/align** | Base block uses the **stone** instrument (BASEDRUM) while its own `nether_ruby_slab`/`_stairs` use IRON_XYLOPHONE — internally inconsistent. Align the base to IRON_XYLOPHONE. |
| nether_ruby_block | iron_block | destroyTime/resistance | 5.0 / 6.0 | 5.0 / 6.0 | — | hardness already matches iron. |

## Ore (cincinnasite, nether lapis/redstone/ruby ore)

Analog: `minecraft:nether_gold_ore` / `minecraft:nether_quartz_ore` (3.0/3.0), `minecraft:ancient_debris`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| *_ore *(all 4)* | nether_quartz_ore | resistance | 5.0 | 3.0 | keep | Slightly tougher nether ores by design. |
| *_ore *(all 4)* | nether_quartz_ore | sound | NETHERRACK | NETHER_ORE | **discuss** | Ours use the plain netherrack break sound; vanilla nether ores have a dedicated `NETHER_ORE`/`NETHER_GOLD_ORE` sound. Align for parity? |
| nether_redstone_ore | redstone_ore | lightEmission | 0..9#2 (lit) | 0/9 lit | — | lit-when-active correctly modelled. |

## Obsidian (`BNObsidian`, 9: blue/regular obsidian + bricks/tiles)

Analog: `minecraft:obsidian` (50.0/1200, BASEDRUM, STONE) / `minecraft:crying_obsidian`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| blue_obsidian *(all 9)* | obsidian | all material cols | 50.0/1200 BASEDRUM STONE | identical | — | **fully aligned.** No action. |

## Glass & panes (`BaseGlassBlock` 34, `BNPane` 37)

Analog: `minecraft:glass` / `minecraft:tinted_glass` / `minecraft:glass_pane` / `minecraft:iron_bars`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| quartz_glass *(plain glass blocks)* | glass | renderLayer | TRANSLUCENT | CUTOUT | keep | BN glass renders translucent (tinted-glass style) on purpose. Material 0.3/0.3/GLASS/reqTool=false matches glass. |
| obsidian_glass_pane / blue_obsidian_glass_pane | glass_pane | reqTool | true | false | **discuss** | Obsidian-glass panes require a tool yet only have 0.3 destroyTime — reqTool=true with such low hardness is unusual; may be a leftover. |
| obsidian_glass_pane *(obsidian glass)* | glass_pane | sound | STONE | GLASS | discuss | Obsidian-glass theme may intend STONE. |
| cincinnasite_bars | iron_bars | destroyTime/resistance | 0.3 / 0.3 | 5.0 / 6.0 | **discuss** | Metal "bars" inherit glass-pane hardness (0.3) instead of iron-bars hardness (5.0/6.0). Should decorative nether metal bars be iron-bars-tough, or stay fragile? |

## Wood sets & trees (`sound=WOOD`, 303 wood-sound blocks; 9 tree species)

Analog: `minecraft:crimson_*` / `minecraft:warped_*` (nether wood).
Species: anchor_tree, mushroom_fir, nether_mushroom, nether_reed, nether_sakura, rubeus,
stalagnate, wart, willow. Material: 2.0/3.0, instrument BASS, `ignitedByLava=false`
(non-flammable — correct per decision 6).

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| *_planks *(all sets)* | crimson_planks | ignitedByLava, instrument, hardness | false, BASS, 2.0/3.0 | same | keep | non-flammable nether wood — matches vanilla + decision 6. |
| *_planks *(all sets)* | crimson_planks | sound | WOOD | NETHER_WOOD | **discuss** | Vanilla nether **planks** sound = `NETHER_WOOD`; ours use generic `WOOD`. Align to NETHER_WOOD for nether flavour, or keep plain WOOD? |
| *_log / *_bark / *_stem *(all sets)* | crimson_stem | sound | WOOD | STEM | **discuss** | Vanilla nether **stems/hyphae** sound = `STEM`; ours use `WOOD`. Same decision. |
| *_log / *_bark *(all sets)* | crimson_stem | resistance | 3.0 | 2.0 | keep | Nether logs slightly tougher than vanilla stems; minor design. |

## Plants, vines, mushrooms, saplings

Analog: `minecraft:crimson_roots` / `minecraft:nether_sprouts` / `minecraft:weeping_vines` /
`minecraft:crimson_fungus`. Spot checks show the expected soft-plant profile
(`pushReaction=DESTROY`, `sound=GRASS/VINE/CROP`, cutout render). No systematic mismatches
surfaced — no action.

## Furniture (`NetherFurnitureBlocks`: taburet, chair, bar_stool + chests/barrels)

Analog = none for the seating; chests/barrels follow their wood set. One anomaly:

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| mushroom_fir_trimmed_chest | (nether wood) | flammable | 5/5 | 0/0 | **align** | The **only** flammable block in all of BetterNether. Nether woods are non-flammable (decision 6) — this one chest was missed. Set `flammable=0/0`. |

---

## Fuel & flammability (user decision 6)

**Current golden state** (`block_registrations.txt`):
- **`flammable`**: exactly **1** block is flammable — `mushroom_fir_trimmed_chest` (`5/5`).
  Every other BetterNether block is `0/0`. This confirms nether woods are already
  non-flammable; the one chest is an oversight (see the `align` row above).
- `fuel=?` for all blocks — **fuel is not captured by the golden** (datagen limitation), so
  current fuel state must be read from code/traits in the apply phase, not diffed here.

**Policy (decision 6):** nether woods stay non-flammable; **bark/log/stem/trunk = NOT furnace
fuel**; **planks + stripped variants = furnace fuel**. Note this is a **deliberate deviation
from vanilla**, where crimson/warped planks are *not* fuel — BetterNether intentionally makes
its nether planks burnable in a furnace.

**Exact block lists (from the 9 wood sets):**

**NOT fuel — bark / log / stem / trunk (21 blocks):**
`anchor_tree_bark, anchor_tree_log, mushroom_fir_bark, mushroom_fir_log, mushroom_fir_stem,
mushroom_fir_trunk, nether_mushroom_stem, nether_reed_stem, nether_sakura_bark,
nether_sakura_log, rubeus_bark, rubeus_log, stalagnate_bark, stalagnate_log, stalagnate_stem,
stalagnate_trunk, wart_bark, wart_log, willow_bark, willow_log, willow_trunk`

**Fuel — planks (9 blocks):**
`anchor_tree_planks, mushroom_fir_planks, nether_mushroom_planks, nether_reed_planks,
nether_sakura_planks, rubeus_planks, stalagnate_planks, wart_planks, willow_planks`

**Fuel — stripped variants (14 blocks):**
`anchor_tree_stripped_bark, anchor_tree_stripped_log, mushroom_fir_stripped_bark,
mushroom_fir_stripped_log, nether_sakura_stripped_bark, nether_sakura_stripped_log,
rubeus_stripped_bark, rubeus_stripped_log, stalagnate_stripped_bark, stalagnate_stripped_log,
wart_stripped_bark, wart_stripped_log, willow_stripped_bark, willow_stripped_log`

> **Open sub-question for the apply phase:** decision 6 names "planks + stripped variants" as
> fuel. Plank-*derived* building blocks (stairs, slabs, fences, doors, buttons, signs, …) are
> not named. In vanilla all wooden building blocks are furnace fuel; here they are left
> unspecified. Recommend an explicit yes/no on whether plank-derived parts are also fuel.

---

## Summary

| classification | count | notes |
|---|---|---|
| **align** | **1 block** (+ `nether_ruby_block` instrument borderline) | `mushroom_fir_trimmed_chest` flammable 5/5→0/0; `nether_ruby_block` instrument BASEDRUM→IRON_XYLOPHONE (internally inconsistent). |
| **discuss** | ~5 topics | wood sound WOOD vs NETHER_WOOD/STEM; ore sound NETHERRACK vs NETHER_ORE; cincinnasite sound METAL vs IRON; obsidian-glass-pane reqTool/sound; metal bars hardness. |
| **keep-deliberate** | bulk | nether stone/metal/ore hardness, custom netherrack sound, translucent glass, non-flammable nether wood, plank-as-fuel deviation. |
| blocks paired | 565 / 565 | furniture/functional paired as `analog=none`. |

### Proposed change list (align + discuss only — strike what you reject)

**Align (recommend applying):**
1. `mushroom_fir_trimmed_chest` → `flammable=0/0` (only flammable block in the mod; nether wood must be non-flammable).
2. `nether_ruby_block` → instrument BASEDRUM→IRON_XYLOPHONE (match its own slab/stairs and the metal sound).

**Discuss (decide, then maybe apply):**
3. Wood sets sound `WOOD` → `NETHER_WOOD` (planks/building) and `STEM` (logs/bark/stems) to match vanilla nether wood? (~303 blocks) Or keep plain WOOD.
4. Nether ores sound `NETHERRACK` → `NETHER_ORE` for parity with vanilla nether ores?
5. `cincinnasite_*` metal sound `METAL` → `IRON`?
6. Obsidian-glass panes: reqTool true→false and/or sound STONE→GLASS?
7. `cincinnasite_bars` (and other metal bars): raise hardness from 0.3/0.3 toward iron_bars 5.0/6.0, or keep fragile-decorative?
8. Plank-derived building blocks (stairs/slabs/fences/…): fuel or not? (fuel-policy gap, see above).
