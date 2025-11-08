import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.ksp.symbol.processor.api)
    compileOnly(project(":kazure-framework"))
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}