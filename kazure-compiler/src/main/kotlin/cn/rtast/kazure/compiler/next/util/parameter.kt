/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/7
 */


package cn.rtast.kazure.compiler.next.util

import org.jetbrains.kotlin.ir.declarations.IrValueParameter

fun IrValueParameter.removeAnnotations(): IrValueParameter = this.apply { this.annotations = listOf() }