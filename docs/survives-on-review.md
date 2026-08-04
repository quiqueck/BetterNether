# SurvivesOn Consistency Review — BetterNether

Audit of every BetterNether block whose placement/survival is constrained, checked against the
target rule for decoration plants:

> **All wall-placeable and roof-placeable (hanging/down-facing) plants should be usable as
> decorations: placeable on ANY block that is solid on the attachment face (vanilla
> `isFaceSturdy`) OR is in `minecraft:leaves`.** Ground-standing plants keep their curated
> ground lists.

This is an **analysis + design** document. No source or gradle changes are made here. The
shared mechanism is intended to live in **BCLib** and be used by both BetterEnd and BetterNether
— see BetterEnd/docs/survives-on-review.md for the BE side of the same design.

## How survival is decided (the three mechanisms)

1. **`SurvivesOnBlockTrait`** (BCLib). Per-block runtime ground list, consulted via
   `VegetationBlockMixin` (vanilla `VegetationBlock` subclasses) or
   `BasePlantBlock.isTerrain`. BN's curated ground lists are assembled in
   `blocks/NetherSurvival.java` (`netherGround()`, `nylium()`, `soulGround()`, …) and handed to
   block definitions at registration.
2. **`SurvivesOnSolidTrait`** (BCLib) — "any sturdy up-face" marker for `VegetationBlock`.
   **Currently unused by BetterNether.**
3. **Class `canSurvive` / `updateShape` overrides** — most BN wall, roof and vine blocks decide
   survival in code and never consult the trait. This is where the wall/roof rule applies.

### Key structural finding

BN's roof-hanging **vines** already satisfy the target rule: they extend BCLib
`BaseVineBlock` / `BaseSimpleVineBlock` (→ `AbstractVineBlock.isSupport` =
`up.is(this) || up.is(BlockTags.LEAVES) || canSupportCenter(up, DOWN)`), i.e. **any solid +
leaves already**.

The restrictive ones are:
- **`BlockPlantWall`** (wall_moss, wall_mushroom_*) — `canSurvive` = `targetState.isFaceSturdy(face)`
  only. Any sturdy face, **no leaves** (BlockPlantWall.java:62-67).
- **`BlockMossCover`** (floor mold) — `isFaceSturdy(below, UP)` only, no leaves.
- **Hardcoded-whitelist roof structures** — `BlockAnchorTreeVine`, `BlockWillowBranch`,
  `BlockEyeBase`/`BlockEyeVine` restrict the block *above* to their own tree's leaves /
  `NETHERRACK` (or just "not air"). These are worldgen structure parts, and most have **no
  block item** (not player-placeable).

## Inventory

Orientation legend: **W** wall, **RH** roof/hanging, **G** ground, **FC** floor-cover,
**RS** roof-structure (worldgen).

| Block id | Class | Orient | Mechanism | Current support | Proposed |
|---|---|---|---|---|---|
| wall_moss | BlockPlantWall | W | class `canSurvive` = `isFaceSturdy(face)` | any sturdy face | **+ leaves** (via shared helper) |
| wall_mushroom_brown | BlockPlantWall | W | ″ | any sturdy face | **+ leaves** |
| wall_mushroom_red | BlockPlantWall | W | ″ | any sturdy face | **+ leaves** |
| black_vine | BlockBlackVine (BaseSimpleVineBlock) | RH | `AbstractVineBlock.isSupport` = self/LEAVES/`canSupportCenter` | any solid + leaves | no change (already matches) |
| blooming_vine | BlockBlackVine | RH | ″ | any solid + leaves | no change |
| golden_vine | BlockGoldenVine (BaseSimpleVineBlock) | RH | ″ | any solid + leaves | no change |
| lumabus_vine | BlockLumabusVine (BaseVineBlock) | RH | ″ (no block item) | any solid + leaves | no change |
| golden_lumabus_vine | BlockLumabusVine | RH | ″ (no block item) | any solid + leaves | no change |
| whispering_gourd_vine | BlockWhisperingGourdVine (BaseVineBlock.Growing) | RH | ″ | any solid + leaves | no change |
| eye_vine | BlockEyeVine (BaseVineBlock) | RH | ″ (no block item; clone = EYE_SEED) | any solid + leaves | no change |
| moss_cover | BlockMossCover | FC | class: `isFaceSturdy(below, UP)` | any solid up-face | optional + leaves (see notes) |
| soul_vein | BlockSoulVein | G | class: `survivesOn(below)` trait `netherSand` | curated ground | keep (ground) |
| nether_reed_stem | BlockNetherReed | G | class: `isNetherGround(below) && adjacent lava`, or self below | curated ground/lava edge | keep (ground) |
| anchor_tree_vine | BlockAnchorTreeVine | RS | `updateShape`: above ∈ {self, ANCHOR_TREE_LEAVES, NETHERRACK} (no block item) | own tree only | see open questions |
| willow_branch | BlockWillowBranch | RS | `updateShape`: above ≠ air | anything non-air above | see open questions |
| eye_base / eyeball | BlockEyeBase | RS | `updateShape`: above ∈ {EYE_VINE, NETHERRACK} | eye plant only | see open questions |

Ground plants/mushrooms/saplings (`NetherPlantBlocks`, `NetherMushroomBlocks`) use
`SurvivesOnBlockTrait` via `NetherSurvival.*` and keep their curated Nether-ground lists —
untouched by this rule.

## Counts (BetterNether)

- Wall (W): **3** (all become more permissive: + leaves).
- Roof/hanging vines (RH): **7** — **all already match**, no change (5 of them have no block
  item, so player placement is moot regardless).
- Floor-cover (FC): **1** (moss_cover) — optional.
- Ground (G): **2** (soul_vein, nether_reed_stem) — out of scope, keep curated.
- Roof-structure worldgen (RS): **3** (anchor_tree_vine, willow_branch, eye_base) — open
  question; mostly no block item.

## Proposed shared mechanism

The same single BCLib predicate proposed on the BetterEnd side, used by both mods:

```java
// org.betterx.bclib.util.BlocksHelper
public static boolean isDecorationSupport(
        BlockGetter level, BlockPos supportPos, BlockState support, Direction face) {
    return support.is(net.minecraft.tags.BlockTags.LEAVES)
        || (support.isSolid() && support.isFaceSturdy(level, supportPos, face));
}
```

Routing in BetterNether:

- **`BlockPlantWall.canSurvive`** → replace `targetState.isFaceSturdy(level, targetPos, direction)`
  with `BlocksHelper.isDecorationSupport(level, targetPos, targetState, direction)`. Covers all
  three wall plants. (Note this also tightens the "solid" half — vanilla `isFaceSturdy` already
  implies a real surface, so no regression.)
- **`BlockMossCover.canSurvive`** (optional) → same helper with `Direction.UP`.
- BCLib `AbstractVineBlock.isSupport` (the parent of every BN vine) already equals the helper's
  semantics; optionally refactor onto it for uniformity (no behaviour change).

Rationale (same as BE): wall/roof plants decide survival in class `canSurvive` overrides that
bypass the trait/mixin path, so a pure trait cannot drive them. A single static predicate is the
one definition every code path can share across BCLib + BE + BN.

## Blocks whose placement becomes MORE PERMISSIVE

- **wall_moss, wall_mushroom_brown, wall_mushroom_red** — gain **leaves** as valid attachment
  (were already any-sturdy-face).
- (optional) **moss_cover** — gains leaves as valid floor if the FC case is included.

The 7 roof/hanging vines already accepted any-solid + leaves, so nothing changes there.

## Golden-file visibility

**Nothing golden-visible is expected.** Survival is runtime `canSurvive`/`updateShape` code, not
datagen; generated blockstates, models, loot tables, tags and recipes are unaffected.
`SurvivesOnBlockTrait` produces no datagen output.

## Applied (2026-07-22)

Implemented against the shared BCLib mechanism (see BetterEnd/docs/survives-on-review.md for the
predicate + tooltip pipeline). "Roof" = ceiling-hanging throughout.

**BetterNether deltas.**
- `[Changed]` Wall plants `wall_moss`, `wall_mushroom_brown`, `wall_mushroom_red`:
  `BlockPlantWall.canSurvive` now uses `BlocksHelper.isDecorationSupport(level, targetPos,
  targetState, facing)` (adds leaves; the old check was `isFaceSturdy` only). Each registration
  gained `SurvivesOnSolidTrait.DEFAULT` for the auto-generated "any solid block or leaves"
  tooltip.
- `[Changed]` Ceiling hangers `anchor_tree_vine`, `willow_branch`, `eye_base`: `updateShape`
  now keeps the block if the block above is the plant's own self/stem (self-chaining) OR passes
  `isDecorationSupport(above, DOWN)`. **Old-anchor superset verified:**
  - anchor_tree_vine old anchors {self, ANCHOR_TREE_LEAVES, NETHERRACK} — self→self,
    ANCHOR_TREE_LEAVES ∈ `minecraft:leaves` (via `LeavesBlockTrait`), NETHERRACK sturdy solid →
    all pass.
  - willow_branch old rule was "above ≠ air"; worldgen (`WillowTreeFeature.vine`) always anchors
    the top branch under WILLOW_LEAVES (∈ leaves) with self-chaining below → all worldgen anchors
    pass. (This is the one case where the new rule is *stricter* than the old code for arbitrary
    player placements, but matches the decoration rule and leaves worldgen intact.)
  - eye_base old anchors {EYE_VINE, NETHERRACK} — EYE_VINE kept as an explicit self/stem case,
    NETHERRACK sturdy solid → all pass.
  These three mostly have no block item, so the change is worldgen-safe and largely invisible to
  players; behavior for the generated structures is unchanged.

**Become-more-permissive (runtime, golden-invisible).** 3 wall plants gain leaves; the 3 ceiling
hangers gain any-solid/leaves ceilings (superset of old anchors). Roof vines already matched —
untouched.

**Golden/lang deltas.** Cache-purged `runDatagenClient` produced **zero** changes to
`src/main/generated` and **zero** BetterNether `lang` changes (the shared tooltip key lives in
BCLib). Placement changes are runtime-only.

**Ground cleanups — deferred, NOT applied (documented per the "fix only if confirmed" guardrail).**
- **BlockNetherReed → NetherSurvival trait:** *not applied.* No existing `NetherSurvival` list is
  identical to the reed's ground set: the reed uses `BlocksHelper.isNetherGround(down)` (=
  NETHER_STONES + soul + NETHER_MYCELIUM + NYLIUM), whereas `NetherSurvival.netherGround()` adds
  `RED_SAND` + `SAND`. Swapping would widen the ground set (behavior change), and the reed's rule
  also carries adjacent-lava and self-chaining logic that a `SurvivesOnBlockTrait` cannot express
  (the class `canSurvive` must stay regardless). A clean, identical swap isn't available without
  adding a bespoke new list purely to wrap four tags while leaving the class override in place —
  pure churn with no consistency win. Flagged for a follow-up decision.
- **soul_vein `netherSand` vs `soulGround`:** *not changed.* `NetherTags.NETHER_SAND` = exactly
  `{SOUL_SAND}` (`NetherBlockTagDataProvider`). No worldgen evidence was found that `soul_vein`
  is ever placed on `SOUL_SOIL` (it only appears in `WartTreeFeature.isReplaceable`). Absent a
  confirmed placement on soul soil, treating "survives on soul sand only" as deliberate; left
  as-is pending confirmation.

## Ground-plant inconsistencies (separate — suggestions only, NOT part of the wall/roof rule)

1. **`NetherReed` vs the trait system.** `BlockNetherReed.canSurvive` hand-rolls
   `BlocksHelper.isNetherGround(down) && adjacentLava`, duplicating logic that `NetherSurvival`
   otherwise expresses as `SurvivesOnBlockTrait` lists. It works, but it is the one ground rule
   that lives entirely in class code and won't track changes to `NetherSurvival.netherGround()`.
   Suggestion: leave functionally, but note the divergence.
2. **`soul_vein` uses `netherSand`, not `soulGround`.** `BlockSoulVein` survives on the
   `NETHER_SAND` tag while conceptually a soul-themed plant might be expected on `SOUL_GROUND`.
   Confirm this is intentional (soul sand *is* in netherSand? verify the tag membership) — a
   possible mismatch worth a glance, not a bug.
3. **`magmaBlockOrSand` / `boneBlocks` block-lists.** `NetherSurvival` mixes tag-based rules with
   hardcoded `withBlocks(...)` lists (e.g. `magmaBlockOrSand`, `boneBlocks`, `gravel`,
   `soulSand`). These are fine but, like BE, diverge from any equivalent tag if one exists.
   Low priority.

## Open questions

- **Roof-structure worldgen parts (anchor_tree_vine, willow_branch, eye_base).** These restrict
  the block above to their own tree/plant and are part of generated structures; most have **no
  block item**, so opening them to any-solid-or-leaves has no player-facing effect and could
  destabilise worldgen self-checks. Recommend leaving them curated unless a specific decoration
  use is wanted.
- **moss_cover (floor-cover).** Include it in the leaves rule, or leave floor covers as
  solid-only? The user's directive names wall + roof plants; moss_cover is a floor mold, so it is
  arguably out of scope.
- **wall plants' looser "no isSolid" check.** `BlockPlantWall` currently tests only
  `isFaceSturdy`, whereas BE's `BaseWallPlantBlock` tests `isSolid() && isFaceSturdy`. Adopting
  the shared helper aligns BN to the stricter-but-standard BE form; confirm that is desired
  (it should be — `isFaceSturdy` on a non-solid is already essentially impossible).
</content>
