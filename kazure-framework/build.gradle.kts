plugins {
    alias(libs.plugins.kotlinx.serilization)
}

dependencies {
    api(libs.azure.functions.java.library)
    api(libs.kotlinx.serialization.core)
    api(libs.kotlinx.serialization.json)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {

}