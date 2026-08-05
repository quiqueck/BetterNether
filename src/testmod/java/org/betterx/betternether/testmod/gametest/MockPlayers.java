package org.betterx.betternether.testmod.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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

    /**
     * A survival-mode player standing at {@code relativePos} within the test structure.
     * <p>
     * Registered with the {@code PlayerList} over an embedded connection, the way
     * {@code makeMockServerPlayerInLevel} does it, but with a subclass whose {@code gameMode()} is
     * survival. Without a connection anything that reaches {@code ServerPlayer#isInvulnerableTo} NPEs
     * on {@code connection.hasClientLoaded()} - which is on the path of every hit the player takes, so
     * a connection-less player cannot be used as an attack target at all.
     */
    public static ServerPlayer survival(GameTestHelper helper, BlockPos relativePos) {
        final GameProfile profile = new GameProfile(UUID.randomUUID(), "test-survival-player");
        final SurvivalTestPlayer player = new SurvivalTestPlayer(helper, profile);

        // Adding the Connection to an EmbeddedChannel's pipeline fires channelActive, which is what
        // binds Connection#channel. Without it every packet send NPEs on a null channel.
        final Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);

        helper.getLevel()
              .getServer()
              .getPlayerList()
              .placeNewPlayer(
                      connection,
                      player,
                      CommonListenerCookie.createInitial(profile, false)
              );
        GameType.SURVIVAL.updatePlayerAbilities(player.getAbilities());
        player.onUpdateAbilities();

        final BlockPos abs = helper.absolutePos(relativePos);
        player.teleportTo(
                helper.getLevel(),
                abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5,
                java.util.Set.of(),
                0.0f, 0.0f,
                false
        );
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
        private SurvivalTestPlayer(GameTestHelper helper, GameProfile profile) {
            super(
                    helper.getLevel().getServer(),
                    helper.getLevel(),
                    profile,
                    ClientInformation.createDefault()
            );
        }

        @Override
        public GameType gameMode() {
            return GameType.SURVIVAL;
        }

        /**
         * A mock player's client never finishes loading, and {@code ServerPlayer#isInvulnerableTo}
         * treats a not-yet-loaded client as invulnerable. That makes the player silently untargetable:
         * {@code hurtServer} returns false, so an attack on it does nothing at all and no post-attack
         * enchantment effect ever runs. Delegating to the LivingEntity behaviour keeps it hittable.
         */
        @Override
        public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
            return false;
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
