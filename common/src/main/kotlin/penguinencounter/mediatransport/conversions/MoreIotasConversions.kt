package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import org.jblas.DoubleMatrix
import penguinencounter.mediatransport.casting.mishaps.MishapNotSendable
import penguinencounter.mediatransport.config.MediaTransportConfig
import ram.talia.moreiotas.api.casting.iota.MatrixIota
import ram.talia.moreiotas.api.casting.iota.StringIota
import java.io.ByteArrayInputStream
import java.io.EOFException

/**
 * SAFETY: keep usages of `ram.talia.moreiotas` and `org.jblas.jblas` within this class
 */
class MoreIotasConversions : Encoder, Decoder {
    override fun canEncode(it: Iota): Boolean = when (it) {
        is StringIota, is MatrixIota -> true
        else -> false
    }

    @Throws(MishapNotSendable::class)
    override fun encode(
        it: Iota
    ): ByteArray = when (it) {
        is StringIota -> convertString(it)
        is MatrixIota -> convertMatrix(it)
        else -> throw IllegalStateException("catastrophic failure: dispatch; MoreIotasConversions doesn't know what a ${it::class.simpleName ?: "<anonymous?>"} is")
    }

    fun convertString(it: StringIota): ByteArray = buildData {
        val theString = it.string.toByteArray(Charsets.UTF_8)
        writeByte(Types.STRING)
        writeInt(theString.size)
        write(theString)
    }

    fun convertMatrix(it: MatrixIota): ByteArray {
        val data = it.matrix
        if (data.rows * data.columns > MediaTransportConfig.server.matrixMaxArea)
            throw MishapNotSendable.reason(it, "matrix_too_big")
        if (data.rows > 0xff)
            throw MishapNotSendable.reason(it, "matrix_too_many_rows")
        if (data.columns > 0xff)
            throw MishapNotSendable.reason(it, "matrix_too_many_cols")

        if (data.rows < 0 || data.columns < 0)
            throw MishapNotSendable.reason(it, "corrupt")

        return buildData {
            writeByte(Types.MATRIX)
            writeByte(data.rows)
            writeByte(data.columns)
            // send rows in order
            for (i in 0 ..< data.rows) {
                for (j in 0 ..< data.columns) {
                    writeDouble(data[i, j])
                }
            }
        }
    }

    fun readString(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        val size = readInt()
        try {
            val bytes = readNBytes(size)
            if (bytes.size != size) GarbageIota()
            else StringIota.make(String(bytes, Charsets.UTF_8))
        } catch (_: MishapInvalidIota) {
            GarbageIota()
        }
    }

    fun readMatrix(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        val rows = read()
        val cols = read()
        if (rows == -1 || cols == -1) return@unpackData GarbageIota()
        if (rows * cols > MediaTransportConfig.server.matrixMaxArea) GarbageIota()
        else {
            val mat = DoubleMatrix(rows, cols)
            for (i in 0 ..< rows) {
                for (j in 0 ..< cols) {
                    mat.put(i, j, readDouble())
                }
            }
            MatrixIota(mat)
        }
    }

    override fun decode(
        type: Int,
        bytes: ByteArrayInputStream
    ): Iota {
        return try {
            when (type) {
                Types.STRING -> readString(bytes)
                Types.MATRIX -> readMatrix(bytes)
                else -> GarbageIota()
            }
        } catch (_: EOFException) {
            GarbageIota()
        }
    }

    override fun canDecode(type: Int): Boolean = when (type) {
        Types.STRING, Types.MATRIX -> true
        else -> false
    }
}