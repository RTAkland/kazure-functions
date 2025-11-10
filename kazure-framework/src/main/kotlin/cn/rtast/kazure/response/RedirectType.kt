/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.response

import com.microsoft.azure.functions.HttpStatus

public enum class RedirectType(
    public val code: Int,
    public val description: String,
    public val httpStatus: HttpStatus,
) {
    MOVED_PERMANENTLY(301, "Moved Permanently", HttpStatus.MOVED_PERMANENTLY),
    FOUND(302, "Found", HttpStatus.FOUND),
    SEE_OTHER(303, "See Other", HttpStatus.SEE_OTHER),
    TEMPORARY_REDIRECT(307, "Temporary Redirect", HttpStatus.TEMPORARY_REDIRECT),
    PERMANENT_REDIRECT(308, "Permanent Redirect", HttpStatus.PERMANENT_REDIRECT);
}