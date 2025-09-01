package penguinencounter.mediatransport.forge

import penguinencounter.mediatransport.MediaTransportClient
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT

object ForgeMediaTransportClient {
    fun init(event: FMLClientSetupEvent) {
        MediaTransportClient.init()
        LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, parent -> MediaTransportClient.getConfigScreen(parent) }
        }
    }
}
