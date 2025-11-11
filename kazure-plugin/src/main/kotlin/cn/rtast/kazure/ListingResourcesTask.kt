/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ListingResourcesTask : DefaultTask() {

    @TaskAction
    fun runTask() {
        val config = project.extensions.getByType(KAzureExtension::class.java)
        val resDir = project.layout.projectDirectory.dir("src/main/resources/").asFile
        val excludeList = config.excludeFiles.get()
        val generatedResourcesDir = project.layout.buildDirectory.dir("generated/resources")
        val resFiles = resDir.listFiles()
            ?.filter { file -> !excludeList.contains(file.name) }
            ?: emptyList()
        outputs.dir(generatedResourcesDir)
        val indexFile = File(generatedResourcesDir.get().asFile, "kazure-listing-resources.txt")
        val indexContent = resFiles.joinToString("\n") { it.path }
        indexFile.writeText(indexContent)
    }
}