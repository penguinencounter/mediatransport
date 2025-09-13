package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.phys.Vec3
import penguinencounter.mediatransport.conversions.Types.BOOLEAN_FALSE
import penguinencounter.mediatransport.conversions.Types.BOOLEAN_TRUE
import penguinencounter.mediatransport.conversions.Types.DOUBLE
import penguinencounter.mediatransport.conversions.Types.GARBAGE
import penguinencounter.mediatransport.conversions.Types.LIST
import penguinencounter.mediatransport.conversions.Types.NULL
import penguinencounter.mediatransport.conversions.Types.PATTERN
import penguinencounter.mediatransport.conversions.Types.VEC3
import penguinencounter.mediatransport.conversions.Types.falseData
import penguinencounter.mediatransport.conversions.Types.garbageData
import penguinencounter.mediatransport.conversions.Types.nullData
import penguinencounter.mediatransport.conversions.Types.trueData
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
        is NullIota -> nullData
        is GarbageIota -> garbageData
        is DoubleIota -> convertDouble(it)
        is PatternIota -> convertPattern(it)
        is Vec3Iota -> convertVec3(it)
        is ListIota -> convertList(it)
        else -> misdispatched(this::class, it)
    }

    fun convertBoolean(it: BooleanIota) = when (it.bool) {
        true -> trueData
        false -> falseData
    }

    fun convertDouble(it: DoubleIota) = buildData {
        writeByte(DOUBLE)
        writeDouble(it.double)
    }

    fun convertPattern(it: PatternIota) = buildData {
        val pattern = it.pattern
        writeByte(PATTERN)
        writeByte(pattern.startDir.ordinal)
        writeInt(pattern.angles.size)
        for (angle in pattern.angles) {
            writeByte(angle.ordinal)
        }
    }

    fun convertVec3(it: Vec3Iota) = buildData {
        writeByte(VEC3)
        val vec3 = it.vec3
        writeDouble(vec3.x)
        writeDouble(vec3.y)
        writeDouble(vec3.z)
    }

    fun convertList(it: ListIota) = buildData {
        writeByte(LIST)
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
            DOUBLE, LIST, PATTERN, VEC3, BOOLEAN_TRUE, BOOLEAN_FALSE, GARBAGE, NULL -> true
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
                BOOLEAN_TRUE -> BooleanIota(true)
                BOOLEAN_FALSE -> BooleanIota(false)
                NULL -> NullIota()
                GARBAGE -> GarbageIota()
                DOUBLE -> readDouble(bytes)
                VEC3 -> readVec3(bytes)
                PATTERN -> readPattern(bytes)
                LIST -> readList(bytes)
                else -> GarbageIota()
            }
        } catch (_: EOFException) {
            GarbageIota()
        }
    }

    override fun defineTypes(target: MutableMap<Int, IotaType<*>>) {
        target.define(
            BOOLEAN_TRUE to BooleanIota.TYPE,
            BOOLEAN_FALSE to BooleanIota.TYPE,
            NULL to NullIota.TYPE,
            GARBAGE to GarbageIota.TYPE,
            DOUBLE to DoubleIota.TYPE,
            VEC3 to Vec3Iota.TYPE,
            PATTERN to PatternIota.TYPE,
            LIST to ListIota.TYPE
        )
    }
}