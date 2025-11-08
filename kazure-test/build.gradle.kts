import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.ksp)
    id("kazure") version "0.0.1"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"
}

azurefunctions {
    appName = "rtasttest"
}

kotlin {
    jvmToolchain(11)

    explicitApi = ExplicitApiMode.Disabled
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    destinationDirectory.set(file("$buildDir/classes/java/main"))
//}
