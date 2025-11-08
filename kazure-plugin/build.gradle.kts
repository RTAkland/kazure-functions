import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.build.config)
    alias(libs.plugins.java.gradle.plugin)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(project(":kazure-framework"))
    implementation(libs.kotlin.gradle.plugin)
}


buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
    val pluginProject = project(":kazure-compiler")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${pluginProject.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${pluginProject.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${pluginProject.version}\"")
}

gradlePlugin {
    plugins {
        create("KAzure") {
            id = "kazure"
            displayName = "KAzure"
            description = "Kotlin azure functions generator"
            implementationClass = "cn.rtast.kazure.KAzureGradlePlugin"
        }
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}