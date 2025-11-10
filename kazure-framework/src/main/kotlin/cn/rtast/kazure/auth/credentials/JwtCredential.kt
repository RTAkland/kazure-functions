/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.credentials

@JvmInline
public value class JwtCredential(public val token: String) : BaseCredential