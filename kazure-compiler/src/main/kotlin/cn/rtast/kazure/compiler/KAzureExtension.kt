/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler

import cn.rtast.kazure.compiler.ir.KAzureIrTransformer
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

class KAzureExtension(
    private val messageCollector: MessageCollector,
    private val configuration: CompilerConfiguration,
) : IrGenerationExtension {

    @OptIn(DeprecatedForRemovalCompilerApi::class)
    fun extractStringListFromProperty(irProperty: IrProperty): List<String>? {
        val initializer = irProperty.backingField?.initializer?.expression ?: return null
        if (initializer !is IrCall) return null
        val stringList = mutableListOf<String>()
        for (argIndex in 0 until initializer.valueArgumentsCount) {
            val argExpr = initializer.getValueArgument(argIndex) ?: continue
            if (argExpr is IrConst && argExpr.kind == IrConstKind.String) {
                stringList.add(argExpr.value as String)
            } else {
                return null
            }
        }
        return stringList
    }

    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val processedFuncList = mutableListOf<String>()
        moduleFragment.files.forEach { file ->
            file.declarations.filterIsInstance<IrProperty>().forEach { prop ->
                if (prop.name.asString() == "_____________________________________processed_______________________________") {
                    processedFuncList.addAll(extractStringListFromProperty(prop)!!)
                }
            }
        }
        moduleFragment.transform(
            KAzureIrTransformer(
                messageCollector,
                pluginContext,
                configuration,
                processedFuncList
            ), null
        )
    }
}