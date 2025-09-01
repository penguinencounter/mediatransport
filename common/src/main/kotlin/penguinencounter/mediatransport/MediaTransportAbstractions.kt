@file:JvmName("MediaTransportAbstractions")

package penguinencounter.mediatransport

import dev.architectury.injectables.annotations.ExpectPlatform
import penguinencounter.mediatransport.registry.MediaTransportRegistrar

fun initRegistries(vararg registries: MediaTransportRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

@ExpectPlatform
fun <T : Any> initRegistry(registrar: MediaTransportRegistrar<T>) {
    throw AssertionError()
}
