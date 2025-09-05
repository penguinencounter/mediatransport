package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.phys.Vec3
import java.io.ByteArrayInputStream
import java.io.EOFException

class HexConversions : Encoder, Decoder {
    override fun canEncode(it: Iota): Boolean = when (it) {
        is BooleanIota, is DoubleIota, is ListIota,
        is NullIota, is PatternIota, is Vec3Iota,
        is GarbageIota -> true

        else -> false
    }

    override fun encode(it: Iota): ByteArray = when (it) {
        is BooleanIota -> convertBoolean(it)
        is NullIota -> Types.nullData
        is GarbageIota -> Types.garbageData
        is DoubleIota -> convertDouble(it)
        is PatternIota -> convertPattern(it)
        is Vec3Iota -> convertVec3(it)
        is ListIota -> convertList(it)
        else -> throw IllegalStateException("catastrophic failure: dispatch; HexConversions doesn't know what a ${it::class.simpleName ?: "<anonymous?>"} is")
    }

    fun convertBoolean(it: BooleanIota) = when (it.bool) {
        true -> Types.trueData
        false -> Types.falseData
    }

    fun convertDouble(it: DoubleIota) = buildData {
        writeByte(Types.DOUBLE)
        writeDouble(it.double)
    }

    fun convertPattern(it: PatternIota) = buildData {
        val pattern = it.pattern
        writeByte(Types.PATTERN)
        writeByte(pattern.startDir.ordinal)
        writeInt(pattern.angles.size)
        for (angle in pattern.angles) {
            writeByte(angle.ordinal)
        }
    }

    fun convertVec3(it: Vec3Iota) = buildData {
        writeByte(Types.VEC3)
        val vec3 = it.vec3
        writeDouble(vec3.x)
        writeDouble(vec3.y)
        writeDouble(vec3.z)
    }

    fun convertList(it: ListIota) = buildData {
        writeByte(Types.LIST)
        var size = 0
        var theList = it.list
        // Why did you have to build lists like this petra
        val intermediary = buildData {
            while (theList.nonEmpty) {
                size++
                write(Encoder.encode(theList.car))
                theList = theList.cdr
            }
        }
        writeInt(size)
        write(intermediary)
    }

    override fun canDecode(type: Int): Boolean {
        return when (type) {
            Types.DOUBLE, Types.LIST, Types.PATTERN, Types.VEC3, Types.BOOLEAN_TRUE, Types.BOOLEAN_FALSE, Types.GARBAGE, Types.NULL -> true
            else -> false
        }
    }

    fun readDouble(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        DoubleIota(readDouble())
    }

    fun readVec3(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        Vec3Iota(
            Vec3(
                readDouble(),
                readDouble(),
                readDouble()
            )
        )
    }

    fun readPattern(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        val startDir = HexDir.entries[readByte().toInt()]
        val nAngles = readInt()
        val angles = MutableList(nAngles) {
            HexAngle.entries[readByte().toInt()]
        }

        PatternIota(HexPattern(startDir, angles))
    }

    fun readList(bytes: ByteArrayInputStream): Iota = unpackData(bytes) {
        val size = readInt()
        val iotas = MutableList(size) {
            Decoder.decode(bytes)
        }
        ListIota(iotas)
    }

    override fun decode(type: Int, bytes: ByteArrayInputStream): Iota {
        return try {
            when (type) {
                Types.BOOLEAN_TRUE -> BooleanIota(true)
                Types.BOOLEAN_FALSE -> BooleanIota(false)
                Types.NULL -> NullIota()
                Types.GARBAGE -> GarbageIota()
                Types.DOUBLE -> readDouble(bytes)
                Types.VEC3 -> readVec3(bytes)
                Types.PATTERN -> readPattern(bytes)
                Types.LIST -> readList(bytes)
                else -> GarbageIota()
            }
        } catch (_: EOFException) {
            GarbageIota()
        }
    }
}