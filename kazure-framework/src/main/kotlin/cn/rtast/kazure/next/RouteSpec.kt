/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure


/**
 * The Router object, contains a function metadata
 * for generating azure functions
 */
public class RouteSpec(
    public val methods: List<HttpMethod>,
    public var route: String? = null,
    public val params: MutableSet<String> = mutableSetOf(),
    public var authProvider: AuthorizationConfigure<out BaseCredential>? = null,
)

/**
 * create a [RouteSpec]
 */
public infix fun HttpMethod.and(other: HttpMethod): RouteSpec =
    RouteSpec(listOf(this, other))


/**
 * create a [RouteSpec] with multiple [HttpMethod]
 */
public infix fun RouteSpec.and(other: HttpMethod): RouteSpec =
    RouteSpec(this.methods + other)

/**
 * specify route path
 */
public infix fun RouteSpec.at(route: String): RouteSpec {
    this.route = route
    return this
}

/**
 * set param
 */
public infix fun RouteSpec.with(param: String): RouteSpec {
    this.params += param
    return this
}

/**
 * set params at a time
 */
public infix fun RouteSpec.with(params: Set<String>): RouteSpec {
    this.params += params
    return this
}

/**
 * set auth basic provider
 */
public infix fun RouteSpec.requiring(auth: AuthorizationConfigure<out BaseCredential>): RouteSpec {
    this.authProvider = auth
    return this
}

/**
 * set routing handler
 */
public infix fun <T> RouteSpec.handledBy(handler: RequestHandler<T>): RegisteredRouteSpec<T> = RegisteredRouteSpec(
    route?.removePrefix("/") ?: error("Route must exists"),
    methods, params, authProvider, handler
)


