/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential

public interface BasicAuthorizationProvider : AuthorizationConfigure<BasicCredential> {
    override fun verify(request: HttpRequest<*>, context: HttpContext, credential: BasicCredential): Boolean
}