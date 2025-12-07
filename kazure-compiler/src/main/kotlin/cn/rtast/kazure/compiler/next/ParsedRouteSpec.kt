/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.compiler.next

import org.jetbrains.kotlin.ir.expressions.IrExpression

data class ParsedRouteSpec(
    val route: String,
    val methods: List<String>,
    val params: List<String>,
    val authFqName: String?,
    val handlerExpression: IrExpression,
)