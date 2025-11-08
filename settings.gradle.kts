rootProject.name = "kazure"

include(":kazure-compiler")
include(":kazure-framework")
include(":kazure-plugin")
//include(":kazure-processor")
include(":kazure-test")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}