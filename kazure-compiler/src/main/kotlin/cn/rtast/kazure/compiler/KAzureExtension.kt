/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

class KAzureExtension(
    private val messageCollector: MessageCollector,
    private val configuration: CompilerConfiguration,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
//        val generatedFunctionsContainerIrFile =
//            moduleFragment.files.find { it.path.endsWith("kazure/v2/generated_functions.kt") }!!
//        moduleFragment.transform(
//            V2HttpRoutingIrVisitor(
//                messageCollector,
//                pluginContext,
//                configuration,
//                generatedFunctionsContainerIrFile
//            ),
//            null
//        )
    }
}