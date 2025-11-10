/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

public interface AuthorizationConfigure<T, C> {
    public fun verify(request: HttpRequest<T>, context: HttpContext, credential: C?): Boolean
}