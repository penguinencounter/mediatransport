package penguinencounter.mediatransport.networking

import dev.architectury.networking.NetworkChannel
import penguinencounter.mediatransport.MediaTransport
import penguinencounter.mediatransport.networking.msg.MediaTransportMessageCompanion

object MediaTransportNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(MediaTransport.id("networking_channel"))

    fun init() {
        for (subclass in MediaTransportMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
