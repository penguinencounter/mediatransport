package penguinencounter.mediatransport.casting.actions.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import org.figuramc.figura.server.FiguraServer
import penguinencounter.mediatransport.MediaTransportServer
import penguinencounter.mediatransport.casting.mishaps.MishapForSomeReasonFSBIsDedicatedServerOnly
import penguinencounter.mediatransport.conversions.ServerInfo

object OpSendServerInfo : SpellAction {
    override val argc: Int = 0

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        if (!FiguraServer.initialized()) throw MishapForSomeReasonFSBIsDedicatedServerOnly()
        val castingEntity = env.castingEntity
        if (castingEntity !is ServerPlayer) throw MishapBadCaster()

        MediaTransportServer.useCooldown(castingEntity, 1.0)

        val toSendBytes = ServerInfo.getServerInfo()
        // Note: no size check here - if you can't handle four ints you have other problems
        // (and maybe should lower the ratelimits)

        return SpellAction.Result(
            OpSendFSB.Spell(castingEntity, toSendBytes, MediaTransportServer::tell),
            0,
            listOf(
                ParticleSpray.burst(castingEntity.position(), 0.25, 5)
            )
        )
    }
}