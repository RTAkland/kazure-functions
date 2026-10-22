/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(ExperimentalCompilerApi::class)

package cn.rtast.kazure.compiler

import cn.rtast.kazure.BuildConfig
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.MessageCollectorAccess

@AutoService(CompilerPluginRegistrar::class)
class KAzureCompilerRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID
    override val supportsK2: Boolean = true

    @OptIn(MessageCollectorAccess::class)
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration[CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE]
        IrGenerationExtension.registerExtension(KAzureExtension(messageCollector, configuration))
    }
}