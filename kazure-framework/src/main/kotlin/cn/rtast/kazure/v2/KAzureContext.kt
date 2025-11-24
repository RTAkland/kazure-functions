/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2

import cn.rtast.kazure.AuthLevel
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.v2.auth.credential.BaseCredential
import cn.rtast.kazure.v2.auth.provider.BaseAuthProvider

public data class KAzureContext<T>(
    val route: String,
    val methods: List<HttpMethod>,
    val authLevel: AuthLevel,
    val bindings: List<String>,
    val authProvider: BaseAuthProvider<T, BaseCredential>?,
    val block: RoutingContext<T>,
)