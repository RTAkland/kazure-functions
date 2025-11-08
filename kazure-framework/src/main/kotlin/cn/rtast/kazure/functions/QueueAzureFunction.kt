/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

public abstract class QueueAzureFunction : AzureFunction {
    final override var __request: HttpRequest? = null
    public abstract fun queueEntrypoint(message: String, context: HttpContext)
}
