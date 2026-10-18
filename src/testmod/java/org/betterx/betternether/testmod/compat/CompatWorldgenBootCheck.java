package org.betterx.betternether.testmod.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * <b>1.20.1 port</b> of WorldWeaver's {@code wover-test-api} {@code CompatWorldgenBootCheck} (1.21.6+), kept
 * as a local testmod copy because there is no WorldWeaver on this branch. Same system properties, same
 * result-file format, same checks - only the API names differ, and {@value #DEFAULT_GENERATOR_PROPERTY}
 * checks for BCLib's {@code BCLChunkGenerator} (what BCLib's {@code DedicatedServerPropertiesMixin} puts
 * the Nether and End on here) instead of WoVer's generator. The rest of this doc is the original's and
 * still says "WoVer" where 1.20.1 means BCLib.
 * <p>
 * The cross-mod compatibility framework's "does the other mod's worldgen actually reach the shared
 * BiomeSource, in the exact dimension(s) it's supposed to" check.
 * <p>
 * Deliberately <b>not</b> a {@code @GameTest} (see the superseded {@code CompatWorldgenGameTest}, kept
 * only in git history): vanilla's own {@code GameTestServer} hardcodes
 * {@code WorldPresets.FLAT.createWorldDimensions()} for every dimension it creates - confirmed by
 * decompiling {@code GameTestServer}'s bytecode, not inferred - so the overworld a GameTest run sees is
 * unconditionally the flat/void Superflat preset, never the mod's real registered generator. Noise-based
 * injections (TerraBlender regions, Terralith's own biome source, etc) can never be observed there
 * regardless of whether they actually work. Nether/end aren't affected (the flat preset only overrides
 * the overworld dimension), which is why the old GameTest-based check could see BiomesOPlenty in
 * nether/end but never in overworld - a real environment quirk, not a mod bug.
 * <p>
 * This class instead boots a <i>plain</i> dedicated server (the project's own {@code testmodServer} run,
 * not a GameTest run) with the real, normally-registered generator for every dimension, waits for
 * {@link ServerLifecycleEvents#SERVER_STARTED} (registries are fully loaded and every dimension's
 * {@link BiomeSource} is live well before any chunk actually needs to generate - no world save or actual
 * terrain generation is required for this check), inspects every level's BiomeSource the same way the
 * old GameTest check did, writes the result to a file, and immediately halts the server. The check runs
 * inside a try/finally around the halt call specifically so a bug in the check itself can never leave the
 * server running forever waiting for a client that will never connect - the node harness's own wall-clock
 * timeout is a second, independent backstop on top of that, not a replacement for it.
 * <p>
 * <b>Per-namespace, per-dimension expectations, not "present somewhere".</b> A mod like BiomesOPlenty
 * adds content to overworld, nether, <i>and</i> end - a flat "found this namespace in any dimension"
 * check would silently pass even if BiomesOPlenty's nether integration broke, as long as its overworld
 * biomes still showed up. {@value #NAMESPACES_PROPERTY} is instead
 * {@code namespace1:dim1+dim2;namespace2:dim3}, e.g.
 * {@code biomesoplenty:overworld+nether+end;terralith:overworld;incendium:nether;nullscape:end} - each
 * namespace is checked against exactly the dimensions listed for it, no more and no less. Recognized
 * dimension aliases: {@code overworld}, {@code nether}, {@code end} (see {@link #DIMENSION_ALIASES}).
 * <p>
 * <b>{@value #DEFAULT_GENERATOR_PROPERTY} - does WoVer's own default generator survive other mods being
 * present.</b> Boolean flag, independent of the namespace checks above. WoVer's own dedicated-server
 * default-preset injection ({@code DedicatedServerPropertiesMixin}) is what makes a fresh install boot on
 * a WoVer preset at all - a third-party mod that also patches {@code DedicatedServerProperties} or the
 * world-preset registry could plausibly interfere with that. When set, asserts both {@code the_nether}
 * and {@code the_end} are on WoVer's own generator (class name compared by string - see
 * {@value #BCL_CHUNK_GENERATOR_CLASS} - rather than a hard compile dependency on wover-generator-api's
 * {@code WoverChunkGenerator}, since this module is shared by every compat-test participant, not just
 * wover-generator-api itself). Failures are reported as {@code generator@the_nether}/{@code generator@the_end}
 * in the same {@code missing} list the namespace checks use.
 * <p>
 * <b>{@value #SURFACE_RULE_PROPERTY} - does our own surface rule still fire with other mods'
 * worldgen mixed in.</b> {@code biomeId1=blockId1;biomeId2=blockId2}, e.g.
 * {@code betternether:gravel_desert=minecraft:gravel;betterend:foggy_mushroomland=betterend:end_moss}.
 * Unlike {@code SurfaceRuleAppliesBootCheck} (wover-surface-api's single-mod version, which ships a
 * purpose-built {@code minecraft:fixed} biome-source dimension so the target biome is guaranteed at a
 * known position), a real mod's biome is placed by its own real generator - there's no fixed position to
 * check deterministically. So for each pair, this searches the biome's own dimension for a real position
 * via {@link #findBiome}, forces that chunk to {@link ChunkStatus#FULL}, and scans it for the expected
 * block via {@link ChunkAccess#findBlocks}. Failures are reported as {@code surfacerule@<biomeId>} in the
 * same {@code missing} list. A biome the search can't find within {@value #SURFACE_RULE_SEARCH_RADIUS}
 * blocks of the origin is also a failure (rather than silently skipped), since a real regression could
 * easily make a biome unreachable at all. See {@link #findBiome} for how the search itself works - it does
 * its own direct real-terrain search rather than delegating to vanilla's {@link BiomeSource#findClosestBiome3d}
 * (which this class used until it was found to be unreliable against WoVer's own {@code WoverBiomeSource}
 * implementations).
 * <p>
 * <b>Feature order - no property, always checked.</b> Every dimension's per-step feature order is built
 * with vanilla's own sorter and reported as {@code featureorder@<dimension>}. It needs no expectation to
 * compare against and generates no chunks, so every configured set gets it for free. See
 * {@link #featureOrderFailure} for why this failure mode deserves its own check rather than being left to
 * turn up as a crash.
 * <p>
 * <b>No system property set = not a compat run at all.</b> Registers the SERVER_STARTED listener
 * unconditionally (this class is on every testmod's normal {@code main} entrypoint list, so it loads on
 * every ordinary {@code testmodServer}/{@code runServer} run too), but the listener itself is a no-op
 * unless {@value #NAMESPACES_PROPERTY}, {@value #DEFAULT_GENERATOR_PROPERTY}, or
 * {@value #SURFACE_RULE_PROPERTY} is set - only the compat-test harness's dedicated
 * {@code runCompatWorldgenBoot} run passes any of them, so a normal dev run never halts early.
 */
public class CompatWorldgenBootCheck implements ModInitializer {
    private static final String NAMESPACES_PROPERTY = "betterx.compat.expectWorldgen";
    private static final String DEFAULT_GENERATOR_PROPERTY = "betterx.compat.expectDefaultGenerator";
    private static final String SURFACE_RULE_PROPERTY = "betterx.compat.expectSurfaceRule";
    private static final String RESULT_FILE_PROPERTY = "betterx.compat.worldgenResultFile";
    private static final String BCL_CHUNK_GENERATOR_CLASS = "org.betterx.bclib.api.v2.generator.BCLChunkGenerator";
    private static final int SURFACE_RULE_SEARCH_RADIUS = 4000;

    private static final Map<String, String> DIMENSION_ALIASES = Map.of(
            "overworld", "minecraft:overworld",
            "nether", "minecraft:the_nether",
            "end", "minecraft:the_end"
    );

    @Override
    public void onInitialize() {
        final Map<String, Set<String>> expected = expectedNamespaceDimensions();
        final boolean expectDefaultGenerator = Boolean.getBoolean(DEFAULT_GENERATOR_PROPERTY);
        final Map<String, String> expectedSurfaceRules = expectedSurfaceRules();
        if (expected.isEmpty() && !expectDefaultGenerator && expectedSurfaceRules.isEmpty()) {
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                runCheck(server, expected, expectDefaultGenerator, expectedSurfaceRules);
            } catch (Throwable t) {
                // Never leave the harness without a result file. Some of these checks force real chunk
                // generation, and a broken enough world takes the server down mid-check - at which point
                // the harness can only fall back to the exit code and report "the boot produced no
                // result", burying the actual reason. Writing a FAIL that names the throwable keeps the
                // diagnosis in the report where it belongs. Observed with a feature-order cycle in the
                // End: the surface-rule check's forced chunk was the thing that died.
                writeCrashResult(t);
            } finally {
                server.halt(false);
            }
        });
    }

    /** Parses {@code biomeId1=blockId1;biomeId2=blockId2} into {biomeId -> expected block id}. */
    private static Map<String, String> expectedSurfaceRules() {
        final String raw = System.getProperty(SURFACE_RULE_PROPERTY);
        if (raw == null || raw.isBlank()) return Map.of();

        final Map<String, String> expected = new LinkedHashMap<>();
        for (String entry : raw.split(";")) {
            final String trimmed = entry.trim();
            if (trimmed.isEmpty()) continue;
            final String[] parts = trimmed.split("=", 2);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) continue;
            expected.put(parts[0].trim(), parts[1].trim());
        }
        return expected;
    }

    /** Parses {@code namespace1:dim1+dim2;namespace2:dim3} into {namespace -> {full dimension ids}}. */
    private static Map<String, Set<String>> expectedNamespaceDimensions() {
        final String raw = System.getProperty(NAMESPACES_PROPERTY);
        if (raw == null || raw.isBlank()) return Map.of();

        final Map<String, Set<String>> expected = new LinkedHashMap<>();
        for (String entry : raw.split(";")) {
            final String trimmed = entry.trim();
            if (trimmed.isEmpty()) continue;
            final String[] parts = trimmed.split(":", 2);
            final String namespace = parts[0].trim();
            final Set<String> dimensions = new LinkedHashSet<>();
            if (parts.length > 1) {
                for (String alias : parts[1].split("\\+")) {
                    final String trimmedAlias = alias.trim();
                    if (trimmedAlias.isEmpty()) continue;
                    dimensions.add(DIMENSION_ALIASES.getOrDefault(trimmedAlias, trimmedAlias));
                }
            }
            if (!namespace.isEmpty() && !dimensions.isEmpty()) {
                expected.put(namespace, dimensions);
            }
        }
        return expected;
    }

    private static void runCheck(
            MinecraftServer server, Map<String, Set<String>> expected, boolean expectDefaultGenerator,
            Map<String, String> expectedSurfaceRules
    ) {
        final Map<String, Set<String>> presentByDimension = new LinkedHashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            presentByDimension.put(
                    level.dimension().location().toString(),
                    namespacesIn(level.getChunkSource().getGenerator().getBiomeSource())
            );
        }

        final List<String> failures = new ArrayList<>();
        // Every check actually attempted, PASS or FAIL - "missing" only tells you what went wrong, this
        // is what lets a caller show "here is everything that was really tested" regardless of outcome.
        final List<String> checks = new ArrayList<>();
        for (var entry : expected.entrySet()) {
            final String namespace = entry.getKey();
            for (String dimension : entry.getValue()) {
                final String key = namespace + "@" + dimension;
                final Set<String> present = presentByDimension.get(dimension);
                final boolean ok = present != null && present.contains(namespace);
                checks.add(key + ": " + (ok ? "PASS" : "FAIL"));
                if (!ok) failures.add(key);
            }
        }

        if (expectDefaultGenerator) {
            for (var entry : Map.of("the_nether", Level.NETHER, "the_end", Level.END).entrySet()) {
                final ServerLevel level = server.getLevel(entry.getValue());
                final ChunkGenerator generator = level == null ? null : level.getChunkSource().getGenerator();
                final String key = "generator@" + entry.getKey();
                final boolean ok = generator != null && generator.getClass().getName().equals(BCL_CHUNK_GENERATOR_CLASS);
                checks.add(key + ": " + (ok ? "PASS" : "FAIL"));
                if (!ok) failures.add(key);
            }
        }

        // Unconditional, unlike every other check here: it needs no expectation to compare against (the
        // failure is self-describing), it costs one sort per dimension with no chunk generation at all, and
        // the thing it catches is a hard crash rather than a degradation. Every configured set gets it.
        //
        // Runs before the surface-rule check on purpose, and hands it the dimensions it found broken: a
        // dimension with a feature-order cycle cannot generate a single chunk, so asking the surface-rule
        // check to force one there does not test the surface rule, it just kills the server before any of
        // this gets written out.
        final Set<String> brokenDimensions = new LinkedHashSet<>();
        for (ServerLevel level : server.getAllLevels()) {
            final String dimension = level.dimension().location().toString();
            final String key = "featureorder@" + dimension;
            final String reason = featureOrderFailure(level);
            checks.add(key + ": " + (reason == null ? "PASS" : "FAIL (" + reason + ")"));
            if (reason != null) {
                failures.add(key);
                brokenDimensions.add(dimension);
            }
        }

        for (var entry : expectedSurfaceRules.entrySet()) {
            final String key = "surfacerule@" + entry.getKey();
            // null == fired; otherwise a short human-readable reason. The reason goes into "checks"
            // only - "missing" stays the bare key, which is what consumers match on.
            final String reason = surfaceRuleFailure(server, entry.getKey(), entry.getValue(), brokenDimensions);
            checks.add(key + ": " + (reason == null ? "PASS" : "FAIL (" + reason + ")"));
            if (reason != null) failures.add(key);
        }

        writeResult(failures.isEmpty(), expected, presentByDimension, failures, checks);
    }

    /**
     * Asks vanilla's own {@link FeatureSorter} to build the dimension's per-step feature order, which is
     * exactly what the first chunk to reach {@link ChunkStatus#FEATURES} would do.
     * <p>
     * Worth its own check because of how late and how total the natural failure is. The order is one global
     * topological sort per {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration} over every
     * biome in the dimension, so two biomes that disagree about the order of the same two features are a
     * cycle - and the sorter answers a cycle with an {@link IllegalStateException}, from a supplier that is
     * only forced when a chunk actually needs to decorate. Nothing complains at boot; the dimension simply
     * crashes the server the moment anything enters it. Two mods that each work alone are enough to cause
     * it, which makes it precisely a compat-test concern (BetterEnd + TechReborn, BetterEnd#596).
     * <p>
     * Deliberately builds its own sort instead of forcing the generator's memoized supplier: that supplier
     * may already have been forced (and its exception swallowed) during boot logging, and a memoized
     * supplier that threw does not re-throw the same way. This calls the sorter directly, on the same
     * inputs, so the answer is about the biome data rather than about who looked at it first.
     *
     * @return {@code null} when the order is consistent, otherwise a short reason for the report
     */
    private static String featureOrderFailure(ServerLevel level) {
        final BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        try {
            FeatureSorter.buildFeaturesPerStep(
                    List.copyOf(biomeSource.possibleBiomes()),
                    holder -> holder.value().getGenerationSettings().features(),
                    true
            );
            return null;
        } catch (IllegalStateException e) {
            // The message already names the biomes involved, which is the whole diagnosis.
            final String message = e.getMessage();
            return message == null ? e.getClass().getSimpleName() : message;
        }
    }

    /** How many distinct candidate positions {@link #findBiomeCandidates} will collect before giving up. */
    private static final int SURFACE_RULE_MAX_CANDIDATES = 24;

    /**
     * Minimum spacing between two candidate positions, in blocks - see {@link #findBiomeCandidates}. Chosen
     * to be far larger than one biome blob so that the candidate list samples the biome's whole occurrence
     * pattern instead of a single patch of it.
     */
    private static final int SURFACE_RULE_CANDIDATE_SPACING = 512;

    /**
     * How many candidate chunks that actually contain terrain have to miss the expected block before the
     * check gives up and reports a failure. Void candidate chunks do not count towards this (see
     * {@link #surfaceRuleFailure}).
     */
    private static final int SURFACE_RULE_TERRAIN_CHUNKS = 6;

    /**
     * Finds real positions where biomeId generates, force-generates candidate chunks in turn, and reports
     * whether any of them actually contains expectedBlockId.
     * <p>
     * <b>Void candidate chunks are skipped, not counted as misses.</b> A biome-noise match is not a promise
     * that terrain exists there: the biome's own noise domain and the terrain density field are separate
     * fields, and a datapack that replaces one without the other decouples them completely. Measured case:
     * with Nullscape loaded, {@code minecraft:worldgen/noise_settings/end.json} is replaced by a version
     * whose {@code noise_router.erosion} is aliased to {@code minecraft:overworld/erosion} and which
     * contains no {@code minecraft:end_islands} term at all - and WoVer's own End biome source classifies
     * the End's land/void/barrens rings from exactly that {@code erosion} sampler (the same way vanilla's
     * {@code TheEndBiomeSource} does). The land ring therefore no longer follows the islands, and roughly a
     * third of the positions where an End land biome is reported turn out to be open void. A chunk with no
     * blocks in it cannot demonstrate <i>any</i> surface rule, ours or vanilla's, so counting it as
     * evidence of a broken rule is simply wrong - it is not a weaker signal, it is no signal.
     * <p>
     * Measured over 27 force-generated candidate chunks across 18 seeds of the four-mod set: 8 were
     * completely empty, and of the 19 that had terrain, 17 contained the expected block - the two that did
     * not held 4 and 54 blocks respectively, i.e. a few stray feature blocks and no surface at all. The
     * surface rule fires wherever there is a surface to fire on; the old version of this method failed
     * whenever its 8 candidates happened to all land in the same void patch, which is a property of the
     * world seed and not of the rule under test.
     *
     * @return {@code null} when the rule fired, otherwise a short reason for the report
     */
    private static String surfaceRuleFailure(
            MinecraftServer server, String biomeId, String expectedBlockId, Set<String> brokenDimensions
    ) {
        final ResourceKey<Biome> biomeKey = ResourceKey.create(
                net.minecraft.core.registries.Registries.BIOME, new ResourceLocation(biomeId)
        );
        final Block expectedBlock = server.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BLOCK)
                .getOptional(new ResourceLocation(expectedBlockId))
                .orElse(Blocks.AIR);

        boolean foundBiomeAnywhere = false;
        boolean skippedBrokenDimension = false;
        int voidChunks = 0;
        int terrainChunks = 0;

        // Every level, not just the first one that has candidates: a biome could legitimately be reachable
        // in more than one dimension, and the scan of the levels that do not have it at all has already
        // been paid for by the time we get here anyway.
        for (ServerLevel level : server.getAllLevels()) {
            // Forcing a chunk in a dimension whose feature order does not sort is not a check, it is a
            // crash - and one that would take the whole result file with it. featureorder@<dimension> has
            // already recorded the real problem.
            if (brokenDimensions.contains(level.dimension().location().toString())) {
                skippedBrokenDimension = true;
                continue;
            }

            final List<BlockPos> candidates = findBiomeCandidates(
                    level, biomeKey, SURFACE_RULE_SEARCH_RADIUS, 32, 8, SURFACE_RULE_MAX_CANDIDATES
            );
            if (candidates.isEmpty()) continue;
            foundBiomeAnywhere = true;

            for (BlockPos pos : candidates) {
                final ChunkAccess chunk = level.getChunkSource().getChunk(
                        pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, true
                );
                if (chunk == null) continue;

                // -1 means every section is air, i.e. this candidate is open void.
                if (chunk.getHighestFilledSectionIndex() < 0) {
                    voidChunks++;
                    continue;
                }
                terrainChunks++;

                final AtomicBoolean hasBlock = new AtomicBoolean(false);
                chunk.findBlocks(state -> state.is(expectedBlock), (blockPos, state) -> hasBlock.set(true));
                if (hasBlock.get()) return null;

                if (terrainChunks >= SURFACE_RULE_TERRAIN_CHUNKS) {
                    return terrainChunks + " chunk(s) with terrain inspected, none contained " + expectedBlockId;
                }
            }
        }

        if (!foundBiomeAnywhere) {
            // Say which of the two it is. "Not found anywhere" reads as a missing biome, but when the
            // dimension it lives in was skipped for a broken feature order, this check simply never got to
            // look - the featureorder@ entry next to it is the real finding.
            return "biome not found within " + SURFACE_RULE_SEARCH_RADIUS + " blocks of the origin in any "
                    + (skippedBrokenDimension
                            ? "dimension that could be searched - dimension(s) with a broken feature order "
                              + "were skipped, see the featureorder@ checks"
                            : "dimension");
        }
        if (terrainChunks == 0) {
            return voidChunks + " candidate chunk(s) inspected, all of them open void - "
                    + "no terrain anywhere this biome generates, so no surface rule can be observed";
        }
        return terrainChunks + " chunk(s) with terrain inspected (" + voidChunks + " void ones skipped), "
                + "none contained " + expectedBlockId;
    }

    /**
     * Finds up to {@code maxCandidates} real block positions where {@code biomeKey} generates in
     * {@code level}, at most one per {@value #SURFACE_RULE_CANDIDATE_SPACING}-block cell, searching outward
     * from {@link BlockPos#ZERO} in expanding horizontal rings ({@code horizontalStep} blocks apart, out to
     * {@code radius}) and, at each ring position, an expanding set of Y offsets ({@code verticalStep} blocks
     * apart, closest-to-zero first, clamped to the level's height range).
     * <p>
     * Does its own direct {@link BiomeSource#getNoiseBiome} search rather than delegating to vanilla's
     * {@link BiomeSource#findClosestBiome3d} (the method backing {@code /locatebiome}, and what this method
     * replaced): that turned out to be unreliable against WoVer's own {@code WoverBiomeSource}
     * implementations specifically. Confirmed by direct reproduction: it reports "not found" for a biome
     * even when {@code getNoiseBiome(0, 0, 0, sampler)} - i.e. the exact block the search starts from -
     * returns that same biome directly. Reproduced independent of which biome was searched for (also
     * checked against a vanilla biome and a different WoVer biome), independent of search radius (checked
     * unreliable up to 150000, 37.5x this class's actual default), and independent of any third-party mod
     * being present (reproduced with the mod that owns the biome running completely alone). Whatever the
     * precise mismatch (vanilla's default {@code findBiomeHorizontal} combines several assumptions -
     * Y-candidate generation via {@code Mth.outFromOrigin} bounded by
     * {@code level.getMinY()+1}/{@code level.getMaxY()+1}, an internal {@code possibleBiomes()} pre-filter,
     * repeated {@code getNoiseBiome} calls against a source that lazily rebuilds/caches its own biome map -
     * any of which could misbehave against a custom, non-multi-noise {@code BiomeSource} like WoVer's), this
     * avoids the vanilla method entirely rather than chasing the exact mismatch.
     * <p>
     * Returns multiple distinct-chunk candidates rather than just the first hit, because a position inside a
     * biome's 3D noise volume is not guaranteed to have any real generated surface there at all - the
     * biome's own noise domain and the terrain density field don't perfectly coincide everywhere, so a
     * single match can legitimately be biome-correct with no surface-rule-triggering transition anywhere in
     * that column (e.g. fully enclosed in another region's solid terrain in the Nether, or landing in the
     * void between islands in the End). An earlier version of this method tried filtering candidates with
     * {@link ChunkGenerator#getBaseHeight} before force-generating anything, expecting to reject exactly
     * these cases cheaply - that backfired specifically in the Nether, since {@code getBaseHeight}'s
     * {@code WORLD_SURFACE_WG} heightmap resolves to the topmost solid block scanning down from the build
     * height limit, which in a cave-riddled Nether is the dimension's physical ceiling, not the biome's
     * floor, and rejected essentially every real match. Trying several distinct real (force-generated)
     * chunks instead - cheap relative to a full compat-test run - turns one unlucky column into a rare
     * double-miss rather than the only chance to pass.
     * <p>
     * <b>De-duplicated per {@value #SURFACE_RULE_CANDIDATE_SPACING}-block cell, not per chunk.</b> Per-chunk
     * de-duplication looked like it was spreading the candidates out and was not: the search steps
     * {@code horizontalStep} (32) blocks at a time, so consecutive matches land in adjacent chunks and the
     * whole candidate list came from one contiguous patch of the biome, a few dozen blocks across. Measured
     * on the failing four-mod End case: all 8 candidates fell inside x 992..1056 / z -608..-512, one blob,
     * and that blob was open void - so the check reported "the surface rule did not fire" on the evidence of
     * a single unlucky spot, and did so as a function of the world seed. Spreading the cells means the list
     * samples the biome's whole occurrence pattern, which is what makes {@link #surfaceRuleFailure}'s
     * skip-the-void-ones loop able to reach real terrain.
     *
     * @param level          the level to search
     * @param biomeKey       the biome to look for
     * @param radius         maximum horizontal search distance from {@link BlockPos#ZERO}, in blocks
     * @param horizontalStep spacing between horizontal (X/Z) search rings, in blocks
     * @param verticalStep   spacing between vertical (Y) search offsets, in blocks
     * @param maxCandidates  stop once this many well-separated candidates have been found
     * @return up to {@code maxCandidates} block positions where {@code biomeKey} generates, nearest first;
     *         empty if none were found within {@code radius}
     */
    private static List<BlockPos> findBiomeCandidates(
            ServerLevel level, ResourceKey<Biome> biomeKey, int radius, int horizontalStep, int verticalStep,
            int maxCandidates
    ) {
        final BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        final Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
        final int minY = level.getMinBuildHeight();
        final int maxY = level.getMaxBuildHeight() - 1;
        final int rings = Math.max(0, radius / horizontalStep);
        final List<BlockPos> candidates = new ArrayList<>();
        final Set<Long> seenCells = new HashSet<>();

        for (BlockPos.MutableBlockPos ring : BlockPos.spiralAround(BlockPos.ZERO, rings, Direction.EAST, Direction.SOUTH)) {
            final int blockX = ring.getX() * horizontalStep;
            final int blockZ = ring.getZ() * horizontalStep;
            final int quartX = QuartPos.fromBlock(blockX);
            final int quartZ = QuartPos.fromBlock(blockZ);

            for (int offset = 0; offset <= (maxY - minY); offset += verticalStep) {
                for (int sign = -1; sign <= 1; sign += 2) {
                    final int blockY = offset * sign;
                    if (blockY < minY || blockY > maxY) continue;

                    final Holder<Biome> sampled = biomeSource.getNoiseBiome(quartX, QuartPos.fromBlock(blockY), quartZ, sampler);
                    final boolean newCell = sampled.is(biomeKey) && seenCells.add(ChunkPos.asLong(
                            Math.floorDiv(blockX, SURFACE_RULE_CANDIDATE_SPACING),
                            Math.floorDiv(blockZ, SURFACE_RULE_CANDIDATE_SPACING)
                    ));
                    if (newCell) {
                        candidates.add(new BlockPos(blockX, blockY, blockZ));
                        if (candidates.size() >= maxCandidates) return candidates;
                    }

                    if (offset == 0) break; // sign -1/+1 are the same position at offset 0
                }
            }
        }
        return candidates;
    }

    /**
     * Last-resort result for a boot check that did not survive to {@link #writeResult}. Records the
     * throwable as the single failing check, so the harness reports what actually happened instead of
     * falling back to "the boot produced no result".
     */
    private static void writeCrashResult(Throwable t) {
        final String reason = t.getClass().getSimpleName()
                + (t.getMessage() == null ? "" : ": " + t.getMessage());
        writeResult(
                false, Map.of(), Map.of(),
                List.of("bootcheck"),
                List.of("bootcheck: FAIL (" + reason + ")")
        );
    }

    private static void writeResult(
            boolean passed, Map<String, Set<String>> expected, Map<String, Set<String>> presentByDimension,
            List<String> failures, List<String> checks
    ) {
        final String resultFile = System.getProperty(RESULT_FILE_PROPERTY);
        if (resultFile == null || resultFile.isBlank()) {
            return;
        }
        final StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"status\": \"").append(passed ? "PASS" : "FAIL").append("\",\n");
        json.append("  \"expected\": {\n");
        appendStringSetMap(json, expected);
        json.append("  },\n");
        json.append("  \"missing\": ").append(jsonArray(failures)).append(",\n");
        json.append("  \"checks\": ").append(jsonArray(checks)).append(",\n");
        json.append("  \"presentByDimension\": {\n");
        appendStringSetMap(json, presentByDimension);
        json.append("  }\n");
        json.append("}\n");

        final Path path = Path.of(resultFile);
        if (path.getParent() != null) {
            path.getParent().toFile().mkdirs();
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException("compat-test: failed to write worldgen boot-check result to " + resultFile, e);
        }
    }

    private static void appendStringSetMap(StringBuilder json, Map<String, Set<String>> map) {
        int i = 0;
        for (var entry : map.entrySet()) {
            json.append("    \"").append(escape(entry.getKey())).append("\": ").append(jsonArray(entry.getValue()));
            if (++i < map.size()) json.append(",");
            json.append("\n");
        }
    }

    private static String jsonArray(Iterable<String> values) {
        final StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String value : values) {
            if (!first) sb.append(", ");
            sb.append("\"").append(escape(value)).append("\"");
            first = false;
        }
        return sb.append("]").toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Every distinct namespace among the possible biomes a BiomeSource can produce. */
    private static Set<String> namespacesIn(BiomeSource source) {
        final Set<String> namespaces = new LinkedHashSet<>();
        for (Holder<Biome> biome : source.possibleBiomes()) {
            biome.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getNamespace).ifPresent(namespaces::add);
        }
        return namespaces;
    }
}
