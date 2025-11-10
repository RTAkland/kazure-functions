/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/10/25
 */


package cn.rtast.kazure.auth

import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class AuthConsumer(val provider: KClass<out AuthorizationConfigure<out BaseCredential>>)