package penguinencounter.mediatransport.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.asTextComponent
import net.minecraft.world.item.DyeColor
import penguinencounter.mediatransport.config.MediaTransportConfig

class MishapTooBigToSendInter(val computedSize: Int) : Mishap() {
    override fun accentColor(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): FrozenPigment = dyeColor(DyeColor.BLACK) // bweh so generic

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) =
        error(
            "too_large_to_send_inter",
            computedSize.toString().asTextComponent,
            MediaTransportConfig.server.maximumInterSendSize.toString().asTextComponent
        )

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: MutableList<Iota>
    ) {
        // do nothing
    }
}