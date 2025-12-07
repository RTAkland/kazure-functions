import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kapt)
    alias(libs.plugins.build.config)
    alias(libs.plugins.shadow)
}

val annotationsRuntimeClasspath: Configuration by configurations.creating { isTransitive = false }

dependencies {
    compileOnly(libs.kotlin.compiler)
    annotationsRuntimeClasspath(project(":kazure-framework"))
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.auto.service)
    kapt(libs.auto.service)
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveClassifier = ""
    exclude("kotlin/**")
    exclude("org/**")
    mergeServiceFiles()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

tasks.compileJava {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.compileKotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_11
}

tasks.compileTestJava {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.compileTestKotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_11
}