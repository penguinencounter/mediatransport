package penguinencounter.mediatransport

import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.config.MediaTransportConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screens.Screen
import vazkii.patchouli.api.PatchouliAPI

object MediaTransportClient {
    fun init() {
        MediaTransportConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }
}
