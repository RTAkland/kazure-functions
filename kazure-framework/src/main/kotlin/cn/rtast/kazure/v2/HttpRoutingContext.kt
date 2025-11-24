/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

public data class HttpRoutingContext<T>(
    val request: HttpRequest<T>,
    val context: HttpContext,
)