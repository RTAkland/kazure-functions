/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.FileSystems
import java.util.*

abstract class ListingResourcesTask : DefaultTask() {

    @TaskAction
    fun runTask() {
        val config = project.extensions.getByType(KAzureExtension::class.java)
        if (!config.listingResources.get()) return
        val resDir = project.layout.projectDirectory.dir("src/main/resources/").asFile
        if (!resDir.exists()) return
        val excludeList = config.excludeFiles.get()
        val generatedResourcesDir = project.layout.buildDirectory.dir("generated/kotlin/kazure").apply {
            get().asFile.mkdirs()
        }
        val resFiles = resDir.walkTopDown().toList().filter { file -> file.isFile }
        fun relativeResourcePath(file: File): String {
            val rel = resDir.toPath().relativize(file.toPath()).toString()
            return rel.replace(File.separatorChar, '/')
        }

        fun isExcluded(file: File): Boolean {
            val relPath = relativeResourcePath(file)
            val fileName = file.name
            if (excludeList.any { it == relPath || it == fileName }) return true
            if (excludeList.any { it.endsWith("/") && relPath.startsWith(it.removeSuffix("/")) }) return true
            for (pattern in excludeList) {
                when {
                    pattern.startsWith("glob:") || pattern.startsWith("regex:") -> {
                        val matcher = FileSystems.getDefault().getPathMatcher(pattern)
                        if (matcher.matches(file.toPath())) return true
                    }

                    pattern.startsWith("startsWith:") -> {
                        val p = pattern.removePrefix("startsWith:").trimStart('/')
                        if (relPath.startsWith(p)) return true
                    }
                }
            }
            return false
        }

        val indexFile = File(generatedResourcesDir.get().asFile, "kazure-listing-resources.kt")
        val indexContent = resFiles
            .filter { file -> !isExcluded(file) }
            .joinToString("\n") { file ->
                val pathWithoutPrefix = relativeResourcePath(file)
                """@cn.rtast.kazure.resources.StaticAssets("${pathWithoutPrefix.removePrefix(config.resourceRoutingPrefix.get())}", "$pathWithoutPrefix")""" +
                        """val ___________as_${
                            UUID.randomUUID().toString().replace("-", "").substring(0, 5)
                        }: ByteArray by cn.rtast.kazure.resources.staticAssets"""
            }
        indexFile.writeText("package kazure.generated.a.b.c.d.e.f.g.h.i.j.k\n$indexContent")
    }
}