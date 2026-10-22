/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.response

import com.microsoft.azure.functions.HttpStatus

public enum class RedirectType(public val httpStatus: HttpStatus) {
    MOVED_PERMANENTLY(HttpStatus.MOVED_PERMANENTLY),
    FOUND(HttpStatus.FOUND),
    SEE_OTHER(HttpStatus.SEE_OTHER),
    TEMPORARY_REDIRECT(HttpStatus.TEMPORARY_REDIRECT),
    PERMANENT_REDIRECT(HttpStatus.PERMANENT_REDIRECT);
}