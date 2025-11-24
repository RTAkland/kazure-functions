/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.compiler.util.ir

import org.jetbrains.kotlin.name.Name

val String.name get() = Name.identifier(this)