package penguinencounter.mediatransport.networking.figura

import org.figuramc.figura.server.packets.CustomFSBPacket

class MediaTransportS2CMessage : CustomFSBPacket {
    constructor(data: ByteArray): super(MediaTransportFigura.channelS2C, data)
}