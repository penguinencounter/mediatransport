package penguinencounter.mediatransport.casting.actions.spells

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import org.figuramc.figura.server.FiguraServer
import penguinencounter.mediatransport.MediaTransportServer
import penguinencounter.mediatransport.casting.mishaps.MishapForSomeReasonFSBIsDedicatedServerOnly
import penguinencounter.mediatransport.casting.mishaps.MishapRadioSilence

object OpCheckQueue : ConstMediaAction {
    override val argc: Int = 0

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): List<Iota> {
        if (!FiguraServer.initialized()) throw MishapForSomeReasonFSBIsDedicatedServerOnly()
        val castingEntity = env.castingEntity
        if (castingEntity !is ServerPlayer) throw MishapBadCaster()

        val queue = MediaTransportServer.transportQueue[castingEntity.uuid] ?: return 0.0.asActionResult
        synchronized(queue) {
            return queue.size.asActionResult
        }
    }
}