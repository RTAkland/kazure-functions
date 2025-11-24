/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package cn.rtast.kazure.compiler.util.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.FqName

fun IrClass.isSubclassOf(targetInterfaceFqName: FqName): Boolean {
    return this.superTypes.any { superType ->
        val superFqName = superType.classFqName?.asString()
        if (superFqName == targetInterfaceFqName.asString()) {
            true
        } else {
            val superClass = superType.classifierOrNull?.owner as? IrClass
            superClass?.isSubclassOf(targetInterfaceFqName) ?: false
        }
    }
}

fun FqName.refCls(ctx: IrPluginContext) = ctx.referenceClass(this.classId)!!

fun IrClassReference.isSubclassOf(fqName: FqName): Boolean {
    return this.symbol.superTypes().any { it.classFqName == fqName }
}