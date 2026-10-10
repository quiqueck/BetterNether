package org.betterx.betternether.testmod.gametest;

import org.betterx.betternether.blocks.BlockStatueRespawner;
import org.betterx.betternether.registry.block.NetherFunctionalBlocks;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

import io.netty.buffer.Unpooled;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * The pig statue's "you must hold 4 glowstone" message passed an ItemStack as translation argument. That is
 * not a valid chat argument: the packet failed to encode on a dedicated server ("Can't deliver chat message")
 * and the text showed the stack's toString ("4 minecraft:glowstone").
 */
public class StatueRespawnerMessageGameTest {
    @GameTest
    public void helpMessageCanBeSentToAClient(GameTestHelper helper) {
        final BlockStatueRespawner statue = (BlockStatueRespawner) NetherFunctionalBlocks.PIG_STATUE_RESPAWNER;
        final Component message = statue.getHelpMessage();

        final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                helper.getLevel().registryAccess()
        );
        try {
            ClientboundSystemChatPacket.STREAM_CODEC.encode(buf, new ClientboundSystemChatPacket(message, true));
            final ClientboundSystemChatPacket decoded = ClientboundSystemChatPacket.STREAM_CODEC.decode(buf);
            if (!(decoded.content().getContents() instanceof TranslatableContents contents)
                    || contents.getArgs().length != 1
                    || !(contents.getArgs()[0] instanceof Component item)) {
                throw helper.assertionException(Component.literal("unexpected help message: " + decoded.content()));
            }
            final String text = item.getString();
            if (!"4 Glowstone".equals(text)) {
                throw helper.assertionException(Component.literal("help message names the item as '" + text + "'"));
            }
        } catch (GameTestAssertException e) {
            throw e;
        } catch (RuntimeException e) {
            throw helper.assertionException(Component.literal("help message can not be encoded: " + e));
        } finally {
            buf.release();
        }
        helper.succeed();
    }
}
