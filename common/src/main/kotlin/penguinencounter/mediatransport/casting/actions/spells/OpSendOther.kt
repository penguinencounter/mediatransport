package penguinencounter.mediatransport.casting.actions.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import org.figuramc.figura.server.FiguraServer
import penguinencounter.mediatransport.MediaTransportServer
import penguinencounter.mediatransport.casting.mishaps.MishapForSomeReasonFSBIsDedicatedServerOnly
import penguinencounter.mediatransport.casting.mishaps.MishapTooBigToSendInter
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.conversions.Encoder

// entity, sendable ->
object OpSendOther : SpellAction {
    override val argc: Int = 2

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        if (!FiguraServer.initialized()) throw MishapForSomeReasonFSBIsDedicatedServerOnly()
        val target = args.getPlayer(0, argc = 2)
        val toSend = args[1]
        val toSendBytes = Encoder.encode(toSend)
        if (toSendBytes.size > MediaTransportConfig.server.maximumInterSendSize)
            throw MishapTooBigToSendInter(toSendBytes.size)

        // This actually uses SendFSB instead
        return SpellAction.Result(
            OpSendFSB.Spell(target, toSendBytes, MediaTransportServer::tellExternal),
            MediaConstants.DUST_UNIT / 2, // .5 dust
            listOf(
                ParticleSpray.burst(target.position(), 0.25, 5)
            )
        )
    }
}