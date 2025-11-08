/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext

public abstract class EventHubAzureFunction : AzureFunction {
    public abstract fun eventHubEntrypoint(event: String, context: HttpContext)
}