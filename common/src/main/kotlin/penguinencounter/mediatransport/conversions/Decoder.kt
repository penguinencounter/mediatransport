package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import penguinencounter.mediatransport.InteropGlue
import java.io.ByteArrayInputStream
import java.io.IOException

interface Decoder {
    companion object {
        val converters by lazy {
            val result: MutableList<Decoder> = mutableListOf()
            result.add(HexConversions())
            if (InteropGlue.moreiotas()) result.add(MoreIotasConversions())
            result
        }

        fun decode(bytes: ByteArrayInputStream): Iota {
            return try {
                val type = bytes.read()
                converters.first { it.canDecode(type) }.decode(type, bytes)
            } catch (_: IOException) {
                GarbageIota()
            } catch (_: NoSuchElementException) {
                GarbageIota()
            }
        }
    }

    fun decode(type: Int, bytes: ByteArrayInputStream): Iota
    fun canDecode(type: Int): Boolean
}