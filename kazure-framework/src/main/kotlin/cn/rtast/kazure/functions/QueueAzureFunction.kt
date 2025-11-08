/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext

public abstract class QueueAzureFunction : AzureFunction {
    public abstract fun queueEntrypoint(message: String, context: HttpContext)
}
