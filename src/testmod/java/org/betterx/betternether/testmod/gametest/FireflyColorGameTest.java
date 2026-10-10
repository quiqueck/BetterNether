package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.entity.EntityFirefly;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * A firefly loaded from data without a stored color (which is what {@code /summon} does) fell back to
 * white instead of keeping the random color every firefly gets on creation.
 */
public class FireflyColorGameTest {
    private static final int WHITE = 0xFFFFFFFF;

    @GameTest
    public void summonedFirefliesKeepARandomColor(GameTestHelper helper) {
        final BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        final MinecraftServer server = helper.getLevel().getServer();
        for (int i = 0; i < 8; i++) {
            server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withLevel(helper.getLevel()).withSuppressedOutput(),
                    "summon betternether:firefly " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " {NoAI:1b}"
            );
        }

        final List<EntityFirefly> fireflies = helper.getLevel().getEntitiesOfClass(
                EntityFirefly.class,
                new AABB(pos).inflate(2)
        );
        if (fireflies.size() != 8) {
            throw helper.assertionException(Component.literal("expected 8 summoned fireflies, found " + fireflies.size()));
        }
        if (fireflies.stream().allMatch(f -> f.getColor() == WHITE)) {
            throw helper.assertionException(Component.literal("every summoned firefly is white"));
        }
        fireflies.forEach(EntityFirefly::discard);
        helper.succeed();
    }
}
