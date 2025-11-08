/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure.compiler

import cn.rtast.kazure.compiler.ir.KAzureIrTransformer
import cn.rtast.kazure.compiler.ir.TopLevelFunctionTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class KAzureExtension(
    private val messageCollector: MessageCollector,
    private val configuration: CompilerConfiguration,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.transform(
            KAzureIrTransformer(
                messageCollector,
                pluginContext,
                configuration
            ), null
        )
        moduleFragment.transform(
            TopLevelFunctionTransformer(
                messageCollector,
                pluginContext,
                configuration
            ), null
        )
    }
}