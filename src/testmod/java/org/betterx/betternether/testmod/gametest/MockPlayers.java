package org.betterx.betternether.testmod.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

import java.util.Collection;
import java.util.UUID;

/**
 * A survival-mode {@link ServerPlayer} for GameTests.
 *
 * <h2>Why not {@code GameTestHelper}'s own mock players</h2>
 * <ul>
 *   <li>{@code makeMockServerPlayerInLevel()} builds an anonymous subclass whose {@code gameMode()} is
 *       hard-coded to {@code CREATIVE}, which no amount of {@code setGameMode} can change. Creative
 *       changes block-breaking behaviour outright, so it is useless for the destroy-speed tests.</li>
 *   <li>{@code makeMockServerPlayer(GameType)} honours the requested mode but never registers the
 *       player with the {@code PlayerList}, leaving {@code connection} null - {@code addEffect} and
 *       {@code lookAt} both NPE trying to send packets.</li>
 * </ul>
 * This class takes the second approach and suppresses the effect-sync hooks, which exist only to
 * notify a client that a test double does not have.
 *
 * <h2>Equipment does not apply by itself</h2>
 * A mock player never runs {@code LivingEntity#detectEquipmentUpdates}, so putting an item in a slot
 * grants none of its attribute modifiers and fires none of its enchantment
 * {@code EnchantmentAttributeEffect}s. Tests that need those must apply them at the vanilla call site
 * explicitly - see {@link ObsidianBreakerGameTest#equipAndApplyEnchantments}.
 */
public final class MockPlayers {
    private MockPlayers() {}

    /** A survival-mode player standing at {@code relativePos} within the test structure. */
    public static ServerPlayer survival(GameTestHelper helper, BlockPos relativePos) {
        final ServerPlayer player = new SurvivalTestPlayer(helper);
        GameType.SURVIVAL.updatePlayerAbilities(player.getAbilities());

        final BlockPos abs = helper.absolutePos(relativePos);
        player.snapTo(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5, 0.0f, 0.0f);
        return player;
    }

    /**
     * A player that is actually registered with the {@code PlayerList} and standing in the structure.
     * <p>
     * Required by anything that looks players up through the level - both BetterNether advancement
     * triggers scan {@code level.getEntitiesOfClass(ServerPlayer.class, ...)} around the converted
     * block, and a hand-constructed player is not in that list no matter where it is positioned.
     * <p>
     * This variant is stuck in creative mode (see the class notes), which is irrelevant for advancement
     * triggers but rules it out for the destroy-speed tests.
     */
    @SuppressWarnings("removal")
    public static ServerPlayer inLevel(GameTestHelper helper, BlockPos relativePos) {
        final ServerPlayer player = helper.makeMockServerPlayerInLevel();
        final BlockPos abs = helper.absolutePos(relativePos);
        // teleportTo, not snapTo: only teleportTo moves the player's chunk-loading ticket, and a player
        // sitting in an unloaded chunk is never ticked.
        player.teleportTo(
                helper.getLevel(),
                abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5,
                java.util.Set.of(),
                0.0f, 0.0f,
                false
        );
        return player;
    }

    private static final class SurvivalTestPlayer extends ServerPlayer {
        private SurvivalTestPlayer(GameTestHelper helper) {
            super(
                    helper.getLevel().getServer(),
                    helper.getLevel(),
                    new GameProfile(UUID.randomUUID(), "test-survival-player"),
                    ClientInformation.createDefault()
            );
        }

        @Override
        public GameType gameMode() {
            return GameType.SURVIVAL;
        }

        @Override
        protected void onEffectAdded(MobEffectInstance instance, Entity source) {
        }

        @Override
        protected void onEffectUpdated(MobEffectInstance instance, boolean forced, Entity source) {
        }

        @Override
        protected void onEffectsRemoved(Collection<MobEffectInstance> instances) {
        }
    }
}
