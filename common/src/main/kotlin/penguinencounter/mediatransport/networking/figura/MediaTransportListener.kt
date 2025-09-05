package penguinencounter.mediatransport.networking.figura

import org.figuramc.figura.server.FiguraCustomPackets
import org.figuramc.figura.server.FiguraUser
import penguinencounter.mediatransport.MediaTransportServer
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.conversions.Types

object MediaTransportListener : FiguraCustomPackets.CustomPacketListener {
    override fun dispatch(
        packetName: String,
        sender: FiguraUser,
        data: ByteArray
    ) {
        val uuid = sender.uuid()
        if (data.size > MediaTransportConfig.server.maximumRecvSize)
            MediaTransportServer.enqueue(uuid, Types.garbageData)
        else
            MediaTransportServer.enqueue(uuid, data)
    }
}