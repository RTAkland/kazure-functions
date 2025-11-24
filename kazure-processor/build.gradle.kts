import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.ksp.symbol.processor.api)
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    implementation(project(":kazure-framework"))
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