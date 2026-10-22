/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.credentials

public data class BasicCredential(
    val username: String,
    val password: String,
) : BaseCredential