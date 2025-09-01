package penguinencounter.mediatransport.networking.msg

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager.PacketContext
import penguinencounter.mediatransport.MediaTransport
import penguinencounter.mediatransport.networking.MediaTransportNetworking
import penguinencounter.mediatransport.networking.handler.applyOnClient
import penguinencounter.mediatransport.networking.handler.applyOnServer
import net.fabricmc.api.EnvType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

sealed interface MediaTransportMessage

sealed interface MediaTransportMessageC2S : MediaTransportMessage {
    fun sendToServer() {
        MediaTransportNetworking.CHANNEL.sendToServer(this)
    }
}

sealed interface MediaTransportMessageS2C : MediaTransportMessage {
    fun sendToPlayer(player: ServerPlayer) {
        MediaTransportNetworking.CHANNEL.sendToPlayer(player, this)
    }

    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        MediaTransportNetworking.CHANNEL.sendToPlayers(players, this)
    }
}

sealed interface MediaTransportMessageCompanion<T : MediaTransportMessage> {
    val type: Class<T>

    fun decode(buf: FriendlyByteBuf): T

    fun T.encode(buf: FriendlyByteBuf)

    fun apply(msg: T, supplier: Supplier<PacketContext>) {
        val ctx = supplier.get()
        when (ctx.env) {
            EnvType.SERVER, null -> {
                MediaTransport.LOGGER.debug("Server received packet from {}: {}", ctx.player.name.string, this)
                when (msg) {
                    is MediaTransportMessageC2S -> msg.applyOnServer(ctx)
                    else -> MediaTransport.LOGGER.warn("Message not handled on server: {}", msg::class)
                }
            }
            EnvType.CLIENT -> {
                MediaTransport.LOGGER.debug("Client received packet: {}", this)
                when (msg) {
                    is MediaTransportMessageS2C -> msg.applyOnClient(ctx)
                    else -> MediaTransport.LOGGER.warn("Message not handled on client: {}", msg::class)
                }
            }
        }
    }

    fun register(channel: NetworkChannel) {
        channel.register(type, { msg, buf -> msg.encode(buf) }, ::decode, ::apply)
    }
}
