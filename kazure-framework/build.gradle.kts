dependencies {
    api(libs.azure.functions.java.library)
    implementation(libs.gson)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation("com.azure:azure-messaging-webpubsub-client:1.0.0-beta.1")
}