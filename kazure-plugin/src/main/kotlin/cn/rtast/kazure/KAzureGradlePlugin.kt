/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

@file:Suppress("unused")

package cn.rtast.kazure

import cn.rtast.kazure.kembed.plugin.GenerateResourceTask
import cn.rtast.kazure.kembed.plugin.KEmbeddableResourcesExtension
import kazure.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KAzureGradlePlugin : KotlinCompilerPluginSupportPlugin {

    private fun registerTasks(project: Project) {
        val generateResourceTask =
            project.tasks.register("generateRoutingResources", ListingResourcesTask::class.java) { it.group = "kazure" }
        project.tasks.named("azureFunctionsPackage").configure { it.dependsOn(generateResourceTask.get().name) }
        val routingOutputDir = project.layout.buildDirectory.dir("generated/kotlin/kazure")
        val resourceOutputDir = project.layout.buildDirectory.dir("generated/kotlin/kembed")
        project.extensions.configure(KotlinProjectExtension::class.java) {
            val sourceSets = it.sourceSets
            sourceSets.getByName("main").kotlin.srcDir(routingOutputDir)
            sourceSets.getByName("main").kotlin.srcDir(resourceOutputDir)
        }
        project.tasks.register("generateResources", GenerateResourceTask::class.java) { it.group = "kazure" }
    }

    private fun registerExtension(project: Project) {
        project.extensions.create("kazure", KAzureExtension::class.java)
        project.extensions.create("kembeddable", KEmbeddableResourcesExtension::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { listOf() }
    }

    override fun apply(target: Project) {
        super.apply(target)
        target.dependencies.add("api", "kazure:kazure-framework:${BuildConfig.KOTLIN_PLUGIN_VERSION}")
        target.pluginManager.apply("com.microsoft.azure.azurefunctions")
        target.pluginManager.apply("com.google.devtools.ksp")
        target.dependencies.add("ksp", "kazure:kazure-processor:${BuildConfig.KOTLIN_PLUGIN_VERSION}")
        registerExtension(target)
        registerTasks(target)
    }

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
        artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
        version = BuildConfig.KOTLIN_PLUGIN_VERSION,
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}