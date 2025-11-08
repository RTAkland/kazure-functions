/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class, DeprecatedForRemovalCompilerApi::class)

package cn.rtast.kazure.compiler.util

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.FqName

fun IrFunction.hasAnnotationByFqName(fqName: FqName): Boolean =
    this.annotations.any { it.symbol.owner.parentAsClass.fqNameWhenAvailable == fqName }

fun IrFunction.getAnnotationByFqName(fqName: FqName): IrConstructorCall? =
    this.annotations.firstOrNull { it.symbol.owner.constructedClass.fqNameWhenAvailable == fqName }

fun IrFunction.filterAnnotations(fqName: FqName): List<IrConstructorCall> =
    this.annotations.filter { it.symbol.owner.constructedClass.fqNameWhenAvailable == fqName }

fun IrConstructorCall.getParameterValue(paramName: String): IrExpression? {
    val constructor = this.symbol.owner
    val index = constructor.valueParameters.indexOfFirst { it.name.asString() == paramName }
    if (index == -1) return null
    return this.getValueArgument(index)
}

fun IrFunction.removeAnnotation(fqName: FqName) {
    try {
        annotations -= annotations.find { call ->
            val classFq = call.symbol.owner.parentAsClass.fqNameWhenAvailable
            classFq == fqName
        }!!
    } catch (_: NullPointerException) {
    }
}

fun IrValueParameter.addAnnotation(context: IrPluginContext, fqName: FqName, block: IrConstructorCallImpl.() -> Unit) {
    val annCls = context.referenceClass(fqName.classId)!!
    val annCon = annCls.constructors.first()
    val annConCallImpl = IrConstructorCallImpl.fromSymbolOwner(
        this.startOffset, this.endOffset,
        annCls.defaultType, annCon
    )
    block(annConCallImpl)
}