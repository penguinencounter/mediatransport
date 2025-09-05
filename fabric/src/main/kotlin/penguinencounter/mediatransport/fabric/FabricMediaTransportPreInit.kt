package penguinencounter.mediatransport.fabric

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import penguinencounter.mediatransport.InteropGlue

object FabricMediaTransportPreInit : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        if (!InteropGlue.isFiguraValid()) throw RuntimeException(InteropGlue.figuraErrorMessage)
    }
}
