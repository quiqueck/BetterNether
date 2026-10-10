package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.BetterNether;
import de.ambertation.wover.test.api.gametest.GameTestAssertions;
import de.ambertation.wover.test.api.gametest.StructureTemplateAudit;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * BetterNether's structure templates must place exactly what they were authored to place (checks in
 * {@link StructureTemplateAudit}, shared with BetterEnd).
 * <p>
 * Regression test for templates that were hand-edited and had their {@code DataVersion} bumped without
 * renaming their blocks: the DataFixer then skips them, so {@code minecraft:chain} (pyramid_3/4, renamed to
 * {@code iron_chain} in 1.21.9) and the pre-1.13 {@code minecraft:mob_spawner} / {@code minecraft:nether_brick}
 * (room_01) were placed as air. Block entity data under removed ids ({@code bclib:chest},
 * {@code betternether:chest}, ...) is caught too: placement ignores the id, but the DataFixer picks its fixes
 * by it.
 */
public class StructureTemplateGameTest {
    @GameTest(maxTicks = 2000)
    public void shippedTemplatesPlaceWhatTheyShould(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final List<ResourceLocation> templates = StructureTemplateAudit.shippedTemplates(level, BetterNether.C.namespace);
        final List<String> failures = new ArrayList<>();
        if (templates.size() < 50) {
            failures.add("only found " + templates.size() + " BetterNether structure templates, expected over 100");
        }
        for (ResourceLocation id : templates) {
            final String diff = StructureTemplateAudit.loadsLikeVanilla(level, id);
            if (diff != null) failures.add(diff);
            failures.addAll(StructureTemplateAudit.blocksLoadingAsAir(level, id));
            failures.addAll(StructureTemplateAudit.mismatchedBlockEntityIds(level, id));
        }
        GameTestAssertions.failIfAny(helper, failures.size() + " problem(s) in BetterNether's templates", failures);
        helper.succeed();
    }

    @GameTest(maxTicks = 2000)
    public void shippedTemplatesAreAtCurrentDataVersion(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final List<String> failures = StructureTemplateAudit.outdatedDataVersions(
                level,
                StructureTemplateAudit.shippedTemplates(level, BetterNether.C.namespace)
        );
        if (!failures.isEmpty()) {
            failures.add("upgrade them with WunderVault's structuregen/upgrade_structures.sh --apply");
        }
        GameTestAssertions.failIfAny(helper, "outdated BetterNether templates", failures);
        helper.succeed();
    }
}
