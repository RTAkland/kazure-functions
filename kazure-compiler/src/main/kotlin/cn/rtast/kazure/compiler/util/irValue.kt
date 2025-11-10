/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


@file:OptIn(
    IrImplementationDetail::class, DelicateIrParameterIndexSetter::class,
)

package cn.rtast.kazure.compiler.util

import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrImplementationDetail
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.DelicateIrParameterIndexSetter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl

fun String.irString(irBuiltIn: IrBuiltIns) = IrConstImpl.string(
    UNDEFINED_OFFSET, UNDEFINED_OFFSET, irBuiltIn.stringType.type, this
)

fun List<IrValueParameter>.getByName(name: String): IrValueParameter? {
    val index = this.indexOfFirst { it.name.asString() == name }
    if (index == -1) return null
    return this[index]
}