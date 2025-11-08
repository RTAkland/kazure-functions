/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.trigger

import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.annotation.AuthorizationLevel

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class HttpRouting(
    val path: String,
    val methods: Array<HttpMethod> = [HttpMethod.GET],
    val authLevel: AuthorizationLevel = AuthorizationLevel.ANONYMOUS,
)