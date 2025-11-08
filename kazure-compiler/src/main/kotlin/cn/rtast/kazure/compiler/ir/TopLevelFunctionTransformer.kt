/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.ir

import cn.rtast.kazure.compiler.HttpRoutingFqName
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.getAnnotation

class TopLevelFunctionTransformer(
    private val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext,
    private val configuration: CompilerConfiguration,
) : IrElementTransformerVoidWithContext() {

    //    private val irBuiltIns = pluginContext.irBuiltIns
    private val commonTransformer = KAzureIrTransformer(messageCollector, pluginContext, configuration)

//    private fun generateDefaultParamsIrExpression(): IrExpression {
//        val pathParamsClass = pluginContext.referenceClass(PathParamFqName.classId)!!
//        return IrVarargImpl(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//            pathParamsClass.defaultType.toArrayOrPrimitiveArrayType(pluginContext.irBuiltIns),
//            pathParamsClass.defaultType, listOf()
//        )
//    }

//    private fun injectBindingName(func: IrSimpleFunction, name: String, type: IrType) {
//        val param = pluginContext.irFactory.createValueParameter(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFINED,
//            IrParameterKind.Regular, Name.identifier(name), type,
//            false, IrValueParameterSymbolImpl(), null, false, isNoinline = false, isHidden = false
//        ).apply { parent = func }
//        val bindingNameAnnoCls = pluginContext.referenceClass(BindingNameFqName.classId)!!
//        val bindingNameAnnoCons = bindingNameAnnoCls.constructors.first()
//        val bindingNameAnnoCall = IrConstructorCallImpl.fromSymbolOwner(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//            bindingNameAnnoCls.defaultType,
//            bindingNameAnnoCons
//        )
//        bindingNameAnnoCall.arguments[0] = name.irString(irBuiltIns)
//        param.annotations += bindingNameAnnoCall


    // DO NOT DO THIS - BUG HERE
//        func.parameters.toList().forEach {
//            if (it.name.asString() == name) {
//                it.annotations += bindingNameAnnoCall
//            } else {
//                func.parameters
//            }
//        }
    // DO NOT DO THIS
//    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        declaration.getAnnotation(HttpRoutingFqName)?.let { ann ->
            addFunctionNameToFunction(
                pluginContext, declaration as IrSimpleFunction,
                declaration.name.asString()
            )
            commonTransformer.handleHttpFunction(declaration, ann)
//            val params = (ann.getParameterValue("params")
//                ?: generateDefaultParamsIrExpression()) as IrVararg
//            params.elements.forEach { ele ->
//                val conCall = (ele as IrConstructorCall)
//                val name = (conCall.getParameterValue("name") as IrConst).value as String
//                val type = (conCall.getParameterValue("type") as IrClassReference).classType
//                injectBindingName(declaration, name, type)
//            }
        }
        return super.visitFunctionNew(declaration)
    }
}