package penguinencounter.mediatransport.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MishapNotSendable(val it: Iota, val details: Component?) : Mishap() {
    companion object {
        fun reason(iota: Iota, key: String) = MishapNotSendable(iota, "mediatransport.not_sendable.$key".asTranslatedComponent)
    }

    override fun accentColor(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): FrozenPigment = dyeColor(DyeColor.BLACK) // bweh so generic

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) =
        if (details == null) error("not_sendable", it.display())
        else error("not_sendable_ex", it.display(), details)

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: MutableList<Iota>
    ) {
        // do nothing
    }
}