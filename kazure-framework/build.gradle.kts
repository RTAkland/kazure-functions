dependencies {
    api(libs.azure.functions.java.library)
    api(libs.gson)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}