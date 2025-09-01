package penguinencounter.mediatransport.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import penguinencounter.mediatransport.MediaTransportClient

object FabricMediaTransportModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(MediaTransportClient::getConfigScreen)
}
