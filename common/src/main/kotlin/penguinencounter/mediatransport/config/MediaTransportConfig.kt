package penguinencounter.mediatransport.config

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import dev.architectury.event.events.client.ClientPlayerEvent
import dev.architectury.event.events.common.PlayerEvent
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import me.shedaniel.autoconfig.serializer.PartitioningSerializer
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.minecraft.network.FriendlyByteBuf
import penguinencounter.mediatransport.MediaTransport
import penguinencounter.mediatransport.casting.mishaps.MishapNotSendable
import penguinencounter.mediatransport.config.Filter.FilterMode
import penguinencounter.mediatransport.networking.msg.MsgSyncConfigS2C
import kotlin.jvm.Throws

object MediaTransportConfig {
    @JvmStatic
    lateinit var holder: ConfigHolder<GlobalConfig>

    @JvmStatic
    val client get() = holder.config.client

    @JvmStatic
    val server get() = syncedServerConfig ?: holder.config.server

    const val VERSION_ID = 1

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

        @Tooltip
        var rateLimitChargePerTick: Double = 1.0
            private set

        @Tooltip
        var rateLimitMaxValue: Double = 8.0
            private set

        @Tooltip
        var interSendCostMultiplier: Double = 2.0
            private set

        @ConfigEntry.Gui.Excluded
        var sendFilterMode: FilterMode = FilterMode.Block

        @ConfigEntry.Gui.Excluded
        var sendFilter: List<String> = listOf()

        @ConfigEntry.Gui.Excluded
        var interSendFilterMode: FilterMode = FilterMode.Block

        @ConfigEntry.Gui.Excluded
        var interSendFilter: List<String> = listOf()

        @ConfigEntry.Gui.Excluded
        var recvFilterMode: FilterMode = FilterMode.Block

        @ConfigEntry.Gui.Excluded
        var recvFilter: List<String> = listOf("hexcasting:list")

        private fun can(type: IotaType<*>, filterMode: FilterMode, filterSet: List<String>): Boolean {
            val reg = HexIotaTypes.REGISTRY.getKey(type) ?: throw IllegalArgumentException("iota type not registered! $type (registry miss)")
            val named = reg.toString()
            return Filter.matches(filterMode, filterSet, named)
        }

        @Throws(MishapNotSendable::class)
        private fun recurValidate(iota: Iota, filterMode: FilterMode, filterSet: List<String>) {
            if (!can(iota.type, filterMode, filterSet)) throw MishapNotSendable.reason(iota, "bad_type")
            iota.subIotas()?.forEach {
                recurValidate(it, filterMode, filterSet)
            }
        }

        fun validateSend(iota: Iota) = recurValidate(iota, sendFilterMode, sendFilter)
        fun validateInterSend(iota: Iota) = recurValidate(iota, interSendFilterMode, interSendFilter)
        fun canReceive(iotaType: IotaType<*>): Boolean = can(iotaType, recvFilterMode, recvFilter)

        fun encode(buf: FriendlyByteBuf) {
            buf.writeInt(VERSION_ID)

            buf.writeInt(maximumSendSize)
            buf.writeInt(maximumInterSendSize)
            buf.writeInt(maximumRecvSize)
            buf.writeInt(matrixMaxArea)

            buf.writeByte(sendFilterMode.ordinal)
            buf.writeByte(interSendFilterMode.ordinal)
            buf.writeByte(recvFilterMode.ordinal)

            // Filters: <n items> <m length> bytes......
            buf.sendStringList(sendFilter)
            buf.sendStringList(interSendFilter)
            buf.sendStringList(recvFilter)
        }

        private fun FriendlyByteBuf.sendStringList(list: List<String>) {
            writeInt(list.size)
            for (item in list) {
                val bytes = item.toByteArray(Charsets.UTF_8)
                writeInt(bytes.size)
                writeBytes(bytes)
            }
        }

        companion object {
            fun decode(buf: FriendlyByteBuf) = when (val it = buf.readInt()) {
                VERSION_ID -> decodeLatest(buf)
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

            private fun FriendlyByteBuf.readStringList() = MutableList(readInt()) {
                val size = readInt()
                readBytes(size).toString(Charsets.UTF_8)
            }

            // latest
            fun decodeLatest(buf: FriendlyByteBuf) = ServerConfig().apply {
                maximumSendSize = buf.readInt()
                maximumInterSendSize = buf.readInt()
                maximumRecvSize = buf.readInt()
                matrixMaxArea = buf.readInt()

                sendFilterMode = FilterMode.entries[buf.readByte().toInt()]
                interSendFilterMode = FilterMode.entries[buf.readByte().toInt()]
                recvFilterMode = FilterMode.entries[buf.readByte().toInt()]

                sendFilter = buf.readStringList()
                interSendFilter = buf.readStringList()
                recvFilter = buf.readStringList()
            }
        }
    }
}
