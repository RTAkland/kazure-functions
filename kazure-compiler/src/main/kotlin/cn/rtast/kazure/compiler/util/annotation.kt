/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.util

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrAnnotation
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrAnnotationImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.FqName

fun IrFunction.hasAnnotationByFqName(fqName: FqName): Boolean =
    this.annotations.any { it.classSymbol.owner.parentAsClass.fqNameWhenAvailable == fqName }

fun IrFunction.getAnnotationByFqName(fqName: FqName): IrConstructorCall? =
    this.annotations.firstOrNull { it.classSymbol.owner.fqNameWhenAvailable == fqName }

fun IrFunction.filterAnnotations(fqName: FqName): List<IrConstructorCall> =
    this.annotations.filter { it.classSymbol.owner.fqNameWhenAvailable == fqName }

fun IrConstructorCall.getParameterValue(paramName: String): IrExpression? {
    val constructor = this.symbol.owner
    val index = constructor.valueParameters.indexOfFirst { it.name.asString() == paramName }
    if (index == -1) return null
    return this.arguments[index]
}

fun IrFunction.removeAnnotation(fqName: FqName) {
    annotations -= annotations.filter { call -> call.type.classFqName == fqName }.toTypedArray()
}

fun IrValueParameter.addAnnotation(context: IrPluginContext, fqName: FqName, block: IrAnnotation.() -> Unit) {
    val annCls = context.finderForBuiltins().findClass(fqName.classId)!!
    val annCon = annCls.constructors.first()
    block(IrAnnotationImpl(this.startOffset, this.endOffset, annCls.defaultType, annCon, 0, 0))
}