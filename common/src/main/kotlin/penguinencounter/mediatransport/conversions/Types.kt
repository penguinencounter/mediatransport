package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import java.io.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

const val PROTOCOL_VERSION = 0x01

object Types {
    val types: MutableMap<Int, IotaType<*>> = mutableMapOf()

    const val GARBAGE = 0xff
    val garbageData = byteArrayOf(GARBAGE.toByte())

    // technically moreiotas but it's pretty important
    const val STRING = 0x01

    // base hex
    const val BOOLEAN_TRUE = 0x02
    val trueData = byteArrayOf(BOOLEAN_TRUE.toByte())
    const val BOOLEAN_FALSE = 0x03
    val falseData = byteArrayOf(BOOLEAN_FALSE.toByte())
    const val NULL = 0x04
    val nullData = byteArrayOf(NULL.toByte())

    const val DOUBLE = 0x05

    const val PATTERN = 0x06 // hexxy

    const val VEC3 = 0x07

    const val LIST = 0x08 // octxxy?

    // moreiotas x4_
    const val MATRIX = 0x40

    // hexpose x5_
    const val TEXT = 0x50
}

@OptIn(ExperimentalContracts::class)
inline fun buildData(func: DataOutputStream.() -> Unit): ByteArray {
    contract {
        callsInPlace(func, InvocationKind.AT_MOST_ONCE)
    }

    ByteArrayOutputStream().use { baos ->
        DataOutputStream(baos).use { dos ->
            dos.func()
        }
        return baos.toByteArray()
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <R> unpackData(data: ByteArrayInputStream, func: DataInputStream.() -> R): R {
    contract {
        callsInPlace(func, InvocationKind.AT_MOST_ONCE)
    }

    return DataInputStream(data).use { dis ->
        dis.func()
    }
}

internal fun misdispatched(myself: KClass<*>, theThing: Iota): Nothing {
    throw IllegalStateException("catastrophic failure: ${myself.simpleName ?: "<anonymous?>"} doesn't know what a ${theThing::class.simpleName ?: "<anonymous?>"} is")
}