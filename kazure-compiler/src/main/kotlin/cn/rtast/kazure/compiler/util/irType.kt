/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/10/25
 */


package cn.rtast.kazure.compiler.util

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.name.FqName

fun FqName.irType(ctx: IrPluginContext): IrSimpleType {
    return ctx.referenceClass(this.classId)!!.starProjectedType
}