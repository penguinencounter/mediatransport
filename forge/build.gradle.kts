plugins {
    id("mediatransport.platform")
}

val modId: String by project

architectury {
    forge()
}

loom {
    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig(
            "mediatransport-common.mixins.json",
            "mediatransport-forge.mixins.json",
        )
    }
}

mediatransportModDependencies {
    // expand versions in mods.toml
    filesMatching.add("META-INF/mods.toml")

    // transform Gradle version ranges into a Forge-compatible format
    anyVersion = ""
    mapVersions {
        replace(Regex("""\](\S+)"""), "($1")
        replace(Regex("""(\S+)\["""), "$1)")
    }

    // CurseForge/Modrinth mod dependency metadata
    requires("architectury-api")
    requires("cloth-config")
    requires(curseforge = "hexcasting", modrinth = "hex-casting")
    requires("kotlin-for-forge")
    optional("moreiotas")
    optional(curseforge=null, modrinth="hexpose")
}

dependencies {
    forge(libs.forge)
    modApi(libs.architectury.forge)

    implementation(libs.kotlin.forge)

    modApi(libs.figura.forge) { isTransitive = false }
    modApi(libs.hexcasting.forge) { isTransitive = false }
    modImplementation(libs.patchouli.forge)

    forgeRuntimeLibrary(libs.figura.luaj.core)
    forgeRuntimeLibrary(libs.figura.luaj.jse)
    forgeRuntimeLibrary(libs.nvwebsocketclient)
//    forgeRuntimeLibrary(libs.jblas)
    forgeRuntimeLibrary(libs.oggus)
    forgeRuntimeLibrary(libs.concentus)

    // Hex Casting dependencies
    // we use modLocalRuntime to add these to the development runtime, but not at compile time or for consumers of this project
    // but we use PAUCAL for datagen, so that's part of the actual implementation
    modImplementation(libs.paucal.forge)
    modLocalRuntime(libs.caelus)
    modLocalRuntime(libs.moreiotas.forge)
    modLocalRuntime(libs.inline.forge) { isTransitive = false }

    modApi(libs.clothConfig.forge)

    libs.mixinExtras.common.also {
        compileOnly(it)
        annotationProcessor(it)
    }

    libs.mixinExtras.forge.also {
        localRuntime(it)
        include(it)
    }
}

tasks {
    shadowJar {
        exclude("fabric.mod.json")
    }
}
