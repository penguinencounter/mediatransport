package penguinencounter.mediatransport.conversions

import penguinencounter.mediatransport.config.MediaTransportConfig

object ServerInfo {
    fun getServerInfo() = buildData {
        // the Most data
        // tPPSSSSIIIIRRRRCCCCCCCCcccccccciiiiiiii
        writeByte(Types.SERVER_INFO)
        writeShort(Types.PROTOCOL_VERSION)
        writeInt(MediaTransportConfig.server.maximumSendSize)
        writeInt(MediaTransportConfig.server.maximumInterSendSize)
        writeInt(MediaTransportConfig.server.maximumRecvSize)
        writeDouble(MediaTransportConfig.server.rateLimitMaxValue)
        writeDouble(MediaTransportConfig.server.rateLimitChargePerTick)
        writeDouble(MediaTransportConfig.server.interSendCostMultiplier)
    }
}