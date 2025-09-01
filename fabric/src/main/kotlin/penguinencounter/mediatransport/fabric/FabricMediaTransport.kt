package penguinencounter.mediatransport.fabric

import penguinencounter.mediatransport.MediaTransport
import net.fabricmc.api.ModInitializer

object FabricMediaTransport : ModInitializer {
    override fun onInitialize() {
        MediaTransport.init()
    }
}
