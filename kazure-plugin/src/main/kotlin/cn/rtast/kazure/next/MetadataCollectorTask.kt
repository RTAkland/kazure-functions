/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/7
 */


package cn.rtast.kazure.next

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

abstract class MetadataCollectorTask : JavaExec() {

    @get:InputFiles
    abstract val runtimeClasspath: ConfigurableFileCollection

    @TaskAction
    override fun exec() {
        mainClass.set("cn.rtast.kazure.next.executable.RouteCollector")
        classpath = runtimeClasspath
        args = listOf()
        super.exec()
    }
}