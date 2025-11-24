/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */

package cn.rtast.kazure.v2

import cn.rtast.kazure.AuthLevel
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.v2.auth.credential.BaseCredential
import cn.rtast.kazure.v2.auth.provider.BaseAuthProvider

public typealias RoutingContext<T> = HttpRoutingContext<T>.() -> HttpResponse

internal fun <T> createRouting(
    route: String,
    methods: List<HttpMethod> = listOf(HttpMethod.GET),
    authLevel: AuthLevel = AuthLevel.ANONYMOUS,
    bindings: List<String> = listOf(),
    authProvider: BaseAuthProvider<T, BaseCredential>? = null,
    block: RoutingContext<T>,
): RoutingDelegate<T> = RoutingDelegate(
    route, methods, authLevel,
    bindings, authProvider, block
)
