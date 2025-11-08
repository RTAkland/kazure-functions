/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


@file:OptIn(IrImplementationDetail::class, DelicateIrParameterIndexSetter::class,
    DeprecatedForRemovalCompilerApi::class
)

package cn.rtast.kazure.compiler.util

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrImplementationDetail
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.DelicateIrParameterIndexSetter
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.impl.IrValueParameterImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl

fun String.irString(irBuiltIn: IrBuiltIns) = IrConstImpl.string(
    UNDEFINED_OFFSET, UNDEFINED_OFFSET, irBuiltIn.stringType.type, this
)

fun List<IrValueParameter>.getByName(name: String): IrValueParameter? {
    val index = this.indexOfFirst { it.name.asString() == name }
    if (index == -1) return null
    return this[index]
}

fun copyValueParameter(
    original: IrValueParameter,
    newParent: IrSimpleFunction,
    newIndex: Int,
    factory: IrFactory,
): IrValueParameter {
    val newSymbol = IrValueParameterSymbolImpl()
    val copy = IrValueParameterImpl(
        startOffset = original.startOffset,
        endOffset = original.endOffset,
        origin = IrDeclarationOrigin.DEFINED,
        symbol = newSymbol,
        name = original.name,
        type = original.type,
        varargElementType = original.varargElementType,
        isCrossinline = original.isCrossinline,
        isNoinline = original.isNoinline,
        factory = factory,
        isAssignable = false,
        isHidden = false,
    ).apply {
        index = newIndex
        parent = newParent
    }
    copy.annotations += original.annotations
//    newSymbol.bind(copy)
    return copy
}