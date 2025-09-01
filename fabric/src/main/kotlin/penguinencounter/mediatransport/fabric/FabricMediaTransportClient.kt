package penguinencounter.mediatransport.fabric

import penguinencounter.mediatransport.MediaTransportClient
import net.fabricmc.api.ClientModInitializer

object FabricMediaTransportClient : ClientModInitializer {
    override fun onInitializeClient() {
        MediaTransportClient.init()
    }
}
