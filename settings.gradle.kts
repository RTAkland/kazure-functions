@file:Suppress("UnstableApiUsage")

rootProject.name = "kazure"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

fun includePathBasedSubModule(name: String, path: String? = null) {
    include(name)
    project(name).projectDir = file(path ?: "lib-$name")
}

include(
    ":kazure-compiler",
    ":kazure-runtime",
    ":kazure-plugin",
    ":kazure-processor",
    ":kazure-test"
)

includePathBasedSubModule(":kazure-serialization-protobuf", "kazure-serialization/serialization-protobuf")