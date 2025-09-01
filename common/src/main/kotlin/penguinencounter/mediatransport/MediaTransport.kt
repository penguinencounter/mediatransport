package penguinencounter.mediatransport

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.networking.MediaTransportNetworking
import penguinencounter.mediatransport.registry.MediaTransportActions

object MediaTransport {
    const val MODID = "mediatransport"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    fun id(path: String) = ResourceLocation(MODID, path)

    fun init() {
        MediaTransportConfig.init()
        initRegistries(
            MediaTransportActions,
        )
        MediaTransportNetworking.init()
    }
}
