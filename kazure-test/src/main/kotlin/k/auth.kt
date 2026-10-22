/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package k

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.credentials.HeaderCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import cn.rtast.kazure.auth.provider.HeaderAuthorizationProvider

object Basic1AuthProvider : BasicAuthorizationProvider() {
    context(request: HttpRequest, context: HttpContext, credential: BasicCredential)
    override fun verify(): Boolean = true
}

object Tok : HeaderAuthorizationProvider("sec-ch-ua-platform") {
    context(
        request: HttpRequest,
        context: HttpContext,
        credential: HeaderCredential,
    )
    override fun verify(): Boolean  {
        println(credential)
        return true
    }
}