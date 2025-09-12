package penguinencounter.mediatransport.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import penguinencounter.mediatransport.casting.actions.spells.OpCheckQueue
import penguinencounter.mediatransport.casting.actions.spells.OpRecvFSB
import penguinencounter.mediatransport.casting.actions.spells.OpSendFSB
import penguinencounter.mediatransport.casting.actions.spells.OpSendOther

object MediaTransportActions : MediaTransportRegistrar<ActionRegistryEntry>(
    HexRegistries.ACTION,
    { HexActions.REGISTRY },
) {
    val SEND_FSB = make("send_fsb", HexDir.EAST, "edwdwad", OpSendFSB)
    val SEND_OTHER_FSB = make("send_other", HexDir.EAST, "wewdwwwdwaawawdwawaqwdwwdada", OpSendOther)
    val RECV_FSB = make("recv_fsb", HexDir.EAST, "edwdwwaa", OpRecvFSB)
    val CHECK_QUEUE = make("check_queue", HexDir.EAST, "edwdwq", OpCheckQueue)

    @Suppress("SameParameterValue")
    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
