/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.compiler.util

import java.util.*

val randomFunctionName
    get() = UUID.randomUUID().toString().replace("-", "")