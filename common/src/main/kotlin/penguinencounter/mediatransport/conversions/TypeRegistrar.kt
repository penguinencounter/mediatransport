package penguinencounter.mediatransport.conversions

import at.petrak.hexcasting.api.casting.iota.IotaType

interface TypeRegistrar {
    fun defineTypes(target: MutableMap<Int, IotaType<*>>)

    fun MutableMap<Int, IotaType<*>>.define(vararg tuples: Pair<Int, IotaType<*>>) {
        for (item in tuples) this[item.first] = item.second
    }
}