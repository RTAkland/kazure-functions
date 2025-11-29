/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/11/29
 */


package cn.rtast.kazure.kembed.plugin

import cn.rtast.kazure.kembed.clearDirectory
import cn.rtast.kazure.kembed.generateIndex
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GenerateResourceTask : DefaultTask() {

    @TaskAction
    fun generateResources() {
        val settings = project.extensions.getByType(KEmbeddableResourcesExtension::class.java)
        val maxKtFileSize = settings.maxSingleFileSize.get() * 1024
        val outputDir = project.layout.buildDirectory.dir("generated/kotlin/kembed").get().asFile.apply {
            clearDirectory(this)
            mkdirs()
        }
        val compression = settings.compression.get()
        val generatedIndexVisibility = if (settings.publicGeneratedResourceVariable.get()) "public" else "internal"
        val packageName = settings.packageName.get()
        val inputDirs = settings.resourcePath.get()
        inputDirs.generateIndex(outputDir, packageName, generatedIndexVisibility, maxKtFileSize, compression)
    }
}