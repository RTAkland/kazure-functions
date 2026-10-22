plugins {
    alias(libs.plugins.kotlinx.serilization)
}

dependencies {
    api(project(":kazure-runtime"))
    api(libs.kotlinx.serialization.protobuf)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
