package penguinencounter.mediatransport.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.DyeColor
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

/**
 * No data available.
 */
class MishapRadioSilence : Mishap() {
    companion object {
        val options = listOf(
            "crickets",
            "ellipsis",
            "help",
            "eof",
            "insanity",
            "informal"
        )
        val rand = SecureRandom().asKotlinRandom()
    }

    override fun accentColor(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): FrozenPigment = dyeColor(DyeColor.WHITE)

    override fun errorMessage(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): Component {
        var option = options.random(rand)
        val caster = ctx.castingEntity
        while (option == "insanity") {
            if (caster is ServerPlayer) {
                break
            }
            // if it wouldn't be a player, don't do that
            option = options.random(rand)
        }
        return error("radio_silence", "mediatransport.radio_silence_options.$option".asTranslatedComponent)
    }

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: MutableList<Iota>
    ) {
        val player = env.castingEntity as? ServerPlayer ?: return
        player.playNotifySound(
            SoundEvents.GOAT_SCREAMING_PREPARE_RAM,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }
}