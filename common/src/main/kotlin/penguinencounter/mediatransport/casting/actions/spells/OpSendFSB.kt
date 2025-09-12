package penguinencounter.mediatransport.casting.actions.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import org.figuramc.figura.server.FiguraServer
import penguinencounter.mediatransport.MediaTransportServer
import penguinencounter.mediatransport.casting.mishaps.MishapForSomeReasonFSBIsDedicatedServerOnly
import penguinencounter.mediatransport.casting.mishaps.MishapTooBigToSend
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.conversions.Encoder

object OpSendFSB : SpellAction {
    override val argc: Int = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        if (!FiguraServer.initialized()) throw MishapForSomeReasonFSBIsDedicatedServerOnly()
        val castingEntity = env.castingEntity
        if (castingEntity !is ServerPlayer) throw MishapBadCaster()

        val toSend = args[0]
        val toSendBytes = Encoder.encode(toSend)
        if (toSendBytes.size > MediaTransportConfig.server.maximumSendSize) throw MishapTooBigToSend(toSendBytes.size)

        return SpellAction.Result(Spell(castingEntity, toSendBytes, MediaTransportServer::tell), 0, listOf(
            ParticleSpray.burst(castingEntity.position(), 0.25, 5)
        ))
    }

    internal data class Spell(val destination: ServerPlayer, val data: ByteArray, val via: (to: ServerPlayer, data: ByteArray) -> Unit) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            via(destination, data)
        }

        // intellij complains if I don't implement these
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Spell

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }
}