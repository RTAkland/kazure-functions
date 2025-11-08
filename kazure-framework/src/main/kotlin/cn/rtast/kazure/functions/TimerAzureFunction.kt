/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext

public abstract class TimerAzureFunction : AzureFunction {
    public abstract fun timerEntrypoint(timerInfo: String, context: HttpContext)
}