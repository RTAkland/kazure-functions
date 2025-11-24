/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package v2

import cn.rtast.kazure.v2.createRouting
import cn.rtast.kazure.v2.respondText

fun testRouting(): Nothing = createRouting<Any>("api/") {
    respondText("123")
}