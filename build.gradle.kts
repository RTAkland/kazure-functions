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
    group = "cn.rtast.kazure"
    version = libVersion

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.maven.rtast.cn/releases")
    }
}

subprojects {
    pluginManager.apply("kotlin")
    pluginManager.apply("maven-publish")

    kotlin {
        explicitApi()
        compilerOptions.jvmTarget = JvmTarget.JVM_11
        compilerOptions.freeCompilerArgs.addAll("-Xcompanion-blocks-and-extensions")
    }

    tasks.compileJava {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    java { withSourcesJar() }

    publishing {
        repositories {
            maven("https://repo.rtast.cn/packages") {
                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    val token = providers.gradleProperty("PUBLISH_TOKEN")
                        .orElse(providers.environmentVariable("PUBLISH_TOKEN"))
                        .orNull
                    value = "Bearer $token"
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
    }
}
