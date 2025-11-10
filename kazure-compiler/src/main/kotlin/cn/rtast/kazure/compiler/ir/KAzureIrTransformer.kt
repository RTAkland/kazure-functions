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
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irNot
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addTypeParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.createBlockBody
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KAzureIrTransformer(
    @Suppress("unused")
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    @Suppress("unused")
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
        if (!func.hasAnnotation(KspTaggedRoutingFqName)) return
        addFunctionNameToFunction(pluginContext, func, func.name.asString())
        val path = (annotation.getParameterValue("path") as IrConstImpl).value as String
        val methodsValue = annotation.getParameterValue("methods")
            ?: generateDefaultHttpMethodsIrExpression()
        val authLevelValue = annotation.getParameterValue("authLevel")
            ?: generateDefaultEnumIrExpression(AuthorizationLevelFqName)
        val requestParam = func.valueParameters[0]

        func.getAnnotation(AuthConsumerFqName)?.let { authProviderAnnotation ->
            val irBuilder = pluginContext.irBuiltIns.createIrBuilder(func.symbol)
            val providerRef = (authProviderAnnotation.getParameterValue("provider") as
                    IrClassReferenceImpl)
            val providerSymbol = providerRef.symbol as IrClassSymbol
            val verifyFn = providerSymbol.owner.functions
                .first { it.name.asString() == "verify" && it.valueParameters.size == 3 }
            val providerExpr = IrGetObjectValueImpl(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                providerRef.type,
                providerSymbol
            )
            val verifyFnCall = IrCallImpl(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                verifyFn.returnType, verifyFn.symbol,
            ).apply {
                dispatchReceiver = providerExpr
                putValueArgument(0, IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, func.valueParameters[0].symbol))
                putValueArgument(1, IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, func.valueParameters[1].symbol))
                // set cred
                val callableId = when {
                    providerRef.isSubclassOf(BasicAuthorizationProviderFqName) -> {
                        CallableId(
                            "cn.rtast.kazure.auth.provider.func".fqName,
                            null, Name.identifier("__getBasicCredential")
                        )
                    }

                    providerRef.isSubclassOf(BearerAuthorizationProviderFqName) -> {
                        CallableId(
                            "cn.rtast.kazure.auth.provider.func".fqName,
                            null, Name.identifier("__getBearerCredential")
                        )
                    }

                    providerRef.isSubclassOf(JwtAuthorizationProviderFqName) -> {
                        CallableId(
                            "cn.rtast.kazure.auth.provider.func".fqName,
                            null, Name.identifier("__getJwtCredential")
                        )
                    }

                    else -> throw IllegalStateException("Unknown authorization provider type")
                }
                val refFuncSymbol = pluginContext.referenceFunctions(callableId).first()

                val getCredCall =
                    IrCallImpl(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET,
                        refFuncSymbol.owner.returnType,
                        refFuncSymbol
                    ).apply {
                        putValueArgument(
                            0,
                            IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, func.valueParameters[0].symbol)
                        )
                    }
                putValueArgument(2, getCredCall)
            }

            val requestGet = IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, func.valueParameters[0].symbol)
            val unAuthorizedCallableId = CallableId(
                "cn.rtast.kazure.auth.provider.func".fqName,
                null, Name.identifier("__unAuthorized")
            )
            val unAuthFuncSymbol = pluginContext.referenceFunctions(unAuthorizedCallableId).first()
            val unAuthCall = IrCallImpl(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                unAuthFuncSymbol.owner.returnType,
                unAuthFuncSymbol
            ).apply { putValueArgument(0, requestGet) }
            val returnStatement = IrReturnImpl(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                unAuthCall.type,
                func.symbol,
                unAuthCall
            )
            val ifStatement = irBuilder.irIfThenElse(
                type = pluginContext.irBuiltIns.unitType,
                condition = irBuilder.irNot(verifyFnCall),
                thenPart = returnStatement,
                elsePart = irBuilder.irGetObjectValue(
                    pluginContext.irBuiltIns.unitType,
                    pluginContext.irBuiltIns.unitClass
                )
            )
            val newBody = pluginContext.irFactory.createBlockBody(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                listOf(ifStatement) + func.body!!.statements
            )
            func.body = newBody
            func.removeAnnotation(AuthConsumerFqName)
        }

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

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        when {
            declaration.hasAnnotation(HttpRoutingFqName) -> {
//                addFunctionNameToFunction(pluginContext, declaration, declaration.name.asString())
                val ann = declaration.getAnnotation(HttpRoutingFqName)!!
                handleHttpFunction(declaration, ann)
            }

            declaration.hasAnnotation(BlobRoutingFqName) -> {
                addFunctionNameToFunction(pluginContext, declaration, declaration.name.asString())
                val ann = declaration.getAnnotation(BlobRoutingFqName)!!
                handleBlobFunction(declaration, ann)
            }

            declaration.hasAnnotation(TimerRoutingFqName) -> {
                addFunctionNameToFunction(pluginContext, declaration, declaration.name.asString())
                val ann = declaration.getAnnotation(TimerRoutingFqName)!!
                handleTimerFunction(declaration, ann)
            }

            declaration.hasAnnotation(QueueRoutingFqName) -> {
                addFunctionNameToFunction(pluginContext, declaration, declaration.name.asString())
                val ann = declaration.getAnnotation(QueueRoutingFqName)!!
                handleQueueFunction(declaration, ann)
            }

            declaration.hasAnnotation(EventHubRoutingFqName) -> {
                addFunctionNameToFunction(pluginContext, declaration, declaration.name.asString())
                val ann = declaration.getAnnotation(EventHubRoutingFqName)!!
                handleEventHubFunction(declaration, ann)
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