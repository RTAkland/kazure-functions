/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.JwtCredential

public interface JwtAuthorizationProvider<T> : AuthorizationConfigure<T, JwtCredential> {
    override fun verify(request: HttpRequest<T>, context: HttpContext, credential: JwtCredential?): Boolean
}