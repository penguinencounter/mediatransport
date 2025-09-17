package penguinencounter.mediatransport

import net.minecraft.server.level.ServerPlayer
import org.figuramc.figura.server.FiguraServer
import org.figuramc.figura.server.packets.CustomFSBPacket
import penguinencounter.mediatransport.casting.mishaps.MishapTooFast
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.networking.figura.MediaTransportExternS2CMessage
import penguinencounter.mediatransport.networking.figura.MediaTransportFigura
import penguinencounter.mediatransport.networking.figura.MediaTransportListener
import penguinencounter.mediatransport.networking.figura.MediaTransportS2CMessage
import java.util.*
import kotlin.math.max
import kotlin.math.min

object MediaTransportServer {
    val transportQueue: MutableMap<UUID, MutableList<ByteArray>> = mutableMapOf()
    val cooldowns: MutableMap<UUID, Double> = mutableMapOf()
    val lastTickUsed: MutableMap<UUID, Int> = mutableMapOf()

    fun updateCooldown(player: ServerPlayer) {
        val theServer = player.server
        val now = theServer.tickCount

        val uuid = player.uuid
        val lastUsed = lastTickUsed.getOrElse(uuid) {
            lastTickUsed[uuid] = now
            cooldowns[uuid] = MediaTransportConfig.server.rateLimitMaxValue
            return
        }
        val chargeSinceLastUse = (now - lastUsed) * MediaTransportConfig.server.rateLimitChargePerTick
        cooldowns.compute(uuid) { _, last ->
            min(
                last!! + chargeSinceLastUse,
                MediaTransportConfig.server.rateLimitMaxValue
            )
        }
    }

    @Throws(MishapTooFast::class)
    fun useCooldown(player: ServerPlayer, cost: Double): Boolean {
        updateCooldown(player)
        val cooldownValue = cooldowns[player.uuid]!!
        if (cooldownValue < cost) throw MishapTooFast(cost, cooldownValue)
        cooldowns.compute(player.uuid) { _, last ->
            max(0.0, last!! - cost)
        }
        return true
    }

    fun enqueue(uuid: UUID, bytes: ByteArray) {
        val theQueue = transportQueue.computeIfAbsent(uuid) { _ -> mutableListOf() }
        synchronized(theQueue) {
            theQueue.add(bytes)
            if (theQueue.size > 6) theQueue.removeAt(0)
            MediaTransport.LOGGER.info("Added item to queue for $uuid, now ${theQueue.size} item(s)")
        }
    }

    private inline fun tell(player: ServerPlayer, bytes: ByteArray, packet: (data: ByteArray) -> CustomFSBPacket) {
        if (!FiguraServer.initialized()) {
            MediaTransport.LOGGER.warn("not initialized")
            return
        }

        MediaTransport.LOGGER.info("Sending Figura packet, ${bytes.size} payload bytes")

        val server: FiguraServer = FiguraServer.getInstance()!!
        server.sendPacket(player.uuid, packet(bytes))
    }

    fun tell(player: ServerPlayer, bytes: ByteArray) = tell(player, bytes, ::MediaTransportS2CMessage)
    fun tellExternal(player: ServerPlayer, bytes: ByteArray) = tell(player, bytes, ::MediaTransportExternS2CMessage)

    fun initializeFigura() {
        if (!FiguraServer.initialized()) throw IllegalStateException("the point was that you were supposed to mixin somewhere to call this...")
        val server: FiguraServer = FiguraServer.getInstance()!!

        MediaTransport.LOGGER.info("figura server started, okay")
        server.customPackets()!!.registerListener(MediaTransportFigura.channelC2S, MediaTransportListener)
    }
}
