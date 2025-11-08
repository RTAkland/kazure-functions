/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class, DeprecatedForRemovalCompilerApi::class)

package cn.rtast.kazure.compiler.ir

import cn.rtast.kazure.compiler.*
import cn.rtast.kazure.compiler.util.*
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.toArrayOrPrimitiveArrayType
import org.jetbrains.kotlin.name.FqName
import java.util.*

class KAzureIrTransformer(
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    private val configuration: CompilerConfiguration,
) : IrElementTransformerVoidWithContext() {

    private val irBuiltIns = pluginContext.irBuiltIns

    private fun generateDefaultEnumIrExpression(fqName: FqName): IrExpression {
        val authEnumClass = pluginContext.referenceClass(fqName.classId)!!
        val entrySymbol = authEnumClass.owner.declarations
            .filterIsInstance<IrEnumEntry>()
            .first { it.name.asString() == "ANONYMOUS" }
            .symbol
        return IrGetEnumValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, authEnumClass.defaultType, entrySymbol)
    }

    private fun generateDefaultHttpMethodsIrExpression(): IrExpression {
        val httpMethodsClass = pluginContext.referenceClass(HttpMethodFqName.classId)!!
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

    ///////////////////////////////////////////////////////

    fun handleHttpFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        val path = (annotation.getParameterValue("path") as IrConstImpl).value as String
        val methodsValue = annotation.getParameterValue("methods")
            ?: generateDefaultHttpMethodsIrExpression()
        val authLevelValue = annotation.getParameterValue("authLevel")
            ?: generateDefaultEnumIrExpression(AuthorizationLevelFqName)
        val requestParam = func.valueParameters[0]

        // add @HttpTrigger into request param
        requestParam.addAnnotation(pluginContext, HttpTriggerFqName) {
            putValueArgument(0, "req".irString(irBuiltIns))
            putValueArgument(2, path.irString(irBuiltIns))
            putValueArgument(3, methodsValue)
            putValueArgument(4, authLevelValue)
            requestParam.annotations += this
        }
    }

    private fun handleBlobFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        val contentParam = func.valueParameters[0]
        // add @BlobTrigger annotation
        val path = annotation.getParameterValue("path")!!
        val connection = annotation.getParameterValue("connection") ?: "".irString(irBuiltIns)
        val source = annotation.getParameterValue("source") ?: "".irString(irBuiltIns)
        contentParam.addAnnotation(pluginContext, BlobTriggerFqName) {
            putValueArgument(0, "blob".irString(irBuiltIns))
            putValueArgument(2, path)
            putValueArgument(3, connection)
            putValueArgument(4, source)
            contentParam.annotations += this
        }
    }

    private fun handleEventHubFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        val eventParam = func.valueParameters[0]
        // add @EventHubTrigger annotation
        val eventHubName = annotation.getParameterValue("eventHubName")!!
        val connection = annotation.getParameterValue("connection")!!
        val cardinality = annotation.getParameterValue("cardinality")
            ?: generateDefaultEnumIrExpression(CardinalityFqName)
        val consumerGroup = annotation.getParameterValue("consumerGroup")
            ?: $$"$Default".irString(irBuiltIns)
        eventParam.addAnnotation(pluginContext, EventHubTriggerFqName) {
            putValueArgument(0, "eh".irString(irBuiltIns))
            putValueArgument(2, eventHubName)
            putValueArgument(3, cardinality)
            putValueArgument(4, consumerGroup)
            putValueArgument(5, connection)
            eventParam.annotations += this
        }
    }

    private fun handleTimerFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        val schedule = (annotation.getParameterValue("schedule") as IrConstImpl).value as String
        // add @TimerTrigger annotation
        val timerInfoParam = func.valueParameters[0]
        timerInfoParam.addAnnotation(pluginContext, TimerTriggerFqName) {
            putValueArgument(0, "timer".irString(irBuiltIns))
            putValueArgument(2, schedule.irString(irBuiltIns))
            timerInfoParam.annotations += this
        }
    }

    private fun handleQueueFunction(func: IrSimpleFunction, annotation: IrConstructorCall) {
        val messageParam = func.valueParameters[0]
        // add @QueueTrigger annotation
        val queueName = annotation.getParameterValue("queueName")!!
        val connection = annotation.getParameterValue("connection")
            ?: "".irString(irBuiltIns)
        messageParam.addAnnotation(pluginContext, QueueTriggerFqName) {
            putValueArgument(0, "queue".irString(irBuiltIns))
            putValueArgument(2, queueName)
            putValueArgument(3, connection)
            messageParam.annotations += this
        }
    }

    private fun IrSimpleFunction.findAnnotatedRouting(): IrConstructorCall? {
        val http = this.getAnnotation(HttpRoutingFqName)
        if (http != null) return http
        val blob = this.getAnnotation(BlobRoutingFqName)
        if (blob != null) return blob
        val eventHub = this.getAnnotation(EventHubRoutingFqName)
        if (eventHub != null) return eventHub
        val timer = this.getAnnotation(TimerRoutingFqName)
        if (timer != null) return timer
        val queue = this.getAnnotation(QueueRoutingFqName)
        if (queue != null) return queue
        return null
    }

    ///////////////////////////////////////

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (!declaration.isSubclassOf(AzureFunctionFqName)) super.visitClassNew(declaration)
        declaration.functions.forEach { func ->
            val ra = func.findAnnotatedRouting() ?: return super.visitClassNew(declaration)
            addFunctionNameToFunction(pluginContext, func, declaration.name.asString())
            when {
                declaration.isSubclassOf(HttpAzureFunctionFqName) -> handleHttpFunction(func, ra)
                declaration.isSubclassOf(BlobAzureFunctionFqName) -> handleBlobFunction(func, ra)
                declaration.isSubclassOf(EventHubAzureFunctionFqName) -> handleEventHubFunction(func, ra)
                declaration.isSubclassOf(TimerAzureFunctionFqName) -> handleTimerFunction(func, ra)
                declaration.isSubclassOf(QueueAzureFunctionFqName) -> handleQueueFunction(func, ra)
            }

            // remove @HttpRouting annotation from origin function
            func.removeAnnotation(HttpRoutingFqName)
            func.removeAnnotation(BlobRoutingFqName)
            func.removeAnnotation(QueueRoutingFqName)
            func.removeAnnotation(TimerRoutingFqName)
            func.removeAnnotation(EventHubRoutingFqName)
        }
        return super.visitClassNew(declaration)
    }
}