/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2.auth.credential

public data class BasicCredential(
    val username: String,
    val password: String,
) : BaseCredential