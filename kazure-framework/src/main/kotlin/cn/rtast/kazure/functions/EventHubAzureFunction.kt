/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

public abstract class EventHubAzureFunction : AzureFunction {
    final override var __request: HttpRequest? = null
    public abstract fun eventHubEntrypoint(event: String, context: HttpContext)
}