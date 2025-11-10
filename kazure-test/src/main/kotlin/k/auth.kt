/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package k

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider

 object Basic1AuthProvider : BasicAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BasicCredential,
    ): Boolean {
        return credential.username == "RTAkland"
    }
}
