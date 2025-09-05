package penguinencounter.mediatransport.conversions

import java.io.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

const val PROTOCOL_VERSION = 0x01

object Types {

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

    // TODO: assign these
    const val MATRIX = 0xd0
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

internal fun ByteArrayInputStream.peek(): Int {
    mark(0)
    val result = read()
    reset()
    return result
}