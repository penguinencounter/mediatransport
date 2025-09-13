package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import miyucomics.hexpose.iotas.TextIota
import net.minecraft.network.chat.Component
import penguinencounter.mediatransport.config.MediaTransportConfig
import penguinencounter.mediatransport.conversions.Types.TEXT
import java.io.ByteArrayInputStream
import java.io.EOFException
import kotlin.math.max

class HexposeConversions : Encoder, Decoder {
    override fun canEncode(it: Iota): Boolean = when (it) {
        is TextIota -> true
        else -> false
    }

    override fun encode(it: Iota): ByteArray = when (it) {
        is TextIota -> encodeText(it)
        else -> misdispatched(this::class, it)
    }

    fun encodeText(it: TextIota): ByteArray = buildData {
        val upperBound =
            max(MediaTransportConfig.server.maximumSendSize, MediaTransportConfig.server.maximumInterSendSize) + 1
        writeByte(TEXT)
        val flat = it.text.getString(upperBound).toByteArray(Charsets.UTF_8)
        writeInt(flat.size)
        write(flat)
    }

    fun readText(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        val size = readInt()
        // so we can't really restore formatting, so literal it is
        try {
            val text = readNBytes(size)
            if (text.size != size) return@unpackData GarbageIota()
            val string = String(text, Charsets.UTF_8)
            TextIota(Component.literal(string))
        } catch (_: MishapInvalidIota) {
            GarbageIota()
        }
    }

    override fun decode(
        type: Int,
        bytes: ByteArrayInputStream
    ): Iota {
        return try {
            when (type) {
                TEXT -> readText(bytes)
                else -> GarbageIota()
            }
        } catch (_: EOFException) {
            GarbageIota()
        }
    }

    override fun canDecode(type: Int): Boolean = when (type) {
        TEXT -> true
        else -> false
    }

    override fun defineTypes(target: MutableMap<Int, IotaType<*>>) {
        target.define(
            TEXT to TextIota.TYPE
        )
    }
}