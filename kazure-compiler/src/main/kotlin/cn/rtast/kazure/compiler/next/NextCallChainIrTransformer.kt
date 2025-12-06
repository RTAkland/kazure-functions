/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.next

import cn.rtast.kazure.compiler.next.util.randomName
import cn.rtast.kazure.compiler.util.classId
import cn.rtast.kazure.compiler.util.fqName
import cn.rtast.kazure.compiler.util.irString
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name


class NextCallChainIrTransformer(
    @Suppress("unused")
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    @Suppress("unused")
    private val configuration: CompilerConfiguration,
) : IrElementTransformerVoidWithContext() {

    companion object {
        val ROUTING_FQ_NAME = "cn.rtast.kazure.next.annotations.Routing".fqName

        val REQUEST_MESSAGE_FQ_NAME = "com.microsoft.azure.functions.HttpRequestMessage".fqName
        val EXECUTION_CONTEXT_FQ_NAME = "com.microsoft.azure.functions.ExecutionContext".fqName
        val RESPONSE_MESSAGE_FQ_NAME = "com.microsoft.azure.functions.HttpResponseMessage".fqName
        val BINDING_NAME_FQ_NAME = "com.microsoft.azure.functions.annotation.BindingName".fqName
        val HTTP_STATUS_FQ_NAME = "com.microsoft.azure.functions.HttpStatus".fqName
        val HTTP_METHOD_FQ_NAME = "com.microsoft.azure.functions.HttpMethod".fqName
        val FUNCTION_NAME_FQ_NAME = "com.microsoft.azure.functions.annotation.FunctionName".fqName
        val HTTP_TRIGGER_FQ_NAME = "com.microsoft.azure.functions.annotation.HttpTrigger".fqName
        val AUTHORIZATION_LEVEL_FQ_NAME = "com.microsoft.azure.functions.annotation.AuthorizationLevel".fqName
    }

    private val irBuiltIns = pluginContext.irBuiltIns

    private val functionNameCls = pluginContext.referenceClass(FUNCTION_NAME_FQ_NAME.classId)!!
    private val requestMessageCls = pluginContext.referenceClass(REQUEST_MESSAGE_FQ_NAME.classId)!!
    private val executionContextCls = pluginContext.referenceClass(EXECUTION_CONTEXT_FQ_NAME.classId)!!
    private val responseMessageCls = pluginContext.referenceClass(RESPONSE_MESSAGE_FQ_NAME.classId)!!
    private val bindingNameCls = pluginContext.referenceClass(BINDING_NAME_FQ_NAME.classId)!!
    private val httpStatusCls = pluginContext.referenceClass(HTTP_STATUS_FQ_NAME.classId)!!
    private val httpMethodCls = pluginContext.referenceClass(HTTP_METHOD_FQ_NAME.classId)!!
    private val httpTriggerCls = pluginContext.referenceClass(HTTP_TRIGGER_FQ_NAME.classId)!!
    private val authorizationLevelCls = pluginContext.referenceClass(AUTHORIZATION_LEVEL_FQ_NAME.classId)!!

    private val mapType = irBuiltIns.mapClass.typeWith(irBuiltIns.stringType, irBuiltIns.stringType)
    private val mapOfSymbol = pluginContext.referenceFunctions(
        CallableId("kotlin.collections".fqName, Name.identifier("mapOf"))
    ).single { it.owner.parameters.size == 1 }

    private val pairCls = pluginContext.referenceClass("kotlin.Pair".fqName.classId)!!
    private val pairType = pairCls.defaultType
    private val pairConstructor = pairCls
        .constructors.single { it.owner.parameters.size == 2 }

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        if (declaration.annotations.hasAnnotation(ROUTING_FQ_NAME)) {
            val init = declaration.backingField!!.initializer!!.expression
            val constructorCall = init as IrConstructorCall
            val routeSpec = parseRouteSpec(constructorCall)

        }
        return super.visitPropertyNew(declaration)
    }

    private fun generateAzureFunction(spec: ParsedRouteSpec, p: IrProperty) {
        val genFName = "f_${p.name.asString()}_$randomName"
        val functionNameAnnoCtor = functionNameCls.constructors.first()
        val functionNameAnnoCall = IrConstructorCallImpl.fromSymbolOwner(
            functionNameCls.defaultType, functionNameAnnoCtor.owner.symbol
        ).apply { arguments[0] = genFName.irString(irBuiltIns) }

        val httpTriggerAnnoCtor = httpTriggerCls.constructors.first()
        val httpTriggerAnnoCall = IrConstructorCallImpl.fromSymbolOwner(
            httpTriggerCls.defaultType, httpTriggerAnnoCtor.owner.symbol
        ).apply {
            val handlerGenericType = p.getter!!.returnType
            val handlerGenericTypeIsByteArray = handlerGenericType.classFqName!!.asString() == "kotlin.ByteArray"
            val httpTriggerDataType =
                if (handlerGenericTypeIsByteArray) "binary".irString(irBuiltIns) else "".irString(irBuiltIns)
            arguments[0] = "req".irString(irBuiltIns)
            arguments[1] = httpTriggerDataType
            arguments[2] = spec.route.irString(irBuiltIns)

            val methodElements = spec.methods.map { m ->
                val entry = httpMethodCls.owner.declarations
                    .filterIsInstance<IrEnumEntry>()
                    .first { it.name.identifier == m }
                IrGetEnumValueImpl(
                    UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                    httpMethodCls.defaultType, entry.symbol
                )
            }
            val methods = IrVarargImpl(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                irBuiltIns.arrayClass.typeWith(httpMethodCls.defaultType),
                httpMethodCls.defaultType, methodElements
            )
            arguments[3] = methods

            val authLevelEntry = authorizationLevelCls.owner.declarations
                .filterIsInstance<IrEnumEntry>()
                .first { it.name.identifier == "ANONYMOUS" }
            val authLevelEnumValue = IrGetEnumValueImpl(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                authorizationLevelCls.defaultType,
                authLevelEntry.symbol
            )
            arguments[4] = authLevelEnumValue
        }
        val paramsMap = mutableMapOf<String, IrValueParameter>()
        spec.params.map { param ->
            val bindingNameCtor = bindingNameCls.constructors.first()
            IrConstructorCallImpl.fromSymbolOwner(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                bindingNameCls.defaultType,
                bindingNameCtor.owner.symbol
            ).apply { arguments[0] = param.irString(irBuiltIns) }
            val p = pluginContext.irFactory.createValueParameter(
                UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFINED,
                IrParameterKind.Regular, Name.identifier(param), bindingNameCls.defaultType,
                false, IrValueParameterSymbolImpl(), null, isCrossinline = false, isNoinline = false, isHidden = false
            )
            paramsMap[param] = p
        }

        /**
         * /**
         *  * fun t(
         *  *     @HttpTrigger(
         *  *         name = "req",
         *  *         dataType = "binary",
         *  *         route = "/api/hello",
         *  *         methods = [HttpMethod.GET],
         *  *         authLevel = AuthorizationLevel.ANONYMOUS
         *  *     ) req: HttpRequest<String>,
         *  *     ctx: HttpContext,
         *  *     @BindingName("path") path: String,
         *  *     @BindingName("name") name: String
         *  * ): HttpResponse {
         *  *     return xxx
         *  * }
         *  */
         */

        val requestParameter = pluginContext.irFactory.createValueParameter(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFINED,
            IrParameterKind.Regular, Name.identifier("_request"), requestMessageCls.defaultType,
            false, IrValueParameterSymbolImpl(), null, isCrossinline = false, isNoinline = false, isHidden = false
        ).apply { annotations += httpTriggerAnnoCall }

        // use lower api to create value parameter
//        val requestParam = IrValueParameterImpl(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//            IrDeclarationOrigin.DEFINED, pluginContext.irFactory,
//            Name.identifier("_request"), requestMessageCls.defaultType, false,
//            IrValueParameterSymbolImpl(), null, isCrossinline = false, isNoinline = false, isHidden = false
//        )

        val contextParameter = pluginContext.irFactory.createValueParameter(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFINED,
            IrParameterKind.Regular, Name.identifier("_context"), executionContextCls.defaultType,
            false, IrValueParameterSymbolImpl(), null, isCrossinline = false, isNoinline = false, isHidden = false
        )

        val irFile = p.parent as IrFile

        val generatedFunction = pluginContext.irFactory.createSimpleFunction(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFINED, Name.identifier(genFName),
            DescriptorVisibilities.PUBLIC,
            isInline = false, isExpect = false,
            returnType = responseMessageCls.defaultType,
            modality = Modality.FINAL,
            symbol = IrSimpleFunctionSymbolImpl(),
            isTailrec = false, isSuspend = false, isOperator = false,
            isInfix = false, isExternal = false,
        ).apply {
            parameters = listOf(requestParameter, contextParameter, *paramsMap.values.toTypedArray())
            parent = irFile

            // create body hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhard
            val handlerType = spec.handlerExpression?.type
            val handleFun = handlerType?.classOrNull?.owner
                ?.functions
                ?.first { it.name.asString() == "handle" }
                ?.symbol
            // remap fun signature path variable to map
            body = DeclarationIrBuilder(pluginContext, this@apply.symbol).irBlockBody {

                // param map block
                if (handleFun != null) {
                    +irReturn(irCall(handleFun).apply {
                        dispatchReceiver = spec.handlerExpression
                        arguments[0] = irGet(requestParameter)
                        arguments[1] = irGet(contextParameter)
//                        arguments[2] =  // params map
                    })
                }
            }

        }
    }

    private fun IrBlockBodyBuilder.pair(key: String, value: IrExpression): IrExpression {
        return irCall(pairConstructor).apply {
            arguments[0] = key.irString(irBuiltIns)
            arguments[1] = value
        }
    }

    private fun parseRouteSpec(constructorCall: IrConstructorCall): ParsedRouteSpec {
        val routeArg = (constructorCall.arguments[0]!! as IrConst).value as String

        val methodsArg = constructorCall.arguments[1]!! as IrVararg

        val methods = methodsArg.elements.map { ele ->
            val enumEntry = ele as IrGetEnumValue
            enumEntry.symbol.owner.name.asString()
        }
        val paramsArg = constructorCall.arguments[2]!! as IrVararg

        val params = paramsArg.elements.map { ele -> (ele as IrConst).value as String }
        val authProviderArg = constructorCall.arguments[3]

        val authProviderFqName = (authProviderArg as IrGetObjectValue)
            .symbol.owner.fqNameWhenAvailable!!.asString()

        val handlerArg = constructorCall.arguments[4]
        return ParsedRouteSpec(routeArg, methods, params, authProviderFqName, handlerArg)
    }
}

