/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/7
 */


package next

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.next.RequestHandler
import cn.rtast.kazure.next.annotations.Routing
import cn.rtast.kazure.next.at
import cn.rtast.kazure.next.handledBy
import cn.rtast.kazure.response.respondText

@Routing
val testRouting = (HttpMethod.GET) at "/api/hello" handledBy RequestHandler<String> { req, ctx, params ->
    req.respondText("")
}