package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.Iota
import penguinencounter.mediatransport.InteropGlue
import penguinencounter.mediatransport.casting.mishaps.MishapNotSendable
import penguinencounter.mediatransport.casting.mishaps.MishapTooBigToSend
import penguinencounter.mediatransport.config.MediaTransportConfig

interface Encoder {
    fun canEncode(it: Iota): Boolean

    @Throws(MishapNotSendable::class)
    fun encode(it: Iota): ByteArray

    companion object {
        val converters by lazy {
            val result: MutableList<Encoder> = mutableListOf()
            result.add(HexConversions())
            if (InteropGlue.moreiotas()) result.add(MoreIotasConversions())

            result
        }

        @Throws(MishapNotSendable::class, MishapTooBigToSend::class)
        fun encode(iota: Iota): ByteArray {
            try {
                val result = converters.first { it.canEncode(iota) }.encode(iota)
                if (result.size > MediaTransportConfig.server.maximumSendSize) throw MishapTooBigToSend(result.size)
                return result
            } catch (_: NoSuchElementException) {
                throw MishapNotSendable.reason(iota, "bad_type")
            }
        }
    }
}