import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.build.config)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.azure)
    alias(libs.plugins.maven.publish)
}

val libVersion: String by extra

allprojects {
    group = "kazure.next"
    version = libVersion

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.maven.rtast.cn/releases")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    kotlin {
        explicitApi()
        compilerOptions.jvmTarget = JvmTarget.JVM_11
    }

    tasks.compileJava {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    java { withSourcesJar() }

    publishing {
        repositories {
            mavenLocal()
            maven("https://repo.maven.rtast.cn/releases") {
                name = "RTAST"
                credentials {
                    username = "RTAkland"
                    password = System.getenv("PUBLISH_TOKEN")
                }
            }
        }
    }
}
