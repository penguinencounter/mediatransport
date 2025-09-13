package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import penguinencounter.mediatransport.InteropGlue
import penguinencounter.mediatransport.config.MediaTransportConfig
import java.io.ByteArrayInputStream
import java.io.IOException

interface Decoder : TypeRegistrar {
    companion object {
        val converters by lazy {
            val result: MutableList<Decoder> = mutableListOf()
            result.add(HexConversions())
            if (InteropGlue.moreiotas()) result.add(MoreIotasConversions())
            if (InteropGlue.hexpose()) result.add(HexposeConversions())

            for (item in result) item.defineTypes(Types.types)

            result
        }

        fun decode(bytes: ByteArrayInputStream): Iota {
            return try {
                val type = bytes.read()
                val interpretedType = Types.types[type]
                if (!(interpretedType?.run(MediaTransportConfig.server::canReceive) ?: false)) GarbageIota()
                else converters.first { it.canDecode(type) }.decode(type, bytes)
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