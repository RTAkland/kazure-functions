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
    group = "kazure"
    version = libVersion

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    kotlin {
        explicitApi()
    }

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
