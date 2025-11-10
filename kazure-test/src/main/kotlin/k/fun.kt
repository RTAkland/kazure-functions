/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package k

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting

//class MyFunction : HttpAzureFunction<String>() {
//    //    @PathParam("name", String::class)
//    @AuthConsumer("bs")
//    @HttpRouting("time")
//    override fun httpEntrypoint(
//        request: HttpRequest<String>,
//        context: HttpContext,
//    ): HttpResponse {
//        val name = "RTAkladsand"
//        val res = Resources.readText("-s.dsadatxt")
//        val ssss = "21231"
//        return request.respondText(name + res + ssss)
//    }
//}
//

context(cred: BasicCredential)
@HttpRouting("hello/{s}")
@AuthConsumer(Basic1AuthProvider::class)
fun index(req: HttpRequest<String?>, ctx: HttpContext, @Param("s") s: String): HttpResponse {
    return req.respondText("Hello" + cred.username + " at $s")
}

//fun main() {
//    with(BasicCredential("", "")) {
//        index()
//    }
//}