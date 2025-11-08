dependencies {
    api(libs.azure.functions.java.library)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}