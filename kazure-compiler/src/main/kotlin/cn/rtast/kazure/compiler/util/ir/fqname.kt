/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure.compiler.util.ir

import org.jetbrains.kotlin.name.FqName


val String?.fqName
    get(): FqName =
        FqName(this.toString())