/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.ir

import cn.rtast.kazure.compiler.*
import cn.rtast.kazure.compiler.util.*
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.toArrayOrPrimitiveArrayType
import org.jetbrains.kotlin.name.FqName

class KAzureIrTransformer(
    @Suppress("unused")
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    @Suppress("unused")
    private val configuration: CompilerConfiguration,
    private val processedFuncList: MutableList<String>,
) : IrElementTransformerVoidWithContext() {

    private val irBuiltIns = pluginContext.irBuiltIns
    private val finder = pluginContext.finderForBuiltins()

    private fun generateDefaultEnumIrExpression(fqName: FqName): IrExpression {
        val authEnumClass = finder.findClass(fqName.classId)!!
        val entrySymbol = authEnumClass.owner.declarations
            .filterIsInstance<IrEnumEntry>()
            .first { it.name.asString() == "ANONYMOUS" }
            .symbol
        return IrGetEnumValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, authEnumClass.defaultType, entrySymbol)
    }

    private fun generateDefaultHttpMethodsIrExpression(): IrExpression {
        val httpMethodsClass = finder.findClass(HttpMethodFqName.classId)!!
        val enumEntry = httpMethodsClass.owner.declarations
            .filterIsInstance<IrEnumEntry>()
            .first { it.name.asString() == "GET" }
            .symbol
        val getExpr = IrGetEnumValueImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            httpMethodsClass.defaultType,
            enumEntry
        )
        return IrVarargImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            httpMethodsClass.defaultType.toArrayOrPrimitiveArrayType(pluginContext.irBuiltIns),
            httpMethodsClass.defaultType, listOf(getExpr)
        )
    }

    fun handleHttpFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        if (processedFuncList.any { func.fqNameWhenAvailable?.asString() == it }) return
        addFunctionNameToFunction(pluginContext, func, func.name.asString())
        val path = (annotation.getParameterValue("path") as IrConstImpl).value as String
        val methodsValue = annotation.getParameterValue("methods")
            ?: generateDefaultHttpMethodsIrExpression()
        val authLevelValue = annotation.getParameterValue("authLevel")
            ?: generateDefaultEnumIrExpression(AuthorizationLevelFqName)
        val requestParam = func.valueParameters[0]

        // add @HttpTrigger into request param
        requestParam.addAnnotation(pluginContext, HttpTriggerFqName) {
            arguments[0] = "req".irString(irBuiltIns)
            arguments[1] = "binary".irString(irBuiltIns)
            arguments[2] = path.removePrefix("/").irString(irBuiltIns)
            arguments[3] = methodsValue
            arguments[4] = authLevelValue
            requestParam.annotations += this
        }
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        when {
            declaration.hasAnnotation(HttpRoutingFqName) -> {
                val ann = declaration.getAnnotation(HttpRoutingFqName)!!
                handleHttpFunction(declaration, ann)
            }

            else -> return super.visitFunctionNew(declaration)
        }
        // remove @HttpRouting annotation from origin function
        declaration.removeAnnotation(HttpRoutingFqName)
        declaration.removeAnnotation(BlobRoutingFqName)
        declaration.removeAnnotation(QueueRoutingFqName)
        declaration.removeAnnotation(TimerRoutingFqName)
        declaration.removeAnnotation(EventHubRoutingFqName)
        return super.visitFunctionNew(declaration)
    }
}