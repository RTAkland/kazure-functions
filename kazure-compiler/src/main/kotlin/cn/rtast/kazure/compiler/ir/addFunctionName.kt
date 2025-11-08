/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.ir

import cn.rtast.kazure.compiler.FunctionNameFqName
import cn.rtast.kazure.compiler.util.classId
import cn.rtast.kazure.compiler.util.irString
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import java.util.*

fun addFunctionNameToFunction(
    pluginContext: IrPluginContext,
    target: IrSimpleFunction,
    funcName: String,
) {
    val irBuiltIns = pluginContext.irBuiltIns
    // add @FunctionName annotation
    val functionNameAnnotationClass = pluginContext.referenceClass(FunctionNameFqName.classId)!!
    val functionNameConstructor = functionNameAnnotationClass.constructors.firstOrNull()!!
    val functionNameConstructorCallImpl = IrConstructorCallImpl.fromSymbolOwner(
        UNDEFINED_OFFSET, UNDEFINED_OFFSET,
        functionNameAnnotationClass.defaultType,
        functionNameConstructor
    )
    functionNameConstructorCallImpl.arguments[0] =
        "f_${funcName}_${UUID.randomUUID().toString().replace("-", "")}".irString(irBuiltIns)
    target.annotations += functionNameConstructorCallImpl
}