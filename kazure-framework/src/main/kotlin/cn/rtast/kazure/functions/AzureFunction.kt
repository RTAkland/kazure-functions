/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */


package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpRequest

public sealed interface AzureFunction {
    @Suppress("PropertyName")
    public var __request: HttpRequest?
}









