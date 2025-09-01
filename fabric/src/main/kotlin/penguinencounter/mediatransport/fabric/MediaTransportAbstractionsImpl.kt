@file:JvmName("MediaTransportAbstractionsImpl")

package penguinencounter.mediatransport.fabric

import penguinencounter.mediatransport.registry.MediaTransportRegistrar
import net.minecraft.core.Registry

fun <T : Any> initRegistry(registrar: MediaTransportRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}
