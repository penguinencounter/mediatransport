package penguinencounter.mediatransport

import net.minecraft.server.level.ServerPlayer
import org.figuramc.figura.server.FiguraServer
import penguinencounter.mediatransport.networking.figura.MediaTransportFigura
import penguinencounter.mediatransport.networking.figura.MediaTransportListener
import penguinencounter.mediatransport.networking.figura.MediaTransportS2CMessage
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue

object MediaTransportServer {
    val transportQueue: MutableMap<UUID, MutableList<ByteArray>> = mutableMapOf()

    fun enqueue(uuid: UUID, bytes: ByteArray) {
        val theQueue = transportQueue.computeIfAbsent(uuid) { _ -> mutableListOf() }
        synchronized(theQueue) {
            theQueue.add(bytes)
            if (theQueue.size > 6) theQueue.removeAt(0)
            MediaTransport.LOGGER.info("Added item to queue for $uuid, now ${theQueue.size} item(s)")
        }
    }

    fun pollQueue(uuid: UUID): ByteArray? {
        val theQueue = transportQueue.computeIfAbsent(uuid) { _ -> mutableListOf() }
        return synchronized(theQueue) {
            if (theQueue.isEmpty()) null
            else theQueue.removeAt(0)
        }
    }

    fun tell(player: ServerPlayer, bytes: ByteArray) {
        if (!FiguraServer.initialized()) {
            MediaTransport.LOGGER.warn("not initialized")
            return
        }

        MediaTransport.LOGGER.info("Sending Figura packet, ${bytes.size} payload bytes")

        val server: FiguraServer = FiguraServer.getInstance()!!
        server.sendPacket(player.uuid, MediaTransportS2CMessage(bytes))
    }

    fun initializeFigura() {
        if (!FiguraServer.initialized()) throw IllegalStateException("the point was that you were supposed to mixin somewhere to call this...")
        val server: FiguraServer = FiguraServer.getInstance()!!

        MediaTransport.LOGGER.info("figura server started, okay")
        server.customPackets()!!.registerListener(MediaTransportFigura.channelC2S, MediaTransportListener)
    }
}
