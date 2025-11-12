import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.ksp)
    id("kazure") version "1.2.5-2.2.21"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"
}

azurefunctions {
    appName = "rtasttest"
}

kotlin {
    jvmToolchain(11)

    explicitApi = ExplicitApiMode.Disabled

    compilerOptions {
        freeCompilerArgs.set(listOf("-Xcontext-parameters"))
    }
}

kazure {
    listingResources = true
    excludeFiles.addAll("test/sensitive.txt")
    resourceRoutingPrefix = "test/"
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    destinationDirectory.set(file("$buildDir/classes/java/main"))
//}

tasks.register("killAzureProcesses") {
    doLast {
        println("Killing all processes containing 'azure' in their command line...")
        val proc = Runtime.getRuntime().exec(arrayOf("sh", "-c", "ps -ef | grep azure | grep -v grep | awk '{print \$2}'"))
        val output = proc.inputStream.bufferedReader().readText().trim()
        if (output.isNotEmpty()) {
            output.lines().forEach { pid ->
                println("Killing PID: $pid")
                @Suppress("DEPRECATION")
                Runtime.getRuntime().exec("kill -9 $pid").waitFor()
            }
        } else {
            println("No azure-related processes found.")
        }
    }
}

tasks.named("azureFunctionsRun") {
    finalizedBy("killAzureProcesses")
}
