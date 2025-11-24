/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.v2.ir

import cn.rtast.kazure.compiler.*
import cn.rtast.kazure.compiler.util.*
import cn.rtast.kazure.compiler.util.ir.addAnnotation
import cn.rtast.kazure.compiler.util.ir.classId
import cn.rtast.kazure.compiler.util.ir.fqName
import cn.rtast.kazure.compiler.util.ir.irString
import cn.rtast.kazure.compiler.util.ir.name
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.createBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class V2HttpRoutingIrVisitor(
    @Suppress("unused")
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    @Suppress("unused")
    private val configuration: CompilerConfiguration,
    private val containerIrFile: IrFile,
) : IrElementTransformerVoidWithContext() {
    private val irBuiltIns = pluginContext.irBuiltIns

    private val httpResponseCls = pluginContext.referenceClass(ResponseMessageFqName.classId)!!
    private val httpTriggerCls = pluginContext.referenceClass(HttpTriggerFqName.classId)!!
    private val bindingNameCls = pluginContext.referenceClass(BindingNameFqName.classId)!!
    private val httpRequestCls = pluginContext.referenceClass(HttpRequestFqName.classId)!!
    private val httpContextCls = pluginContext.referenceClass(ExecutionContextFqName.classId)!!
    private val functionNameCls = pluginContext.referenceClass(FunctionNameFqName.classId)!!
    private val httpRoutingContextCls = pluginContext.referenceClass(HttpRoutingContextFqName.classId)!!
    private val httpMethodCls = pluginContext.referenceClass(HttpMethodFqName.classId)!!
    private val authLevelCls = pluginContext.referenceClass(AuthorizationLevelFqName.classId)!!

    private val runBlockingCallableId = CallableId("kotlinx.coroutines".fqName, "runBlocking".name)

    private val runBlockingCall = pluginContext.referenceFunctions(runBlockingCallableId).first()

    private val httpTriggerCtor = httpTriggerCls.constructors.single()
    private val bindingNameCtor = bindingNameCls.constructors.single()

    private fun createDefaultMethods(): IrExpression {
        val httpMethodGetEntry = httpMethodCls.owner.declarations
            .filterIsInstance<IrEnumEntry>()
            .first { it.name.asString() == "GET" }
        val element = IrGetEnumValueImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            httpMethodCls.defaultType, httpMethodGetEntry.symbol
        )
        val arrayType = irBuiltIns.arrayClass.typeWith(httpMethodCls.defaultType)
        val arrayExpression = IrVarargImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            arrayType, httpRequestCls.defaultType,
            listOf(element)
        )
        return arrayExpression
    }

    private fun createDefaultAuthLevel(): IrExpression {
        val authLevelAnonymousEntry = authLevelCls.owner.declarations
            .filterIsInstance<IrEnumEntry>()
            .first { it.name.asString() == "ANONYMOUS" }
        val authLevelExpr = IrGetEnumValueImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            authLevelCls.defaultType,
            authLevelAnonymousEntry.symbol
        )
        return authLevelExpr
    }

    private fun createDefaultBindings(): IrExpression {
        val stringArrayType = irBuiltIns.arrayClass.typeWith(irBuiltIns.stringType)
        val emptyStringArrayExpr = IrVarargImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            stringArrayType, irBuiltIns.stringType,
            emptyList()
        )
        return emptyStringArrayExpr
    }


    @OptIn(DeprecatedForRemovalCompilerApi::class)
    private fun generateHttpRouting(irCall: IrCall, genericTType: IrType, extra: V2HttpRoutingIrVisitor.() -> Unit) {
        val routeArg = irCall.arguments[0]
        val methodsArg = irCall.arguments[1] ?: createDefaultAuthLevel()
        val authLevelArg = irCall.arguments[2] ?: createDefaultAuthLevel()
        val bindingsArg = irCall.arguments[3] ?: createDefaultBindings()
        val blockBodyArg = irCall.arguments[4]!!

        val generatedFunctionName = "f_$randomFunctionName"
        val func = pluginContext.irFactory.buildFun {
            name = Name.identifier(generatedFunctionName)
            returnType = httpResponseCls.defaultType
            visibility = DescriptorVisibilities.PUBLIC
            isSuspend = false
        }.apply func_inner@{
            parent = containerIrFile
            this@func_inner.addAnnotation(pluginContext, FunctionNameFqName) {
                putValueArgument(0, generatedFunctionName.irString(irBuiltIns))
            }
        }

        // req param
        val paramReqSymbol = IrValueParameterSymbolImpl()
        val paramReq = pluginContext.irFactory.createValueParameter(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, KAZURE_ORIGIN,
            IrParameterKind.Regular, Name.identifier("req"),
            genericTType, false, paramReqSymbol,
            null, isCrossinline = false, isNoinline = false, isHidden = false
        ).apply param@{
            parent = func
            this@param.addAnnotation(pluginContext, HttpTriggerFqName) {
                putValueArgument(0, "req".irString(irBuiltIns))
                putValueArgument(1, "".irString(irBuiltIns))
                putValueArgument(2, ((routeArg as IrConst).value as String).irString(irBuiltIns))
                putValueArgument(3, methodsArg)
                putValueArgument(4, authLevelArg)
                this@param.annotations += this
            }
        }
//        paramReqSymbol.bind(paramReq)
        func.valueParameters += paramReq

        // ctx param
        val paramCtxSymbol = IrValueParameterSymbolImpl()
        val paramCtx = pluginContext.irFactory.createValueParameter(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, KAZURE_ORIGIN,
            IrParameterKind.Regular, Name.identifier("ctx"),
            httpContextCls.defaultType, false, paramCtxSymbol,
            null, isCrossinline = false, isNoinline = false, isHidden = false
        ).apply { parent = func }
//        paramCtxSymbol.bind(paramCtx)
        func.valueParameters += paramCtx

        val bindingsString = (bindingsArg as IrVararg).elements.map { (it as IrConst).value as String }
        bindingsString.forEach {
            // binding name param
            val bSymbol = IrValueParameterSymbolImpl()
            val b = pluginContext.irFactory.createValueParameter(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET, KAZURE_ORIGIN,
                IrParameterKind.Regular, Name.identifier(it),
                irBuiltIns.stringType, false, bSymbol,
                null, isCrossinline = false, isNoinline = false, isHidden = false
            ).apply param@{
                parent = func
                this@param.addAnnotation(pluginContext, BindingNameFqName) {
                    putValueArgument(0, it.irString(irBuiltIns))
                    this@param.annotations += this
                }
            }
//            bSymbol.bind(b)
            func.valueParameters += b
        }

//        val runBlockingCall = IrCallImpl(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//            httpResponseCls.defaultType,
//            runBlockingCall
//        ).apply { putValueArgument(0, blockBodyArg) }

        val returnExpr = IrReturnImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            httpResponseCls.defaultType, func.symbol,
            blockBodyArg
        )

        func.body = pluginContext.irFactory.createBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET, listOf(returnExpr))

        containerIrFile.declarations += func
        extra()
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val symbol = expression.symbol.owner
        if (symbol.fqNameWhenAvailable?.asString() == "cn.rtast.kazure.v2.createRouting") {
            val genericTType = expression.typeArguments[0] ?: pluginContext.irBuiltIns.anyType
            generateHttpRouting(expression, genericTType) {}
        }
        return super.visitCall(expression)
    }
}