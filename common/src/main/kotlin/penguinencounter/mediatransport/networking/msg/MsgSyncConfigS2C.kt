package penguinencounter.mediatransport.networking.msg

import penguinencounter.mediatransport.config.MediaTransportConfig
import net.minecraft.network.FriendlyByteBuf

data class MsgSyncConfigS2C(val serverConfig: MediaTransportConfig.ServerConfig) : MediaTransportMessageS2C {
    companion object : MediaTransportMessageCompanion<MsgSyncConfigS2C> {
        override val type = MsgSyncConfigS2C::class.java

        override fun decode(buf: FriendlyByteBuf) = MsgSyncConfigS2C(
            serverConfig = MediaTransportConfig.ServerConfig.decode(buf),
        )

        override fun MsgSyncConfigS2C.encode(buf: FriendlyByteBuf) {
            serverConfig.encode(buf)
        }
    }
}
