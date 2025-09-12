package penguinencounter.mediatransport.config

import dev.architectury.event.events.client.ClientPlayerEvent
import dev.architectury.event.events.common.PlayerEvent
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import me.shedaniel.autoconfig.serializer.PartitioningSerializer
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.minecraft.network.FriendlyByteBuf
import penguinencounter.mediatransport.MediaTransport
import penguinencounter.mediatransport.networking.msg.MsgSyncConfigS2C

object MediaTransportConfig {
    @JvmStatic
    lateinit var holder: ConfigHolder<GlobalConfig>

    @JvmStatic
    val client get() = holder.config.client

    @JvmStatic
    val server get() = syncedServerConfig ?: holder.config.server

    const val VERSION_ID = 0

    // only used on the client, probably
    private var syncedServerConfig: ServerConfig? = null

    fun init() {
        holder = AutoConfig.register(
            GlobalConfig::class.java,
            PartitioningSerializer.wrap(::Toml4jConfigSerializer),
        )

        PlayerEvent.PLAYER_JOIN.register { player ->
            MsgSyncConfigS2C(holder.config.server).sendToPlayer(player)
        }
    }

    fun initClient() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { _ ->
            syncedServerConfig = null
        }
    }

    fun onSyncConfig(serverConfig: ServerConfig) {
        syncedServerConfig = serverConfig
    }

    @Config(name = MediaTransport.MODID)
    class GlobalConfig : GlobalData() {
        @Category("client")
        @TransitiveObject
        val client = ClientConfig()

        @Category("server")
        @TransitiveObject
        val server = ServerConfig()
    }

    @Config(name = "client")
    class ClientConfig : ConfigData {
//        @Tooltip
//        val clientConfigOption: Boolean = true
    }

    @Config(name = "server")
    class ServerConfig : ConfigData {
        @Tooltip
        var maximumSendSize: Int = 10240
            private set
        @Tooltip
        var maximumInterSendSize: Int = 10240
            private set
        @Tooltip
        var maximumRecvSize: Int = 10240
            private set
        @Tooltip
        var matrixMaxArea: Int = 10240
            private set

        fun encode(buf: FriendlyByteBuf) {
            buf.writeInt(VERSION_ID)

            buf.writeInt(maximumSendSize)
            buf.writeInt(maximumInterSendSize)
            buf.writeInt(maximumRecvSize)
            buf.writeInt(matrixMaxArea)
        }

        companion object {
            fun decode(buf: FriendlyByteBuf) = when (val it = buf.readInt()) {
                0 -> decode0(buf)
                else -> {
                    if (it in VERSION_ID..256) {
                        throw IllegalArgumentException("can't decode config v$it (a newer version! this is v$VERSION_ID)")
                    } else if (it < VERSION_ID) {
                        throw IllegalArgumentException("can't decode config v$it (too old to decode - try an older version of this mod)")
                    } else {
                        throw IllegalArgumentException("can't decode config v$it (either way too old, or corrupt?)")
                    }
                }
            }

            // latest
            fun decode0(buf: FriendlyByteBuf) = ServerConfig().apply {
                maximumSendSize = buf.readInt()
                maximumInterSendSize = buf.readInt()
                maximumRecvSize = buf.readInt()
                matrixMaxArea = buf.readInt()
            }
        }
    }
}
