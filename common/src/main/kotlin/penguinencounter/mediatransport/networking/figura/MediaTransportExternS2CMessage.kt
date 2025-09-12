package penguinencounter.mediatransport.networking.figura

import org.figuramc.figura.server.packets.CustomFSBPacket

class MediaTransportExternS2CMessage : CustomFSBPacket {
    constructor(data: ByteArray): super(MediaTransportFigura.channelExternS2C, data)
}