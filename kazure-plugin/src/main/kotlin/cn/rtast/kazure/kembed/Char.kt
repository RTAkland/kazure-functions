/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/24/25
 */


package cn.rtast.kazure.kembed

fun String.sanitize(): String {
    var result = this.lowercase()
        .replace(Regex("[^a-z0-9_]"), "_")
    if (result.firstOrNull()?.isDigit() == true) {
        result = "_$result"
    }
    result = result.replace(Regex("_+"), "_")
    return result
}