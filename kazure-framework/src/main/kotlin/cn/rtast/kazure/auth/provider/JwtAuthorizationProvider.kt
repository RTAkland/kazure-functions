/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.JwtCredential

@Deprecated("Not Impl", level = DeprecationLevel.HIDDEN)
public interface JwtAuthorizationProvider : AuthorizationConfigure<JwtCredential> {
    override fun verify(request: HttpRequest<*>, context: HttpContext, credential: JwtCredential): Boolean
}