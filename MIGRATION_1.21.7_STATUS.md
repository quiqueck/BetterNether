# BetterNether 1.21.1 → 1.21.7 migration — working status & roadmap

Reference implementation: **BetterEnd `1.21.6` branch** (already at 1.21.7) in
`../BetterEnd`. Local libs BCLib / WorldWeaver / WunderLib are all at 21.7.0.
Build Gradle with **Java 21** (`JAVA_HOME=.../temurin-21.0.7`); the system default
Java 24 will not run Gradle 8.12.

## Baseline
`./gradlew compileJava` (against 21.7.0 libs) → **2690 compile errors**. This is a
full architectural port (block registry → traits, `bclib.complexmaterials` →
`wover-sets-api`, off-BCLib bases) **plus** six Minecraft versions of API churn, not
a version bump. BetterEnd did the same across dozens of commits.

## STATUS: `./gradlew build` SUCCEEDS (0 compile errors, down from 2690). Datagen/server/client validation in progress.

### Deferred `// TODO(1.21.7)` markers left during the port (grep the tree for `TODO(1.21.7)`)
- **Firefly custom RenderType** (`entity/render/RenderPhaseAccessor.java`, `ShaderDebugHelper.java`): the additive-blend
  `RenderType` built from `RenderStateShard` shards was removed in the 1.21.5 render-pipeline overhaul → now falls back to
  stock `RenderType.entityTranslucentEmissive` (glow preserved, additive blend lost). Access-wideners for the removed shard
  classes were dropped. Reimplement via a custom `RenderPipeline` if the exact look matters.
- **Piglin nether-armor pacification** (`mixin/common/piglin/PiglinAiMixin.java` → no-op): reimplement data-side by adding
  Cincinnasite/Nether-Ruby/Flaming-Ruby armor items to the `minecraft:piglin_safe_armor` item tag (datagen).
- **Block render layers** (`client/BetterNetherClient.java`): Fabric `BlockRenderLayerMap` was removed; render layer is now
  driven by the block model's `render_type` (data) / a RENDER_LAYER trait. The code-side registration was removed.
- **EMI integration disabled** (`integrations/emi/*`, entrypoint renamed `emi`→`emi_disabled` in fabric.mod.json), mirroring
  BetterEnd, which also disabled EMI. Re-port when the bclib EMI helper is available.
- **Trimmed mushroom-fir chest** removed (`NetherBlocks.java`, recipe too) — re-add via a wover chest API.
- **Wooden furniture** (taburet/chair/bar-stool) dropped (slots removed upstream); the 3 cincinnasite furniture blocks still
  register but their crafting recipes are gone.
- **RubyFire** enchantment kept as a runtime helper; enchantment itself is data-driven.
- **Model fidelity**: the custom sets/slots (trunk/seed/sapling/roof, `ModelTraitLibrary.externalModel()`) and re-parented
  blocks should be checked against the `src/main/generated` datagen diff + `runClient` for missing/placeholder models.

## Done
- [x] **Stage 1 — build files** (commit `[Build] Update build for 1.21.7`): gradle.properties,
  gradle-wrapper 8.12, fabric.mod.json depends/breaks, Java 21, transitive AW for local BCLib,
  `.gitignore` `.DS_Store`.
- [x] **FabricBlockSettings removal** (commit `Replace removed FabricBlockSettings...`): 21 files,
  `FabricBlockSettings.copyOf(x)` → `BlockBehaviour.Properties.ofFullCopy(x)`. Error count 2690 → 2618.

## KEY SCOPE CORRECTION (differs from the generic playbook)
The playbook assumes the full §D block-registry rewrite (every block → `defineBlock(...).addTrait(...)`)
is mandatory. **For BetterNether it is NOT**, because in 21.7.0:
- wover `BlockRegistry` still has `register(name, prebuiltBlock, tags...)` and `registerBlockOnly(...)` —
  the **old `registerBlock("x", new Foo())` pattern still compiles**. NetherBlocks' `registerBlock` helper
  and the no-arg block constructors can stay.
- BCLib `behaviours` package **survives** (`BehaviourBuilders.createWood/createMetal/createGrass/...`,
  `BehaviourMetal/Stone/Wood` markers), so `blocks/materials/Materials.java` and `BlockBase(.Stone/Metal/Wood)`
  need almost no change.
- Only Fabric's `FabricBlockSettings` was removed (done above).

So the genuinely-forced rewrites are narrower: **(a) complexmaterials → wover-sets-api**, **(b) MC 1.21.1→1.21.7
API churn** (recipes/models/block-entities/entities/renderers/render-pipeline), **(c) a few moved bclib
packages** (`items.tool`, `items.boat.BoatTypeOverride`, `integration.emi`, `complexmaterials.*`).
Full trait migration (§D/§G) is OPTIONAL polish, not required to build/run.

**NOTE — items are NOT a cheap relocation:** `bclib.items.tool.Base{Axe,Pickaxe,Shovel,Hoe,Sword,Shears}Item`
were removed in the 1.21.x item-component/`ToolMaterial` overhaul. Tools are now built from vanilla
`net.minecraft.world.item.ToolMaterial` (a record) — see BetterEnd `item/material/EndToolMaterial.java` and
`EndArmorMaterial.java`. `NetherPickaxe/Axe/Shovel/Hoe/Sword/Shears`, `NetherArmor`, and item registration
(`NetherItems`) need real rework (judgment-heavy), not an import swap.

## In flight
- Recipe providers `bootstrap(HolderLookup.Provider, RecipeOutput)` → `bootstrap(RecipeBuilder.Context)`:
  dispatched to a subagent (WoverRecipeProvider subclasses under `datagen/.../recipes/`). `.build(ctx)` unchanged;
  `provider` → `ctx.lookupProvider()`. Loot providers (`WoverLootTableProvider`) are a separate change — TODO.

## Complex-material lifecycle (for the rewrite)
- `WoodenComplexMaterial` → `WoodenBlockSet<S>`; `StoneMaterial`/`MetalMaterial` in BetterEnd are the stone/metal analogs.
- Old `new WillowMaterial().init()` → `new WillowMaterial().buildAndRegister()` (returns the concrete set; keep an
  `init()` wrapper if callers rely on it). `getBlock(WoodSlots.X)` → `getBlock(SlotType.X)`.
- Non-flammable Nether wood: override `addCommonBlockDefinitions(slot, def)` to add `WOOD_BLOCK.withDefault()` +
  mapColor but NOT `BlockTraits.FLAMMABLE` (base class adds FLAMMABLE).
- Custom slots to rewrite as `SlotFromDefinition`/`WoodenSlotFromDefinition`: `Stem`, `Roof`/`RoofStairs`/`RoofSlab`,
  `TrunkSlot`, `AbstractSeed`. Trivial `NetherLog/Bark/StrippedLog/StrippedBark` collapse into `WoodSlots.*`.
  Slot block ctors must become `Foo(BlockBehaviour.Properties)` (slots use `defineDefaultBlockWithProps(name, Foo::new)`).
- `DebugDataItem.forLootTable`, `bclib.items.boat.BoatTypeOverride` (NetherReedMaterial.supplyBoatType) — find wover equivalents.

### Custom-slot template (EXACT — copy this shape)
BetterEnd `complexmaterials/types/Pedestal.java` is the canonical custom-slot example:
```java
public class Stem extends WoodenSlotFromDefinition {          // or SlotFromDefinition for non-wood
    public static final Stem SLOT = new Stem();
    private Stem() { super(NetherSlots.STEM); }               // NetherSlots.STEM is a `new SlotType("stem")`
    @Override protected BlockDefinition<?,?> startBlockDefinition(BlockRegistry r, BlockSet<?> set, String name) {
        return r.defineDefaultBlock(name, def -> new BlockStem(def.getProperties()));   // block ctor takes Properties!
    }
    @Override protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?,?> def) {
        def.addTags(BlockTags.MINEABLE_WITH_AXE);
    }
    @Override protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup l) {
        return BlockTraits.RECIPE.with((key, block, ctx) -> RecipeBuilder.crafting(key.location(), block)
            .shape("##","##").addMaterial('#', set.recipeMaterial(SlotType.LOG)).group("planks").build(ctx));
    }
    @Override protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup l) { /* ModelTraitLibrary.* */ }
}
```
- `SlotType` is a `public record SlotType(String suffix)` → define custom types as
  `public static final SlotType STEM = new SlotType("stem");` (put them in `NetherSlots`).
- Block-only slot (Willow branch, Stalagnate bowl): return `null` from `startBlockDefinition` is "skip";
  for a real block with no item, add `.withBlockItem((d,b)->null)` on the definition.
- `set.recipeMaterial(SlotType)` / `set.recipeMaterialWithFallback(SlotType...)` for recipe ingredients that
  resolve after build. `set.getBlock(SlotType)` for direct refs.

### SEQUENCING CONSTRAINT — the wooden family is all-or-nothing
`NetherWoodenMaterial` and all subclasses override `createMaterialSlots()` (old bclib API). The new base
(`WoodenBlockSet`) has no such method — it uses `createDefaultDefinitions()`. So you CANNOT migrate the base
without simultaneously migrating: `RoofMaterial`, `VanillaFallback`, `VanillaWood`, `VanillaNetherWood`, and the
9 subclasses (Rubeus, Willow, Stalagnate, NetherReed, Wart, NetherMushroom, NetherSakura, AnchorTree,
MushroomFir), the 6 custom slots (Stem/Roof/RoofStairs/RoofSlab/TrunkSlot/AbstractSeed; delete trivial
NetherLog/Bark/StrippedLog/StrippedBark → use `WoodSlots.*`), the ~20 referenced block-class ctors
(BlockStem, BlockRubeusCone, RubeusLog/Bark, BlockWillowTrunk/Branch/Torch/Sapling, BlockStalagnate(+Seed/Bowl),
BlockMushroomFir(+Sapling), BlockAnchorTreeSapling, BlockNetherSakuraSapling, BlockWartSeed/Roots, BlockReedsBlock…)
→ each becomes `Foo(BlockBehaviour.Properties)`, and the `NetherBlocks` call sites (`new X().init()`→`.buildAndRegister()`,
`getBlock(WoodSlots.X)`→`getBlock(SlotType.X)`). Do it as ONE focused push, then recompile.
- **Furniture is dropped** (Taburet/Chair/BarStool slots gone) — `VanillaWood` becomes empty/removable;
  `VanillaNetherWood` keeps only its non-furniture slots.

### complexmaterials rewrite — progress & concrete findings (this push)
FOUNDATION WRITTEN (uncommitted WIP in tree, references the not-yet-written slot API/block ctors → won't compile yet):
- `slots/NetherSlots.java` — now holds custom `SlotType` constants (STEM/TRUNK/ROOF/ROOF_STAIRS/ROOF_SLAB/CONE/BOWL/ROOTS/TORCH/BRANCH/SEED/SAPLING).
- `NetherWoodenMaterial.java` — extends `WoodenBlockSet`; `init()` → `buildAndRegister()`; no-op `setFurnitureCloth`;
  `addCommonBlockDefinitions` adds `WOOD_BLOCK.withDefault()`+mapColor but NOT FLAMMABLE; `createDefaultDefinitions` = super + `WoodSlots.WALL`; get{Planks,Slab,Log,Bark,StrippedLog,StrippedBark} via `SlotType`.
- `RoofMaterial.java` — adds `Roof.SLOT`/`RoofStairs.SLOT`/`RoofSlab.SLOT`.
- `slots/VanillaFallback.java` — `getBlock(SlotType)` falls back to `BuiltInRegistries.BLOCK.getValue(minecraft:<base>_<suffix>)`.

Confirmed API facts:
- `SlotMap` has `.add(SlotFactory)`, `.replace(SlotFactory)`, `.remove(SlotFactory|SlotType)`, `.of(...)` — subclass ports stay close to old shape.
- Registry lookup by id: `Registry.getValue(ResourceLocation)` returns the value (old `.get(id)` now returns `Optional<Holder.Reference>`).
- Local bases `BlockBaseNotFull(Properties)`, `BNPillar(Properties)`/`.Wood(Properties)`, `BlockBase(Properties)` EXIST → add an **additive** `Foo(BlockBehaviour.Properties)` ctor to each slot-instantiated block (keep existing ctors); slot does `defineDefaultBlock(name, def -> new Foo(def.getProperties()))`.
- **`BaseStripableLogBlock`/`BaseStripableBarkBlock` appear REMOVED** from bclib 21.7.0 → `RubeusLog`/`RubeusBark` re-parent to `RotatedPillarBlock` + `BlockTraits.STRIPABLE`/`LOG_BLOCK` (see wover `types/Log.java`).

TODO (next push, one coherent unit):
1. Write custom slots `Stem`, `Roof`, `RoofStairs`, `RoofSlab`, `TrunkSlot`(parameterized `create(...)`), `AbstractSeed`(parameterized `create(...)`), `Sapling`(parameterized `create(...)`); delete trivial `NetherLog/Bark/StrippedLog/StrippedBark`. Port each old `makeRecipe` → `buildRecipe` (BlockTraits.RECIPE.with(...)), each model → ModelTraitLibrary. Faithful recipes are in the old slot/material source (git show HEAD~n).
2. Add additive `Properties` ctors to the ~19 slot-instantiated blocks (list above in this file).
3. Rewrite 9 subclass materials (`RubeusMaterial`, `WillowMaterial`, `StalagnateMaterial`, `NetherReedMaterial`, `WartMaterial`, `NetherMushroomMaterial`, `NetherSakuraMaterial`, `AnchorTreeMaterial`, `MushroomFirMaterial`) + `VanillaWood`(furniture gone → likely delete/empty) + `VanillaNetherWood` + `ColoredGlassMaterial`: override `createDefaultDefinitions()` (start `super`, `.add/.remove/.replace`), swap `WoodSlots.SAPLING`→`Sapling.create(...)`/`NetherSlots.SAPLING`, `getBlock(WoodSlots.X)`→`getBlock(SlotType.X)`, `.remove(WoodSlots.LOG)`→`.remove(SlotType.LOG)`. `NetherReedMaterial.supplyBoatType` used `bclib.items.boat.BoatTypeOverride` (moved — find wover equivalent or drop).
4. Update `NetherBlocks` material call sites + external callers of `MAT_*.getSapling()/getStem()/getTrunk()/...`.
5. Recompile; expect the ~700 `blocks/complex` errors to clear.

### Remaining block-entity bits (post the ValueInput/Output commit)
`BlockEntityForge`: `AbstractFurnaceBlockEntity.getBurnDuration` signature changed (now needs `FuelValues`);
`BNBrewingStandBlockEntity:215-216` cannot-find-symbol — fix with the entity/BE-API pass.

## Error distribution (by area, from `compileJava`)
| Area | ~errors | Nature |
|---|---|---|
| `blocks/complex` (wood/stone material system) | 720 | `bclib.complexmaterials` → `wover-sets-api` re-architecture |
| `datagen/.../recipes` | 350 | `RecipeOutput` → `RecipeProvider.Context` recipe datagen overhaul |
| `entity/render` + `entity/model` | 256 | EntityRenderer **render-state** system (1.21.2+), model generics |
| `entity/EntityNagaProjectile` | 132 | entity API churn |
| `blocks/BlockNetherGrass` | 98 | plant base + traits |
| `client/block` (`BNModels`) | 86 | `net.minecraft.data.models` → `net.minecraft.client.data.models` |
| `world/structures` | 62 | worldgen refs to re-parented bases |
| `registry/NetherBlocks` | 44 | registration DSL |
| block entities | ~40 | NBT `save/loadAdditional` → `ValueOutput`/`ValueInput` |
| items (materials/tools/armor) | ~90 | tool/armor tier + component API |
| mixins (client/common) | ~54 | render pipeline, final methods |

## Concrete API mappings discovered (old → new)

### Complex materials (`bclib.complexmaterials.*` → `org.betterx.wover.sets.api.blocks.*`)
- `WoodenComplexMaterial` → `WoodenBlockSet<S>`; ctor `(ModCore, name, woodColor)` +
  `setPlanksColor(planksColor)`. **Furniture (Taburet/Chair/BarStool) is dropped** — BetterEnd
  removed it (`WoodSlots` has no furniture slots). `GATE` → `FENCE_GATE`.
- Slots are now declared by overriding `SlotMap createDefaultDefinitions()` (base returns the
  standard 23-slot set, minus WALL). Add WALL where needed. `SlotType.*` are the keys; `WoodSlots.*`
  are ready `SlotFromDefinition` factories.
- Custom slot = subclass `SlotFromDefinition` / `WoodenSlotFromDefinition`:
  - `startBlockDefinition(registry, set, name)` → pick Block subclass (`registry.defineDefaultBlockWithProps(name, Foo::new)`); return `null` to skip.
  - `addSlotSpecificDefinitions` / `addWoodSlotSpecificDefinitions(set, def)` → traits/tags.
  - `buildModel(...)` (client-only) → `ModelTraitLibrary.*`.
  - `buildRecipe(...)` → `BlockTraits.RECIPE.with((key, block, ctx) -> RecipeBuilder.crafting(...).build(ctx))` or `RecipeTraitLibrary.*`.
  - block-only slot → `withBlockItem((def,block)->null)` on the definition (see `defineBlockOnly`).
- Old `SlotMap.of(...).add(...).replace(...).remove(...)` semantics still exist on the new `SlotMap`.
- Old per-slot `createBlock(mat, props)` + `makeRecipe(RecipeOutput,...)` → new `startBlockDefinition` + `buildRecipe`.
- `parentMaterial.getBlock(WoodSlots.X)` → `set.getBlock(SlotType.X)`.
- BetterNether custom slots to port: `Stem`, `Roof`/`RoofStairs`/`RoofSlab`, `TrunkSlot`, `AbstractSeed`.
  Trivial ones (`NetherLog/NetherBark/NetherStrippedLog/NetherStrippedBark`) collapse into `WoodSlots.*`;
  "nether = non-flammable" moves to a `NetherWoodenMaterial.addCommonBlockDefinitions` override that skips `BlockTraits.FLAMMABLE`.
  Reference materials in BetterEnd: `EndWoodenComplexMaterial`, `JellyLucerniaWoodMaterial`, `StoneMaterial`, `MetalMaterial`, `VanillaStoneSet`, `VanillaMetalSet`.

### Block registry & bases (§D/§G of playbook)
- `registerBlock("x", new Foo())` (no-arg ctor building `FabricBlockSettings`) →
  `defineBlock("x", Foo::new).<props>.addTrait(...).buildAndRegister()`; every Block ctor takes
  `BlockBehaviour.Properties`. `defineBlockOnly` for item-less blocks.
- `FabricBlockSettings` (`net.fabricmc.fabric.api.object.builder.v1.block`) is **gone** → props on the
  definition/trait.
- Remaining BCLib bases to re-parent onto vanilla + traits: `BaseBlock`, `BaseLeavesBlock`
  (→ `TintedParticleLeavesBlock`, `super(0.01F, props)`), `BasePlantBlock`, `BaseBlockNetherGrass`(×5),
  `BaseBlockMold`(×3), `BaseBlockCommonSapling`(×2), `BaseStripableLog/BarkBlock`, `BaseDoorBlock`,
  `BaseGlassBlock`. **Keep on BCLib**: `BaseVineBlock`, `BaseSimpleVineBlock`, `BaseOreBlock`,
  `StalactiteBlock`, `AnvilBlock`. Non-`Block` subclasses need `simpleCodec` + `codec()`.

### Minecraft 1.21.1 → 1.21.7
- **Recipes datagen**: `RecipeProvider` now takes a `HolderLookup.Provider` + `RecipeOutput`
  wrapped as `RecipeProvider.Context`; every `RecipeOutput`-typed method that BN passes around →
  `Context`. (332 "RecipeOutput cannot be converted to Context".)
- **Datagen model packages moved**: `net.minecraft.data.models.model` / `.blockstates` / `data.models`
  → `net.minecraft.client.data.models.*` (BlockModelGenerators, TextureMapping, TexturedModel,
  ModelTemplates, MultiVariantGenerator/`VariantProperties`, etc.). Big rewrite in `client/block/BNModels`
  + `datagen/NetherModelProvider`. Prefer WoVer `ModelTraitLibrary` where possible.
- **Block-entity NBT**: `saveAdditional(CompoundTag, HolderLookup.Provider)` /
  `loadAdditional(...)` → `saveAdditional(ValueOutput)` / `loadAdditional(ValueInput)`;
  `ContainerHelper.loadAllItems`/`saveAllItems` now take `ValueInput`/`ValueOutput`.
  Files: `BNBrewingStandBlockEntity`, chest/barrel BEs, `BlockEntitiesRegistry`.
- **Entities**: `LivingEntity.getScale()` is **final** (remove overrides; use attribute/`getAgeScale`);
  `Entity.hurt(DamageSource,float)` is **final** → override `hurtServer(ServerLevel, DamageSource, float)`.
  `EntityHydrogenJellyfish`, `EntityFlyingPig`, `EntityNagaProjectile`, `EntityFirefly`.
- **Entity renderers/models**: render-state system — `EntityRenderer<T>` →
  `EntityRenderer<T, S extends EntityRenderState>`; `render(...)` signature change; `EntityModel<T>`
  → `EntityModel<S>`; `getScale`/`setupAnim` via render state. Affects `entity/render/*`,
  `entity/model/*`, `RenderJungleSkeleton`, `RenderFirefly`, mixins `RenderPhaseAccessor`.
- **Render pipeline**: `GlStateManager`/`Program`/`RenderSystem`/`ShaderDebugHelper` — 1.21.5+ render
  overhaul; `RenderType`/`RenderPipeline`. `ShaderDebugHelper`, EMI integration render bits.
- `MapColor`/`MaterialColor` already migrated in BN; `defaultMaterialColor`→`defaultMapColor` if any remain.
- `bclib.items.tool` / `bclib.items.boat.BoatTypeOverride` / `bclib.integration.emi` moved — check wover equivalents.

## Recommended execution order (bottom-up, mirrors BetterEnd commit sequence)
1. Individual **Block classes** → vanilla + `Properties` ctor (+ codec where needed). Family by family.
2. **Complex material** system → `wover-sets-api` (depends on 1).
3. **NetherBlocks / NetherItems** registration DSL → `defineBlock/defineItem ... addTrait ... buildAndRegister`.
4. **Block entities** → ValueInput/Output.
5. **Entities** (final methods) then **renderers/models** (render-state).
6. **Recipes** datagen → Context.
7. **Client models** (`BNModels`) + **NetherModelProvider** → `client.data.models` / `ModelTraitLibrary`.
8. **Structures/features** refs, **mixins**, **integrations** (EMI/wthit).
9. Datagen entrypoint providers; `./gradlew :runDatagenClient`; drive datagen diff to zero unintended drops.
10. `runServer` (catches client-lambda-on-server, §B-1), then `runClient`.

## Verify commands
```
export JAVA_HOME=/Users/quiqueck/Library/Java/JavaVirtualMachines/temurin-21.0.7/Contents/Home
./gradlew compileJava --console=plain -Duser.language=en -Duser.country=US   # error count
./gradlew build
./gradlew :runDatagenClient      # NOT runDatagen (§B-3); diff src/main/generated
./gradlew runServer              # dedicated server (only thing that catches §B-1)
./gradlew runClient              # catches resource/model errors; run in background and KILL after it loads
```
For `runClient`: launch in background, let it reach the title screen / load resources, scan logs for
missing-model/texture/resource errors, then kill it (it won't exit on its own).

## Open tasks

Kept in sync with the working task list. Each entry carries the traps that make it executable later —
do not drop them.

### #22 — Restore the 60 per-wood furniture blocks
Lost in the complexmaterials -> wover-sets rewrite: pre-migration `NetherWoodenMaterial` listed
`WoodSlots.TABURET/CHAIR/BAR_STOOL`; wover's `WoodSlots` has no furniture slots and wover has no furniture
code at all. **It is real content, not dead code** — BetterEnd's history has
`10afe277d [Feature] **New** Wooden Furniture (quiqueck/BetterNether#134)`.
- Assets still ship: 18 bclib `CustomModelData` materialmaps (`materialmaps/block/<wood>_chair.json`,
  `{"defaultMaterial": "betternether:noshade"}`) + `textures/block/stalagnate_chair.png`.
- **No art is missing**: models are generated from each wood's planks texture by
  `BCLModels.create{Chair,Taburet,BarStool}BlockModel` (BCLModels.java:126/146/163).
- Still present: bclib `BaseTaburet`/`BaseChair`/`BaseBarStool`; BN's `registerTaburet`/`registerChair`/
  `registerBarStool` (in use for the 3 cincinnasite pieces, with recipes + fuel).
- Scope: 20 woods x 3 = 60, named `<wood>_taburet|_chair|_bar_stool` (suffix form; the standalone
  cincinnasite ones use the prefix form). Woods at `158e63d4`: acacia anchor_tree bamboo birch cherry
  crimson dark_oak jungle mangrove mushroom_fir nether_mushroom nether_reed nether_sakura oak rubeus
  spruce stalagnate warped wart willow — BN's own **and** vanilla (via `VanillaWood`).
- Slot classes go in **BCLib** (shared with #23), shaped like BN's `Sapling`/`TrunkSlot`.

### #23 — BetterEnd: restore the wooden furniture (after #22)
26 orphaned assets, zero registrations. Reuse #22's BCLib slot classes. Work out the real wood list from
its pre-migration blockstates — the orphan list is incomplete and is not the scope.

### #17 — `ModelOverides` -> traits, and `IRenderTypeable` -> `RENDER_LAYER`
37 provider entries + ~110 `addMaterialOverrides`; 43 render-layer blocks. Plumbing is ready
(`register*`/`Sapling`/`AbstractSeed` take `List<BlockTrait<?, ?>>`).

### #21 — Remove `Behaviour*` / `BehaviourBuilders`, and the deprecated runtime interfaces
`BlockLootProvider` (9 files), `BlockModelProvider` (8) + `RuntimeBlockModelProvider`,
`CustomBlockItemProvider`. Overlaps #17 on `BlockModelProvider` — cannot run in parallel with it.

### #15 — Remaining broken assets
4 log/stem particle models (wover's `particleOnlyModel` guesses `<name>_side` only when the name ends in
`_log`; BN ships `_side_1..3`, or nothing at all for `nether_reed_stem`/`rubeus_stripped_log`), 3 `*_trunk`
item models, 6 **dev-only** debug items (behind `BCLib.isDevEnvironment()`).

### #20 — wover: let the definition own the smithing-template id
Cosmetic only; the correctness hazard is closed by WorldWeaver `a45614b`. Does **not** collapse
`templatePath` — `build()` still needs the bare path for the four description keys.
`ItemRegistry.java:536`'s comment stays accurate for the deprecated path — do not delete it as collateral.

## Traps that keep biting (read before touching models/registration)

1. **Lambdas strip badly.** javac does NOT copy `@Environment(CLIENT)` onto the synthetic method it
   generates for a lambda body, so Fabric's stripper removes the enclosing method on a dedicated server and
   leaves the lambda behind, still referencing client-only datagen types. If the class is loaded on the
   server, *verification alone* crashes startup — the code never runs. Put such lambdas in a nested
   `@Environment(CLIENT)` class: BetterEnd `StoneLanternBlock`, `FlowerPotBlock` (fafba62ba).
   `ModelTraitLibrary` calls are safe unguarded (they return null outside datagen).
2. **ModelOverides gates the traits.** `BlockModelTrait.bootstrapModels`' filter is
   `!overrides.contain(block)`, so an entry must be REMOVED from `ModelOverides` as its trait is added, or
   the trait is skipped and the block silently loses its model.
3. **Never derive per-class facts with a whole-file grep.** Several files hold multiple classes; a
   file-level scan for `setRenderLayer(BNRenderLayer.X)` wrongly attributed CUTOUT to `BNNetherBrick` and
   `BlockMossCover`. Wrong render layers compile and pass build/datagen/server.
4. **The dangerous bugs pass every gate.** The 235 missing blockstates, the 43 solid-rendering blocks and a
   plant with no survival trait all build, datagen and boot cleanly. Diff generated output against
   `158e63d4` and check coverage counts explicitly.
