/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.UUID

abstract class ListingResourcesTask : DefaultTask() {

    @TaskAction
    fun runTask() {
        val config = project.extensions.getByType(KAzureExtension::class.java)
        if (!config.listingResources.get()) return
        val resDir = project.layout.projectDirectory.dir("src/main/resources/").asFile
        val excludeList = config.excludeFiles.get()
        val generatedResourcesDir = project.layout.buildDirectory.dir("generated/kotlin/kazure").apply {
            get().asFile.mkdirs()
        }
        val resFiles = resDir.listFiles()
            ?.filter { file -> !excludeList.contains(file.name) }
            ?: emptyList()
        val indexFile = File(generatedResourcesDir.get().asFile, "kazure-listing-resources.kt")
        val indexContent = resFiles.joinToString("\n") {
            val pathWithoutPrefix = it.path.split("/src/main/resources/").last()
            """@cn.rtast.kazure.resources.StaticAssets("$pathWithoutPrefix", "${config.resourceRoutingPrefix.get()}$pathWithoutPrefix")""" +
                    """val as_${UUID.randomUUID().toString().replace("-", "")}: ByteArray by cn.rtast.kazure.resources.staticAssets"""
        }
        indexFile.writeText(indexContent)
    }
}