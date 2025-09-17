package penguinencounter.mediatransport.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.DyeColor
import penguinencounter.mediatransport.config.MediaTransportConfig

class MishapTooFast(val amountWanted: Double, val amountAvailable: Double) : Mishap() {
    override fun accentColor(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): FrozenPigment = dyeColor(DyeColor.ORANGE)

    override fun errorMessage(
        ctx: CastingEnvironment,
        errorCtx: Context
    ) = error(
        "rate_limit_exceeded",
        "%.1f".format(amountWanted),
        "%.1f".format(amountAvailable),
        "%.2f".format(MediaTransportConfig.server.rateLimitChargePerTick),
        "%.1f".format(MediaTransportConfig.server.rateLimitMaxValue),
    )

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: MutableList<Iota>
    ) {
        val player = env.castingEntity as? ServerPlayer ?: return
        player.playNotifySound(
            SoundEvents.GLASS_BREAK,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }
}