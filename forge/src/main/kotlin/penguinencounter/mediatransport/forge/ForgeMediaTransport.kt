package penguinencounter.mediatransport.forge

import dev.architectury.platform.forge.EventBuses
import penguinencounter.mediatransport.MediaTransport
import net.minecraft.data.DataProvider
import net.minecraft.data.DataProvider.Factory
import net.minecraft.data.PackOutput
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(MediaTransport.MODID)
class MediaTransportForge {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(MediaTransport.MODID, this)
            addListener(ForgeMediaTransportClient::init)
            addListener(::gatherData)
        }
        MediaTransport.init()
    }

    private fun gatherData(event: GatherDataEvent) {
        event.apply {
            // TODO: add datagen providers here
        }
    }
}

fun <T : DataProvider> GatherDataEvent.addProvider(run: Boolean, factory: (PackOutput) -> T) =
    generator.addProvider(run, Factory { factory(it) })
