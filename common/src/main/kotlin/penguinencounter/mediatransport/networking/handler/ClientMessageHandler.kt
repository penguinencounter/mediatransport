package penguinencounter.mediatransport.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.networking.msg.*

fun MediaTransportMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            MediaTransportConfig.onSyncConfig(serverConfig)
        }

        // add more client-side message handlers here
    }
}
