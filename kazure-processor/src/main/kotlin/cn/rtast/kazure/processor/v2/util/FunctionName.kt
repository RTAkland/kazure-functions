/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.processor.v2.util

import java.util.UUID

val randomFunctionName get() = "f_${UUID.randomUUID().toString().replace("-", "")}"