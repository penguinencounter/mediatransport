plugins {
    id("mediatransport.minecraft")
}

architectury {
    common("fabric", "forge")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(kotlin("reflect"))

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    modApi(libs.architectury)

    modApi(libs.hexcasting.common)
    modCompileOnly(libs.moreiotas.common)
    modCompileOnly(libs.hexpose)
    modCompileOnly(libs.patchouli.xplat)
    compileOnly(libs.jblas)

    modApi(libs.figura.common)
    compileOnly(libs.figura.server) // provided by flavored figura

    modApi(libs.clothConfig.common)

    api(libs.figura.luaj.core)
    api(libs.figura.luaj.jse)
    localRuntime(libs.nvwebsocketclient)

    libs.mixinExtras.common.also {
        implementation(it)
        annotationProcessor(it)
    }
}
