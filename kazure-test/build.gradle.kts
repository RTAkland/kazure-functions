import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.ksp)
    id("cn.rtast.kazure") version "1.3.3-2.4.21"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"
}

repositories {
    mavenLocal()
}

azurefunctions {
    appName = "rtasttest"
}

kotlin {
    jvmToolchain(11)

    explicitApi = ExplicitApiMode.Disabled

//    compilerOptions.freeCompilerArgs.addAll("-Xcontext-parameters")
}

kazure {
    listingResources = false
    excludeFiles.addAll("test/sensitive.txt")
    resourceRoutingPrefix = "test/"
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    destinationDirectory.set(file("$buildDir/classes/java/main"))
//}

tasks.register("killAzureProcesses") {
    description = ""
    doLast {
        println("Killing all processes containing 'azure' in their command line...")
        val proc =
            Runtime.getRuntime().exec(arrayOf("sh", "-c", "ps -ef | grep azure | grep -v grep | awk '{print \$2}'"))
        val output = proc.inputStream.bufferedReader().readText().trim()
        if (output.isNotEmpty()) output.lines().forEach { pid ->
            println("Killing PID: $pid")
            ProcessBuilder().command("kill", "-9", pid).start().waitFor()
        } else println("No azure-related processes found.")
    }
}

tasks.named("azureFunctionsRun") {
    val enableKillAzureProcess = System.getenv("ENABLE_KILL_AZURE_PROCESS") != null
    if (enableKillAzureProcess) finalizedBy("killAzureProcesses") else println("Killing azure core tools process is disabled")
}